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
package engine.operators.permutation;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.Assert;

import engine.individuals.Permutation;
import engine.utils.WevoRandom;

/**
 * Tests for TranspositionMutation.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class TranspositionMutationTest {

  /** Random number generator mock. */
  private WevoRandom generatorMock;

  /** Mock control. */
  private final IMocksControl mockControl = EasyMock.createControl();

  /** Tests if mutation operator works as expected. */
  public void testSuccesfullMutation() {
    // MagicNumber off

    final int transposedPosition1 = 5;
    final int transposedPosition2 = 7;
    Permutation originalIndividual = new Permutation(
        new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9});

    generatorMock = EasyMock.createMock(WevoRandom.class);
    EasyMock.expect(generatorMock.nextInt(0, originalIndividual.getSize()))
        .andReturn(transposedPosition1);
    EasyMock.expect(generatorMock.nextInt(0, originalIndividual.getSize()))
        .andReturn(transposedPosition2);

    TranspositionMutation mutation =
      new TranspositionMutation(generatorMock, 0.5f);

    mockControl.replay();
    Permutation mutatedIndividual =
        mutation.mutate(originalIndividual);
    mockControl.reset();

    for (int i = 0; i < originalIndividual.getSize(); i++) {
      if (i == transposedPosition1) {
        Assert.assertEquals(
            mutatedIndividual.getValue(transposedPosition1),
            originalIndividual.getValue(transposedPosition2));
        continue;
      }

      if (i == transposedPosition2) {
        Assert.assertEquals(
            mutatedIndividual.getValue(transposedPosition1),
            originalIndividual.getValue(transposedPosition2));
        continue;
      }

      Assert.assertEquals(
          mutatedIndividual.getValue(i), originalIndividual.getValue(i));
    }
    // MagicNumber on
  }
}
