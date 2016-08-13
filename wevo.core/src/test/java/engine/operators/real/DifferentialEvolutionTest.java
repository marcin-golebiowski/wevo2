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
package engine.operators.real;

import org.testng.annotations.Test;

import engine.Population;
import engine.individuals.RealVector;
import engine.utils.ListUtils;

/**
 * Test for {@link DifferentialEvolution}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class DifferentialEvolutionTest {

  /**
   * Tests if differential evolution operator works according
   * to it's contract.
   */
  @Test
  public void testDifferentialEvolution() {
    // MagicNumber off
    RealVector v1 = new RealVector(new double[]{0.0, 1.0});
    RealVector v2 = new RealVector(new double[]{0.0, 1.0});
    RealVector v3 = new RealVector(new double[]{1.0, 0.0});
    Population<RealVector> population = new Population<RealVector>();
    population.addIndividual(v1);
    population.addIndividual(v2);
    population.addIndividual(v3);
 
    population = new DifferentialEvolution(3).apply(population);

    assertRealVectorIndividualsEqual(population.getIndividuals().get(0),
        new RealVector(new double[] {-3.0, 4.0}));
    assertRealVectorIndividualsEqual(population.getIndividuals().get(1),
        new RealVector(new double[] {3.0, -2.0}));
    assertRealVectorIndividualsEqual(population.getIndividuals().get(2),
        new RealVector(new double[] {1.0, 0.0}));
    // MagicNumber on
  }

  /**
   * Checks whether vectors of real number are equal with 10e-4 accuracy.
   * @param v1 First vector to be compared; must not be null.
   * @param v2 Second vector to be compared; must not be null.
   */
  private void assertRealVectorIndividualsEqual(RealVector v1, RealVector v2) {
    // MagicNumber off
    ListUtils.compareArraysOfDoubles(v1.getValues(), v2.getValues(), 10e-4);
    // MagicNumber on
  }
}
