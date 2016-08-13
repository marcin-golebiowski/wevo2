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
import org.testng.annotations.Test;

import engine.individuals.Permutation;
import engine.utils.WevoRandom;

/**
 * Tests for InversionMutation.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class InversionMutationTest {
  // MagicNumber off

  /** Mock control. */
  private final IMocksControl mockControl = EasyMock.createControl();

  /** Random number generator mock. */
  private WevoRandom generatorMock;

  /** Tests if single mutation works as expected. */
  @Test
  public void testSingleMutation() {

    final int chromosomeLength = 8;
    final Permutation originalIndividual =
        new Permutation(new int[]{0, 1, 2, 3, 4, 5, 6, 7});
    final Permutation expectedIndividual =
        new Permutation(new int[]{0, 1, 2, 7, 6, 5, 4, 3});

    mockControl.reset();
    generatorMock = mockControl.createMock(WevoRandom.class);

    EasyMock.expect(generatorMock.nextInt(0, chromosomeLength))
        .andReturn(3);
    EasyMock.expect(generatorMock.nextInt(0, chromosomeLength))
        .andReturn(7);

    InversionMutation mutation = new InversionMutation(generatorMock, 0.0);

    mockControl.replay();
    Permutation mutatedIndividual =
        mutation.mutate(originalIndividual);
    mockControl.verify();

    for (int i = 0; i < expectedIndividual.getSize(); i++) {
      Assert.assertEquals(
          mutatedIndividual.getValue(i),
          expectedIndividual.getValue(i));
    }
  }
}
