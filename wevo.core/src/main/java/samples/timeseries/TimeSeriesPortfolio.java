/*
 * Wevo2 - Distributed Evolutionary Computation Library.
 * Copyright (C) 2009 Marcin Brodziak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 *    Boston, MA  02110-1301  USA
 */
package samples.timeseries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A set of time series.
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class TimeSeriesPortfolio implements Cloneable {
 
  /** Map from time series to their quantities. */
  private final ArrayList<TimeSeries> timeSeries;

  /**
   * Creates a set of time series.
   * @param series List of time series to initialize portfolio with.
   */
  public TimeSeriesPortfolio(Collection<TimeSeries> series) {
    timeSeries = new ArrayList<TimeSeries>();
    for (TimeSeries s : series) {
      timeSeries.add(s);
    }
  }
 
  /** 
   * Returns a simplified representation of the portfolio, omits
   * items for which there are no values. 
   * @param weights Weights associated with consecutive stocks.
   * @return String representing the portfolio.
   */
  public String toStringWithOmittedZeroes(long[] weights) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < weights.length; i++) {
      if (sb.length() > 1) {
        sb.append(", ");
      }
      sb.append(timeSeries.get(i).getName() 
          + ": " + weights[i]);
    }
    sb.append("]");
    return sb.toString();
  }
 

  /** 
   * Returns the portfolio as a mathematical expression. In a way that
   * is useful for copying&pasting into programs like Matlab or R.
   * @param weights Weights associated with consecutive stocks.
   * @return String representing the portfolio as math expression.
   */
  public String toMathematicalExpression(long[] weights) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < weights.length; i++) {
      if (weights[i] == 0) {
        continue;
      }
      if (sb.length() > 1) {
        sb.append(" + ");
      }
      sb.append(timeSeries.get(i).getName() 
          + "*" + weights[i]);
    }
    return sb.toString();
  }
 
  /**
   * Returns a time series representing this portfolio.
   * @param weights Weights associated with consecutive stocks.
   * @return Time series representing this portfolio.
   */
  public TimeSeries toTimeSeries(long[] weights) {
    TimeSeries result = new TimeSeries("portfolio");
    for (long timestamp : timeSeries.get(0).getTimestamps()) {
      double value = 0.0;
      for (int weight = 0; weight < weights.length; weight++) {
        if (weights[weight] == 0) {
          continue;
        }
        value += weights[weight] * timeSeries.get(weight)
            .getValue(timestamp);
      }
      result.addValue(timestamp, value);
    }
    return result;
  }

  /**
   * Calculates what is the cost of transition of moving between two portfolios.
   * 
   * @param from Portfolio from which we're moving away.
   * @param to Portfolio we want to move to.
   * @param percentage Percentage broker charges for this transaction.
   * @return Cost of transition.
   */
  public double costOfTransition(long[] from, long[] to, double percentage) {
    double result = 0.0;
    long maxTimestamp = Collections.max(this.timeSeries.get(0).getTimestamps());
    for (int i = 0; i < from.length; i++) {
      long delta = Math.abs(from[i] - to[i]);
      result += delta * timeSeries.get(i).getValue(maxTimestamp) * percentage;
    }
    return result;
  }

  /**
   * Calculates difference between values of two portfolios.
   * 
   * @param from Portfolio from which we're moving away.
   * @param to Portfolio we want to move to.
   * @return Difference of values.
   */
  public double differenceOfValues(long[] from, long[] to) {
    return getValue(to) - getValue(from);
  }

  /**
   * Returns a number of timeseries in the portfolio.
   * @return Number of timeseries in the portfolio.
   */
  public long getSize() {
    return timeSeries.size();
  }

  /**
   * Returns weights for given description.
   * @param description Description of the portfolio.
   * @return Weights of subsequent stocks in the portfolio.
   */
  public long[] parseMathematicalExpression(String description) {
    // TODO (marcin.brodziak): I know someone will not like me for this method
    //   because it's usually me who complains that we shouldn't use hand
    //   made parsers. Still... this is trivial parsing, I hope.
    Map<String, Integer> map = new HashMap<String, Integer>();
    if (!"".equals(description)) {
      String[] weights = description.split("\\+");
      for (String weight : weights) {
        String[] w = weight.split("\\*");
        map.put(w[0].trim(), Integer.parseInt(w[1].trim()));
      }
    }
    long[] result = new long[timeSeries.size()];
    for (int i = 0; i < timeSeries.size(); i++) {
      if (map.containsKey(timeSeries.get(i).getName())) {
        result[i] = map.get(timeSeries.get(i).getName());
      }
    }
    return result;
  }

  /**
   * @param values Portfolio as vector.
   * @return Value of the portfolio.
   */
  public double getValue(long[] values) {
    double result = 0.0;
    long maxTimestamp = Collections.max(this.timeSeries.get(0).getTimestamps());
    for (int i = 0; i < values.length; i++) {
      // Percentages from transactions
      result += values[i] * timeSeries.get(i).getValue(maxTimestamp);
    }
    return result;
  }
}
