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

import java.util.Date;

import junit.framework.Assert;

import org.testng.annotations.Test;

/**
 * Tests for SlaveInformation.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class SlaveInformationTest {

  /**
   * Self-explanatory.
   * @throws InterruptedException Never thrown.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void updatingTimestampTest() throws InterruptedException {
    // MagicNumber off
    // sets last slave contact at 4.05.2009
    SlaveInformation slaveInfo =
      new SlaveInformation(new Date(109, 4, 4).getTime());
    long oldTimestamp = slaveInfo.getLastContactTimestamp();

    // update timestamp
    slaveInfo.updateLastContactTimestamp();
    long newTimestamp = slaveInfo.getLastContactTimestamp();
    Assert.assertTrue(oldTimestamp < newTimestamp);
    // MagicNumber on
  }
}
