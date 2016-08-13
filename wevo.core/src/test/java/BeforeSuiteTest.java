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

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.annotations.BeforeSuite;

/**
 * A piece of code contains all global settings for tests.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class BeforeSuiteTest {

  /** Sets up testing environment for all test. Disables logging. */
  @BeforeSuite
  public void globalSetUp() {
    for (Handler s : Logger.getLogger("").getHandlers()) {
      s.setLevel(Level.OFF);
    }
  }
}
