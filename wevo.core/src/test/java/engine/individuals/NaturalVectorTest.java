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
package engine.individuals;

import org.testng.Assert;
import org.testng.annotations.Test;

/** 
 * Tests for {@link NaturalVector}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class NaturalVectorTest {

  /** Self-explanatory. */
  @Test
  public void testSetters() {
    // MagicNumber off 
    NaturalVector vector = new NaturalVector(2);
    Assert.assertEquals(vector.getValue(0), 0);
    Assert.assertEquals(vector.getValue(1), 0);
    vector.setValue(0, 1);
    vector.setValue(1, 3);
    Assert.assertEquals(vector.getValue(0), 1);
    Assert.assertEquals(vector.getValue(1), 3);
    // MagicNumber on
  }

  /**
   * Tests if hash code of an individual is exactly the same iff individuals
   * are equal.
   */
  @Test
  public void testHashCodeConformsEquals() {
    NaturalVector natural1 = new NaturalVector(new long[]{0, 1, 2});
    NaturalVector natural2 = new NaturalVector(new long[]{0, 1, 2});
    NaturalVector natural3 = new NaturalVector(new long[]{1, 2, 0});

    Assert.assertEquals(natural1.hashCode(), natural2.hashCode());
    Assert.assertEquals(natural1, natural2);

    Assert.assertFalse(natural1.hashCode() == natural3.hashCode());
    Assert.assertFalse(natural1.equals(natural3));
  }
}
