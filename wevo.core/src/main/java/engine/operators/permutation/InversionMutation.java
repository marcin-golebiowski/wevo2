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
 * Class implementing a well-known inversion mutation operator. Mutation that
 * takes place is simple:
 * 1) two distinct positions are chosen; let us denote
 * them by i and j. Assume, that i < j.
 * 2) the sequence x_{i}, x_{i+1}, ..., x_{j} is inverted, i.e. we reverse
 * the sequence and copy it to the exact same place in individual.
 * 
 * @author Karol Asgaroth Stosiek (karol.stosiek@gmail.com)
 * @author Szymon Fogiel (szymek.fogiel@gmail.com)
 */
public class InversionMutation implements Operator<Permutation> {

  /** Probability that a mutation will happen to individual. */
  private double mutationProbability;

  /** Random number generator. */
  private WevoRandom randomGenerator;

  /**
   * Constructor.
   * @param generator Random number generator.
   * @param probability Probability of a individual mutation.
   */
  public InversionMutation(
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
   * Mutates given individual. The source individual stays intact. Package
   * visibility for testing.
   * @param individual Individual to be mutated.
   * @return Mutated individual.
   */
  Permutation mutate(final Permutation individual) {
    final Permutation mutatedIndividual =  new Permutation(individual);

    final int guess1 = randomGenerator.nextInt(0, individual.getSize());
    final int guess2 = randomGenerator.nextInt(0, individual.getSize());

    final int start = guess1 <= guess2 ? guess1 : guess2;
    final int end = guess1 > guess2 ? guess1 : guess2;

    int i = start;
    int j = end;
    while (i <= j) {
      mutatedIndividual.transpose(i++, j--);
    }

    return mutatedIndividual;
  }
}
