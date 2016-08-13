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

import junit.framework.Assert;

import org.testng.annotations.Test;

/** Tests for {@link BinaryVector}. */
public class BinaryVectorTest {

  /** Tests if initial individual is filled with zeros only. */
  @Test
  public void testZeroedAtStart() {
    final int size = 3;
    BinaryVector bi = new BinaryVector(size);
    Assert.assertEquals(bi.getSize(), size);
    for (int i = 0; i < bi.getSize(); i++) {
      Assert.assertEquals(false, bi.getBit(i));
    }
  }

  /** Self-explanatory. */
  @Test
  public void testSettingFromArray() {
    BinaryVector bi = new BinaryVector(new boolean[]{true, false});
    Assert.assertEquals(2, bi.getSize());
    Assert.assertEquals(true, bi.getBit(0));
    Assert.assertEquals(false, bi.getBit(1));
  }

  /** Self-explanatory. */
  @Test
  public void testSetBit() {
    BinaryVector bi = new BinaryVector(new boolean[]{true, false});
    bi.setBit(0, false);
    Assert.assertEquals(false, bi.getBit(0));

    bi.negateBit(1);
    Assert.assertEquals(true, bi.getBit(1));
  }

  /** Self-explanatory. */
  @Test
  public void testToString() {
    BinaryVector bi = new BinaryVector(new boolean[]{true, false, true});
    Assert.assertEquals("101", bi.toString());
  }

  /**
   * Tests if hash code of an individual is exactly the same iff individuals
   * are equal.
   */
  @Test
  public void testHashCodeConformsEquals() {
    BinaryVector binary1 = new BinaryVector(new boolean[]{true, true, true});
    BinaryVector binary2 = new BinaryVector(new boolean[]{true, true, true});
    BinaryVector binary3 = new BinaryVector(new boolean[]{true, false, true});

    Assert.assertEquals(binary1.hashCode(), binary2.hashCode());
    Assert.assertEquals(binary1, binary2);

    Assert.assertFalse(binary1.hashCode() == binary3.hashCode());
    Assert.assertFalse(binary1.equals(binary3));
  }
}
