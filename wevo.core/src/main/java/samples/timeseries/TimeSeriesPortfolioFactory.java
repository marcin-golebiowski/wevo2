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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for TimeSeriesPortfolio.
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class TimeSeriesPortfolioFactory {

  /**
   * Reads set of time series from CSV file. For a file that is structured
   * as follows:
   * <pre>
   * DATE,SERIES1,SERIES2,
   * 20090101,1,2
   * 20090203,2,4
   * </pre>
   * two series will be read, SERIES1 and SERIES2. First column is 
   * always skipped, under assumption that it represents a date.
   * @param string Name of the file to read.
   * @return Portfolio initialized with time series from the file.
   * @throws IOException If the file cannot be read.
   */
  public static TimeSeriesPortfolio readCsvFile(String string) 
      throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(string));
    String line = reader.readLine();
    String[] shares = line.split(",");
    List<TimeSeries> timeSeries = new ArrayList<TimeSeries>();
    for (int i = 1; i < shares.length; i++) {
      timeSeries.add(new TimeSeries(shares[i]));
    }

    long lineNumber = 0;
    while ((line = reader.readLine()) != null) {
      String[] values = line.split(",");
      for (int i = 1; i < values.length; i++) {
        timeSeries.get(i - 1).addValue(lineNumber, 
            Double.parseDouble(values[i]));
      }
      lineNumber++;
    }

    return new TimeSeriesPortfolio(timeSeries);
  }

}
