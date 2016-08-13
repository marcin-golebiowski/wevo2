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

import java.util.List;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import engine.individuals.Permutation;
import engine.utils.WevoRandom;

/**
 * Class testing PMXCrossover.
 * 
 * @author Szymek Fogiel (szymek.fogiel@gmail.com)
 * @author Karol Asgaroth Stosiek (szymek.fogiel@gmail.com)
 *
 */
public class PMXCrossoverTest {

  /** Length of the chromosome. */
  private final int chromosomeLength = 9;

  /** Beginning of the segment. */
  private final int segmentBeginning = 3;

  /** Segment length. */
  private final int segmentLength = 3;

  /** First parent used in tests. */
  private Permutation parent1;

  /** Second parent used in tests. */
  private Permutation parent2;

  /** Tested instance. */
  private PMXCrossover crossover;

  /** Random number generator mock. */
  private WevoRandom generatorMock;

  /** Mock control. */
  private final IMocksControl mockControl = EasyMock.createControl();

  /** Sets up testing environment. */
  @BeforeMethod
  public void setUp() {
    mockControl.reset();
    generatorMock = EasyMock.createMock(WevoRandom.class);

    // MagicNumber off
    parent1 = new Permutation(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
    parent2 = new Permutation(new int[] {4, 1, 2, 8, 7, 6, 9, 3, 5});
    // MagicNumber on

    Assert.assertEquals(parent1.getValues().length, chromosomeLength);
    Assert.assertEquals(parent2.getValues().length, chromosomeLength);
  }

  /** Tests if crossover works as expected. */
  @Test
  public void testSuccessfulCrossover() {
    crossover = new PMXCrossover(
        generatorMock, segmentBeginning, segmentLength);

    mockControl.replay();
    List<Permutation> children = crossover.combine(parent1, parent2);
    mockControl.verify();

    Permutation child1 = children.get(0);
    Permutation child2 = children.get(1);

    // MagicNumber off
    long[] properChild1Chromosome = new long[] {1, 2, 3, 8, 7, 6, 5, 4, 9};
    long[] properChild2Chromosome = new long[] {8, 1, 2, 4, 5, 6, 9, 3, 7};
    // MagicNumber on

    for (int i = 0; i < chromosomeLength; i++) {
      Assert.assertEquals(child1.getValue(i), properChild1Chromosome[i]);
      Assert.assertEquals(child2.getValue(i), properChild2Chromosome[i]);
    }
  }
}
