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

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import engine.Operator;
import engine.Population;
import engine.individuals.NaturalVector;

/**
 * Performs a crossover of two natural number individuals by
 * creating two offsprings that contain random subset of genes
 * from two parents.
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class UniformCrossover implements Operator<NaturalVector> {

  /** 0.5. */
  private static final double DEFAULT_PROBABILITY = 0.5;

  /** RNG. */
  private final Random random;

  /**
   * Creates uniform crossover operator.
   * @param random Random number generator.
   */
  public UniformCrossover(Random random) {
    this.random = random;
  }

  /**
   * Creates uniform crossover operator.
   */
  public UniformCrossover() {
    this(new SecureRandom());
  }

  /** {@inheritDoc}. */
  public Population<NaturalVector> apply(
      Population<NaturalVector> population) {
    List<NaturalVector> result = new LinkedList<NaturalVector>();
    List<NaturalVector> individuals = population.getIndividuals();
    for (int i = 0; i < individuals.size() / 2; i++) {
      long[] p1 = individuals.get(2 * i).getValues();
      long[] p2 = individuals.get(2 * i + 1).getValues();
      long[] o1 = new long[p1.length];
      long[] o2 = new long[p1.length];

      for (int j = 0; j < p1.length; j++) {
        if (random.nextDouble() < DEFAULT_PROBABILITY) {
          o1[j] = p1[j];
          o2[j] = p2[j];
        } else {
          o1[j] = p2[j];
          o2[j] = p1[j];
        }
      }
      result.add(new NaturalVector(o1));
      result.add(new NaturalVector(o2));
    }

    if (result.size() < individuals.size()) {
      result.add(individuals.get(result.size()));
    }

    return new Population<NaturalVector>(result);
  }

}
