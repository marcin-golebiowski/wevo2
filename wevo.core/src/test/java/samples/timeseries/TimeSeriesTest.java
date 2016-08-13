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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test for {@link TimeSeries}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class TimeSeriesTest {
  // MagicNumber off

  /** Precision for tests. */ 
  private static final double PRECISION = 0.01;

  /** Tests time series construction. */
  @Test
  public void testTimeSeriesConstruction() {
    TimeSeries series = new TimeSeries("");
    series.addValue(1L, 10.0);
    Assert.assertEquals(series.getValue(1L), 10.0, PRECISION);
  }

  /** Tests calculating min, max and mean values for time series. */
  @Test
  public void testTimeSeriesMinMaxMean() {
    TimeSeries series = new TimeSeries("");
    series.addValue(1L, 10);
    series.addValue(3L, 20);
    series.addValue(2L, 15);
    Assert.assertEquals(series.getMin(), 10.0, PRECISION);
    Assert.assertEquals(series.getMax(), 20.0, PRECISION);
    Assert.assertEquals(series.getMean(), 15.0, PRECISION);
  }

  /** Tests calculating time series trend. */
  @Test
  public void testTimeSeriesTrend() {
    TimeSeries series = new TimeSeries("");
    series.addValue(1L, 2);
    series.addValue(2L, 4);
    series.addValue(3L, 6);
    Assert.assertEquals(series.getSlope(), 2.0, PRECISION);
    Assert.assertEquals(series.getIntercept(), 0.0, PRECISION);
  }

  /** Tests time series statistics. */
  @Test
  public void testTimeSeriesStats() {
    TimeSeries series = new TimeSeries("");
    series.addValue(1L, 2);
    series.addValue(2L, 4);
    series.addValue(3L, 6);
    Assert.assertEquals(series.getMean(), 4.0, PRECISION);
    Assert.assertEquals(series.getStandardDeviation(), 2.0, PRECISION);
    Assert.assertEquals(series.getTrendCorrectedStandardDeviation(),
        0, PRECISION);
  }

  /** Tests time series statistics with different values. */
  public void testTimeSeriesStats2() {
    TimeSeries series = new TimeSeries("");
    series.addValue(1L, 2);
    series.addValue(2L, 5);
    series.addValue(3L, 6);
    Assert.assertEquals(series.getMean(), 4.0, PRECISION);
    Assert.assertEquals(series.getStandardDeviation(), 2.0, PRECISION);
    Assert.assertEquals(series.getTrendCorrectedStandardDeviation(),
        0, PRECISION);
  }

  // MagicNumber on
}
