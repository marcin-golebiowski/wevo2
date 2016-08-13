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
package engine.distribution.master;

import engine.utils.SystemClock;
import engine.utils.WevoClock;

/**
 * Represents slave status and holds information
 * about last contact. There should be one-to-one
 * mapping from SlaveId to SlaveInformation 
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class SlaveInformation {

  /** Timestamp of last slave contact. */
  private long lastContactTimestamp;

  /** Time measurement utility. */
  private WevoClock clock;

  /**
   * Constructor. Package visibility for testing purposes.
   * @param clock Time measurement utility.
   * @param lastContactTimestamp Last contact timestamp.
   */
  SlaveInformation(final WevoClock clock, final long lastContactTimestamp) {
    this.clock = clock;
    this.lastContactTimestamp = lastContactTimestamp;
  }

  /**
   * Constructor.
   * @param newClock Clock for time measurements. Must not be null.
   */
  public SlaveInformation(final WevoClock newClock) {
    this(newClock, newClock.getCurrentTimeMillis());
  }

  /**
   * Constructor. 
   * @param lastContactTimeStamp Time of the last contact.
   */
  public SlaveInformation(final long lastContactTimeStamp) {
    this(new SystemClock(), lastContactTimeStamp);
  }

  /** Constructor. */
  public SlaveInformation() {
    this(new SystemClock());
  }

  /**
   * Getter for last contact timestamp.
   * @return Timestamp of last contact 
   * (measured since epoch)
   */
  public long getLastContactTimestamp() {
    return lastContactTimestamp;
  }

  /**
   * Updates last contact timestamp.
   */
  public void updateLastContactTimestamp() {
    lastContactTimestamp = clock.getCurrentTimeMillis();
  }
}
