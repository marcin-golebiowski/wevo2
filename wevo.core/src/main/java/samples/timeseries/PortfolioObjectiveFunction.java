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

import engine.ObjectiveFunction;
import engine.individuals.NaturalVector;

/**
 * An objective function that measures quality of portfolio. Natural
 * number vector is used to represent the number of shares in a stock
 * portfolio. I-th number in the individual represents the number of
 * shares of i-th stock in given portfolio.
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class PortfolioObjectiveFunction 
    implements ObjectiveFunction<NaturalVector> {
 
  /** Portfolio used as a basis for the objective function. */
  private final TimeSeriesPortfolio portfolio;
 
  /** Expected number of different kinds of stocks in portfolio. */
  private final int expectedNumberOfStocks;

  /** Portfolio will never be bigger than expectedNumberOfStocks +/- this. */
  private final int maximumStocksNumberError;

  /** A base portfolio against which new one is measured. */
  private final long[] basePortfolio;

  /** Maximum amount of money user is willing to pay to improve portfolio. */
  private final double maxPortfolioValueChange;

  /**
   * Creates the objective function.
   * @param portfolio Portfolio we're basing on.
   * @param expectedNumberOfStocks Expected number of different stocks 
   *     in portfolio.
   * @param maximumStocksNumberError Maximum error in portfolio size.
   * @param basePortfolio Base portfolio against which new one is measured.
   * @param maxPortfolioValueChange2 Maximum amount of money we agree to pay
   *     when switching to a better portfolio.
   */
  public PortfolioObjectiveFunction(TimeSeriesPortfolio portfolio,
      int expectedNumberOfStocks, int maximumStocksNumberError,
      long[] basePortfolio, double maxPortfolioValueChange2) {
    this.portfolio = portfolio;
    this.expectedNumberOfStocks = expectedNumberOfStocks;
    this.maximumStocksNumberError = maximumStocksNumberError;
    this.basePortfolio = basePortfolio;
    this.maxPortfolioValueChange = maxPortfolioValueChange2;
  }
 
  /** {@inheritDoc}. */
  public double compute(NaturalVector individual) {
    // TODO (marcin.brodziak): get back to unit tests
    TimeSeries series = portfolio.toTimeSeries(
        individual.getValues());
    double differenceOfValues = basePortfolio == null ? 0 
        : portfolio.differenceOfValues(basePortfolio, individual.getValues());
    if (differenceOfValues > maxPortfolioValueChange) {
      return 0;
    }
    long numberOfShares = numberOfShares(individual);
    if (numberOfShares == 0) {
      return 0;
    }
    long nos = numberOfStocks(individual);
    double numberOfStocksMultiplier = 
        Math.abs(nos - this.expectedNumberOfStocks) 
            > maximumStocksNumberError ? 0 : 1;
    double returnRiskRatio = series.getSlope() 
        / series.getTrendCorrectedStandardDeviation();
    if (returnRiskRatio == Double.NaN) {
      return 0;
    }
    return returnRiskRatio * numberOfStocksMultiplier;
  }

  /**
   * Compute the number of shares (of all stocks) in the portfolio.
   * @param individual Individual representing the portfolio.
   * @return Number of shares (of all stocks) int the portfolio.
   */
  private long numberOfShares(NaturalVector individual) {
    long sumOfShareCounts = 0;
    for (int i = 0; i < individual.getSize(); i++) {
      sumOfShareCounts += individual.getValue(i);
    }
    return sumOfShareCounts;
  }
 
  /**
   * Compute the number of different stocks in the portfolio.
   * @param individual Individual representing the portfolio.
   * @return Number of different stocks in the portfolio.
   */
  private long numberOfStocks(NaturalVector individual) {
    long numberOfStocks = 0;
    for (int i = 0; i < individual.getSize(); i++) {
      if (individual.getValue(i) != 0) {
        numberOfStocks++;
      }
    }
    return numberOfStocks;
  }
}
