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
package classifier.examples.operators;

import java.util.Arrays;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.Assert;
import org.testng.annotations.Test;

import engine.utils.JavaRandom;

/**
 * Tests for {@link PhotoRulesBordersMutation}.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRulesBordersMutationTest {

    /** Mock controller. */
    private IMocksControl mockControl = EasyMock.createControl();

    /** Random generator mock. */
    private JavaRandom randomMock;

    /** Test border shifting. */
    @Test
    public void testBorderShifting() {
      // MagicNumber off
      List<Integer> set = Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80);
      createMocks();
      EasyMock.expect(randomMock.nextDouble(0.0, 1.0)).andReturn(0.1);
      EasyMock.expect(randomMock.nextInt(0, 4)).andReturn(0);
      EasyMock.expect(randomMock.nextGaussian()).andReturn(-0.5);

      EasyMock.expect(randomMock.nextDouble(0.0, 1.0)).andReturn(0.1);
      EasyMock.expect(randomMock.nextInt(0, 4)).andReturn(1);
      EasyMock.expect(randomMock.nextGaussian()).andReturn(0.5);

      EasyMock.expect(randomMock.nextDouble(0.0, 1.0)).andReturn(0.1);
      EasyMock.expect(randomMock.nextInt(0, 4)).andReturn(2);
      EasyMock.expect(randomMock.nextGaussian()).andReturn(0.5);

      EasyMock.expect(randomMock.nextDouble(0.0, 1.0)).andReturn(0.1);
      EasyMock.expect(randomMock.nextInt(0, 4)).andReturn(3);
      EasyMock.expect(randomMock.nextGaussian()).andReturn(-0.5);
      EasyMock.expect(randomMock.nextGaussian()).andReturn(0.5);

      mockControl.replay();
      PhotoRulesBordersMutation mutation = new PhotoRulesBordersMutation(
          randomMock, 0.3, 30);
      List<Integer> mutatedSet = mutation.shiftBorders(set);
      mockControl.verify();

      // Check if old list is not affected
      Assert.assertEquals(set, Arrays.asList(10, 20, 30, 40, 50, 60, 70, 80));
      // Check values of new list 
      Assert.assertEquals(mutatedSet,
          Arrays.asList(-5, 20, 30, 55, 65, 75, 55, 95));
      // MagicNumber on
    }


    /** Creates mock instances used in tests. */
    private void createMocks() {
      mockControl.reset();
      randomMock = mockControl.createMock(JavaRandom.class);
    }
}
