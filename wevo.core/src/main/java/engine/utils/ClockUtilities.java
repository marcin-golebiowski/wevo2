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
package engine.utils;

import java.util.GregorianCalendar;

/**
 * Class for gathering clock utilities.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class ClockUtilities {

  /**
   * Returns timespan between two dates in seconds.
   * @param earlier Earlier date.
   * @param later Later date.
   * @return Length of timespan in seconds.
   */
  public static double getTimeSpanInSeconds(GregorianCalendar earlier,
      GregorianCalendar later) {
    long earlierMillis = earlier.getTimeInMillis();
    long laterMillis = later.getTimeInMillis();

    // MagicNumber off
    return ((double) (laterMillis - earlierMillis)) / 1000.0;
    // MagicNumber on
}

  /**
   * Returns timespan between given date and actual time (time of method call).
   * @param earlier Earlier date.
   * @return Length of timespan in seconds.
   */
  public static double getTimeSpanInSeconds(GregorianCalendar earlier) {
    return ClockUtilities.getTimeSpanInSeconds(earlier,
        new GregorianCalendar());
  }
}
