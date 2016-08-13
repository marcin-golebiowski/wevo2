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
 * Tests for {@link PhotoRulesIntervalAddMutation}.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRulesIntervalAddMutationTest {

    /** Mock controller. */
    private IMocksControl mockControl = EasyMock.createControl();

    /** Random generator mock. */
    private JavaRandom randomMock;

    /** Test adding interval. */
    @Test
    public void testAddInterval() {
      // MagicNumber off
      List<Integer> set = Arrays.asList(10, 20, 30, 40);
      createMocks();
      EasyMock.expect(randomMock.nextInt(0, 256)).andReturn(50);

      mockControl.replay();
      PhotoRulesIntervalAddMutation mutation = 
          new PhotoRulesIntervalAddMutation(randomMock, 40);
      List<Integer> mutatedSet = mutation.apply(set);
      mockControl.verify();

      // Check if old list is not affected
      Assert.assertEquals(set, Arrays.asList(10, 20, 30, 40));
      // Check values of new list 
      Assert.assertEquals(mutatedSet, Arrays.asList(10, 20, 30, 40, 30, 70));
      // MagicNumber on
    }

    /** Creates mock instances used in tests. */
    private void createMocks() {
      mockControl.reset();
      randomMock = mockControl.createMock(JavaRandom.class);
    }
}
