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

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

/**
 * Test for {@link TimeSeries}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class TimeSeriesPortfolioTest {
  // MagicNumber off

  /** Number of values in series. */
  private static final int VALUES_IN_SERIES = 3;

  /** Series of ones. */
  private TimeSeries seriesOnes;

  /** Series of twos. */
  private TimeSeries seriesTwos;

  /** Series of fives. */
  private TimeSeries seriesFives;
 
  /** Sets up testing environment. */
  @BeforeTest
  public void setUp() {
    seriesOnes = new TimeSeries("ones");
    seriesTwos = new TimeSeries("twos");
    seriesFives = new TimeSeries("fives");
    for (long i = 0; i < VALUES_IN_SERIES; i++) {
      seriesOnes.addValue(i, 1.0);
      seriesTwos.addValue(i, 2.0);
      seriesFives.addValue(i, 5.0);
    }
  }

  /** Tests converstion to time series. */
  @Test
  public void testConvertToTimeSeries() {
    List<TimeSeries> series = new LinkedList<TimeSeries>();
    series.add(seriesTwos);
    series.add(seriesOnes);
    TimeSeriesPortfolio portfolio = new TimeSeriesPortfolio(series);

    equalsInValueWithPrecision(portfolio.toTimeSeries(new long[]{2, 1}), 
        seriesFives, 0.01);
  }

  /** Test updating from list. */
  @Test
  public void testPrintingString() {
    List<TimeSeries> series = new LinkedList<TimeSeries>();
    series.add(seriesTwos);
    series.add(seriesOnes);
    TimeSeriesPortfolio portfolio = new TimeSeriesPortfolio(series);

    Assert.assertEquals(portfolio.toStringWithOmittedZeroes(new long[]{7, 1}),
        "[twos: 7, ones: 1]");
    Assert.assertEquals(portfolio.toMathematicalExpression(new long[]{5, 3}),
        "twos*5 + ones*3");
  }

  /** Tests getting time series for given amounts. */
  @Test
  public void testTimeSeriesFromSetOfWeights() {
    List<TimeSeries> series = new LinkedList<TimeSeries>();
    series.add(seriesTwos);
    series.add(seriesOnes);
    TimeSeriesPortfolio portfolio = new TimeSeriesPortfolio(series);

    TimeSeries result = portfolio.toTimeSeries(new long[]{2, 1});
    equalsInValueWithPrecision(result, seriesFives, 0.01);
  }
 
  /** 
   * Tests whether transfering how expensive is transferring from one portfolio
   * to another. 
   */
  @Test
  public void testCostEstimate() {
    List<TimeSeries> series = new LinkedList<TimeSeries>();
    series.add(seriesTwos);
    series.add(seriesOnes);
    series.add(seriesFives);
    TimeSeriesPortfolio portfolio = new TimeSeriesPortfolio(series);
    Assert.assertEquals(portfolio.costOfTransition(new long[] {1, 0, 0},
        new long[] {2, 1, 0}, 0.05), 0.15, 0.001);
  }

  /** Tests difference of values. */
  //@Test
  public void testDifferenceOfPrices() {
    List<TimeSeries> series = new LinkedList<TimeSeries>();
    series.add(seriesTwos);
    series.add(seriesOnes);
    series.add(seriesFives);
    TimeSeriesPortfolio portfolio = new TimeSeriesPortfolio(series);
    Assert.assertEquals(portfolio.differenceOfValues(new long[] {1, 0, 0},
        new long[] {2, 1, 0}), 3.00, 0.001);
  }
 
  /**
   * Tests parsing of time series.
   * @throws IOException Thrown on reading errors.
   */
  @Test
  public void testTimeSeriesParsing() throws IOException {
    TimeSeriesPortfolio portfolio = 
      TimeSeriesPortfolioFactory.readCsvFile(
          "testData/simpleTimeSeriesSet.csv");
    String description = "NATURAL*3 + SQUARED*1";
    long[] weights = portfolio.parseMathematicalExpression(
        description);
    Assert.assertEquals(portfolio.toMathematicalExpression(weights),
        description);
  }

  /**
   * Helper method for comapring time series with given precision.
   * @param series1 First series to compare.
   * @param series2 Second series to compare.
   * @param precision Precision of comparison.
   */
  public static void equalsInValueWithPrecision(TimeSeries series1,
      TimeSeries series2, double precision) {
    for (long i = 0; i < VALUES_IN_SERIES; i++) {
      Assert.assertEquals(series1.getValue(i),
          series2.getValue(i), precision);
    }
  }
  // MagicNumber on
}
