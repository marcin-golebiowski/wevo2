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

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for {@link JavaRandom}.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class JavaRandomTest {
  // MagicNumber off

  /** Tests whether generator returns non-constant values. */
  @Test
  public void testNextBoolean() {
    JavaRandom generator = new JavaRandom(0);
    Assert.assertTrue(generator.nextBoolean());
    Assert.assertTrue(generator.nextBoolean());
    Assert.assertFalse(generator.nextBoolean());
  }

  /**
   * Tests whether random double stays within given limits.
   * Since we are not able to check all possibilites, this
   * test becomes non-deterministic.
   */
  @Test
  public void testNextDoubleIsWithinLimits() {
    JavaRandom generator = new JavaRandom();

    final double pick = generator.nextDouble(1.0, 2.5);
    Assert.assertTrue(pick >= 1.0 && pick <= 2.5);
  }

  /**
   * Tests whether random integer stays within given limits.
   * Since we are not able to check all possibilites, this
   * test becomes non-deterministic.
   */
  @Test
  public void testNextIntIsWithinLimits() {
    JavaRandom generator = new JavaRandom();

    final int pick = generator.nextInt(1, 10);
    Assert.assertTrue(pick >= 1 && pick < 10);
  }

  /**
   * Tests whether random long stays within given limits.
   * Since we are not able to check all possibilites, this
   * test becomes non-deterministic.
   */
  @Test
  public void testNextLongIsWithinLimits() {
    JavaRandom generator = new JavaRandom();

    final long pick = generator.nextLong(1100, 26000);
    Assert.assertTrue(pick >= 1100 && pick < 26000);
  }

  // MagicNumber on
}
