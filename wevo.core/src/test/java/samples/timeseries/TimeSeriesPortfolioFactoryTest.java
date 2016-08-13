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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for {@link TimeSeriesPortfolioFactory}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class TimeSeriesPortfolioFactoryTest {
  // MagicNumber off

  /**
   * Test if portfolio is constructed properly from a file.
   * @exception IOException Thrown on IO errors.
   */
  @Test
  public void testSimpleFile() throws IOException {
    TimeSeriesPortfolio portfolio = 
        TimeSeriesPortfolioFactory.readCsvFile(
            "testData/simpleTimeSeriesSet.csv");
    Assert.assertEquals(portfolio.toTimeSeries(new long[]{1, 2})
        .getValue(0L), 3.0);
  }
  // MagicNumber on
}
