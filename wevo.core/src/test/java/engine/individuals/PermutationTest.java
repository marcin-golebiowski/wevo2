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

import engine.utils.JavaRandom;

/**
 * Tests for {@link Permutation}.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PermutationTest {

  /** Self-explanatory. */
  @Test
  public void testIndividualGenerator() {
    final int individualSize = 10;

    Permutation individual = Permutation.generate(
        new JavaRandom(), individualSize); 

    assertGenesFormPermutation(individual.getValues());
  }

  /**
   * Asserts that each gene from pool [0, individualSize) exists only once.
   * @param genes Chromosome to check.
   */
  private void assertGenesFormPermutation(final int[] genes) {
    final int chromosomeLength = genes.length;
    int[] genesCount = new int[chromosomeLength];
    for (int i = 0; i < chromosomeLength; i++) {
      genesCount[genes[i]]++; 
    }

    for (int i = 0; i < chromosomeLength; i++) {
      Assert.assertEquals(genesCount[i], 1);
    }
  }

  /**
   * Tests if hash code of an individual is exactly the same iff individuals
   * are equal.
   */
  @Test
  public void testHashCodeConformsEquals() {
    Permutation permutation1 = new Permutation(new int[]{0, 1, 2});
    Permutation permutation2 = new Permutation(new int[]{0, 1, 2});
    Permutation permutation3 = new Permutation(new int[]{1, 2, 0});

    Assert.assertEquals(permutation1.hashCode(), permutation2.hashCode());
    Assert.assertEquals(permutation1, permutation2);

    Assert.assertFalse(permutation1.hashCode() == permutation3.hashCode());
    Assert.assertFalse(permutation1.equals(permutation3));
  }
}
