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

import engine.Operator;
import engine.Population;
import engine.individuals.Permutation;
import engine.utils.WevoRandom;

/**
 * A mutation for individuals representing permutations.
 * Two random elements of permutation will be transposed with given probability.
 * 
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * 
 * Previous version: Donata Malecka, Piotr Baraniak.
 */
public class TranspositionMutation
    implements Operator<Permutation> {

  /** Probability that a mutation will happen to individual. */
  private double mutationProbability;

  /** Random number generator. */
  private WevoRandom randomGenerator;

  /**
   * Constructor.
   * @param generator Random number generator.
   * @param probability Probability of a individual mutation.
   */
  public TranspositionMutation(
      final WevoRandom generator,
      final double probability) {
    this.randomGenerator = generator;
    this.mutationProbability = probability;
  }

  /** {@inheritDoc} */
  public Population<Permutation> apply(Population<Permutation> population) {
    Population<Permutation> result = new Population<Permutation>();

    for (Permutation individual : population.getIndividuals()) {
      if (randomGenerator.nextDouble(0.0, 1.0) >= mutationProbability) {
        result.addIndividual(individual);
      } else {
        result.addIndividual(mutate(individual));
      }
    }

    return result;
  }

  /**
   * Mutates given individual. Package-visibility for testing.
   * @param individual Individual to mutate. Stays intact.
   * @return Mutated individual.
   */
  Permutation mutate(Permutation individual) {
    final Permutation mutatedIndividual = new Permutation(individual);

    final int i = randomGenerator.nextInt(0, individual.getSize());
    final int j = randomGenerator.nextInt(0, individual.getSize());

    mutatedIndividual.transpose(i, j);
    return mutatedIndividual;
  }
}
