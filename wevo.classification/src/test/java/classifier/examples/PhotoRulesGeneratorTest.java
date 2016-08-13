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
package classifier.examples;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.Assert;
import org.testng.annotations.Test;

import classifier.Rule;
import engine.utils.JavaRandom;

/**
 * Tests for {@link PhotoRulesGenerator}.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRulesGeneratorTest {

    /** Mock controller. */
    private IMocksControl mockControl = EasyMock.createControl();

    /** Random generator mock. */
    private JavaRandom randomMock;

    /**
     * Test random rule generation.
     */
    @Test
    public void testRuleGeneration() {
      // MagicNumber off
      createMocks();
      EasyMock.expect(randomMock.nextInt(0, 256)).andReturn(10);
      EasyMock.expect(randomMock.nextInt(0, 256)).andReturn(20);
      EasyMock.expect(randomMock.nextInt(0, 256)).andReturn(30);
      EasyMock.expect(randomMock.nextInt(0, 256)).andReturn(40);
      EasyMock.expect(randomMock.nextInt(0, 256)).andReturn(50);
      EasyMock.expect(randomMock.nextInt(0, 256)).andReturn(60);

      mockControl.replay();
      PhotoRulesGenerator generator = new PhotoRulesGenerator(
          randomMock, 1, 3);
      Rule<List<Integer>, SatellitePhotoCategory> rule =
          generator.generateRandomRule(
              new SatellitePhotoCategory(Color.black, "name"));
      mockControl.verify();

      List<List<Integer>> ranges = new ArrayList<List<Integer>>();
      ranges.add(Arrays.asList(10, 20));
      ranges.add(Arrays.asList(30, 40));
      ranges.add(Arrays.asList(50, 60));
      Assert.assertEquals(
          ((PhotoRuleCondition) rule.getCondition()).getSets(), ranges);
      // MagicNumber on
    }

    /** Creates mock instances used in tests. */
    private void createMocks() {
      mockControl.reset();
      randomMock = mockControl.createMock(JavaRandom.class);
    }
}
