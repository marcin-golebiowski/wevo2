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
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import engine.individuals.NaturalVector;

/**
 * Test for portfolio objective function ({@link PortfolioObjectiveFunction}).
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class PortfolioObjectiveFunctionTest {
  // Magic Number off

  /** Tested instance. */
  private TimeSeriesPortfolio timeSeriesPortfolio;

  /** Self-explanatory.*/
  @BeforeTest
  public void setUp() {
    List<TimeSeries> timeSeries = new ArrayList<TimeSeries>();

    TimeSeries ts = new TimeSeries("ones");
    ts.addValue(1L, 1.0);
    ts.addValue(2L, 14.0);
    ts.addValue(3L, 22.0);
    ts.addValue(4L, 50.0);
    timeSeries.add(ts);

    timeSeriesPortfolio = new TimeSeriesPortfolio(timeSeries);
  }

  /** Self-explanatory. */
  @Test
  public void testZeroForTooLargeCost() {
    PortfolioObjectiveFunction function = new PortfolioObjectiveFunction(
        timeSeriesPortfolio,
        1, 0, new long[]{0}, 100);
    Assert.assertEquals(
        function.compute(new NaturalVector(new long[]{3})), 
        0.0, 0.001);
  }

  /** Self-explanatory. */
  @Test
  public void testNonZeroForGoodCost() {
    PortfolioObjectiveFunction function = new PortfolioObjectiveFunction(
        timeSeriesPortfolio,
        1, 0, new long[]{1}, 51);
    Assert.assertEquals(
        function.compute(new NaturalVector(new long[]{2})), 
        2.8700423092949094, 0.001);
    Assert.assertEquals(
        function.compute(new NaturalVector(new long[]{3})), 
        0, 0.001);
  }

  // Magic Number on
}
