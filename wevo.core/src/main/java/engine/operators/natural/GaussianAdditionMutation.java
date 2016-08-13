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
package engine.operators.natural;

import java.util.LinkedList;
import java.util.List;

import engine.Operator;
import engine.Population;
import engine.individuals.NaturalVector;
import engine.utils.WevoRandom;

/**
 * For each gene of each individual with given probability a mutation occurs.
 * The mutation draws  a number from gaussian distribution with standard 
 * deviation sigma and mean 0, adds it to the value of the gene.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class GaussianAdditionMutation implements Operator<NaturalVector> {
 
  /** Random number generator. */
  private final WevoRandom random;
 
  /** Sigma in gaussian sampling. */
  private final double sigma;
 
  /** Probability of mutation on a single gene. */
  private final double probability;

  /**
   * Creates a mutation for natural number individuals. For each gene of each
   * individual with given probability a mutation occurs. The mutation draws 
   * a number from gaussian distribution with standard deviation sigma and
   * mean 0, adds it to the value of the gene
   * and rounds to nearest natural number.
   * @param random Random number genarator.
   * @param probability Probability of mutation for each gene 
   *     of each individual.
   * @param sigma Standard deviation in gaussian distribution.
   */
  public GaussianAdditionMutation(
      final WevoRandom random, final double probability, final double sigma) {
    this.random = random;
    this.probability = probability;
    this.sigma = sigma;
  }

  /** {@inheritDoc}. */
  public Population<NaturalVector> apply(Population<NaturalVector> population) {
    List<NaturalVector> result = new LinkedList<NaturalVector>();
    List<NaturalVector> individuals = population.getIndividuals();
    for (NaturalVector parent : individuals) {
      NaturalVector offspring = new NaturalVector(parent.getSize());
      for (int i = 0; i < offspring.getSize(); i++) {
        double delta = 0;
        if (random.nextDouble(0.0, 1.0) < probability) {
          delta = random.nextGaussian() * sigma; 
        }
        offspring.setValue(i, Math.round(parent.getValue(i) + delta));
      }
      result.add(offspring);
    }

    return new Population<NaturalVector>(result);
  }
}
