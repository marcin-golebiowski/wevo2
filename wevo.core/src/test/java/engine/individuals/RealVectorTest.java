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

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.Assert;
import org.testng.annotations.Test;

import engine.utils.JavaRandom;

/** 
 * Tests for {@link RealVector}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class RealVectorTest {

  /** Mock controller. */
  private IMocksControl mockControl = EasyMock.createControl();

  /** Mock of the random number generator. */
  private JavaRandom randomMock;

  //MagicNumber off 

  /** Self-explanatory. */
  @Test
  public void testSetters() {
    RealVector vector = new RealVector(2);
    Assert.assertEquals(vector.getValue(0), 0.0);
    Assert.assertEquals(vector.getValue(1), 0.0);
    vector.setValue(0, 1);
    vector.setValue(1, 3);
    Assert.assertEquals(vector.getValue(0), 1.0);
    Assert.assertEquals(vector.getValue(1), 3.0);
  }

  /**
   * Tests if hash code of an individual is exactly the same iff individuals
   * are equal.
   */
  @Test
  public void testHashCodeConformsEquals() {
    RealVector real1 = new RealVector(new double[]{0.0, 1.1, 2.2});
    RealVector real2 = new RealVector(new double[]{0.0, 1.1, 2.2});
    RealVector real3 = new RealVector(new double[]{1.0, 2.0, 0.0});

    Assert.assertEquals(real1.hashCode(), real2.hashCode());
    Assert.assertEquals(real1, real2);

    Assert.assertFalse(real1.hashCode() == real3.hashCode());
    Assert.assertFalse(real1.equals(real3));
  }

  /**
   * Test for individual generation.
   */
  @Test
  public void testIndividualGenerator() {
    createMock();

    for (int i = 0; i < 10; i++) {
      EasyMock.expect(randomMock.nextDouble(0, 100)).andReturn((double) i);
    }

    mockControl.replay();
    RealVector real = RealVector.generate(randomMock, 10, 0, 100);
    mockControl.verify();

    for (int i = 0; i < 10; i++) {
      Assert.assertEquals((double) i, real.getValue(i));
    }
  }
  // MagicNumber on

  /** Creates mock instances used in tests. */
  private void createMock() {
    mockControl.reset();

    randomMock = mockControl.createMock(JavaRandom.class);
  }
}
