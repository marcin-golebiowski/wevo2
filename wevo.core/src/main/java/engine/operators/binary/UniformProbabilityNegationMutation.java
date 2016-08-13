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
package engine.operators.binary;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import engine.Operator;
import engine.Population;
import engine.individuals.BinaryVector;
import engine.utils.WevoRandom;

/**
 * Performs mutation of the individual by randomly negating one 
 * bit with some probability.
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 */
public class UniformProbabilityNegationMutation 
    implements Operator<BinaryVector> {
 
  /** For each bit, probability of negating it. */
  private final double mutationProbability;
 
  /** Random number generator. */
  private final WevoRandom random;

  /**
   * Returns a mutator, which modifies each bit of the {@link BinaryVector}
   * negated with some probability.
   * @param mutationProbability Probability of the mutation of each bit 
   *     of the individual.
   * @param random Random number generator.
   */
  public UniformProbabilityNegationMutation(double mutationProbability,
        final WevoRandom random) {
    this.mutationProbability = mutationProbability;
    this.random = random;
  }


  /** {@inheritDoc} */
  public Population<BinaryVector> 
      apply(Population<BinaryVector> population) {
    Iterator<BinaryVector> iterator = population.getIndividuals().iterator();
    List<BinaryVector> output = new LinkedList<BinaryVector>();
 
    while (iterator.hasNext()) {
      BinaryVector binaryIndividual = iterator.next();
      if (random.nextDouble(0.0, 1.0) < mutationProbability) {
        binaryIndividual.negateBit(
            random.nextInt(0, binaryIndividual.getSize()));
      } 
      output.add(binaryIndividual);
    }
    return population;
  }

}
