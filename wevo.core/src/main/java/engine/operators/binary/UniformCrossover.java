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
import engine.utils.JavaRandom;
import engine.utils.WevoRandom;

/**
 * Uniform crossover operator. Offsprings have equal probability
 * of inheriting gene from both of his parents.
 * 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 */
public class UniformCrossover implements Operator<BinaryVector> {

  /** Head-tail probability. */
  private static final double DEFAULT_PROBABILITY = 0.5;

  /** Random number generator. */
  private final WevoRandom random;

  /**
   * Creates a uniform crossover object.
   * @param random Random to be used
   */
  public UniformCrossover(final JavaRandom random) {
    this.random = random;
  }

  /** {@inheritDoc} */
  public Population<BinaryVector> apply(
      Population<BinaryVector> population) {
    Iterator<BinaryVector> iterator = population.getIndividuals().iterator();
    List<BinaryVector> output = new LinkedList<BinaryVector>();

    while (iterator.hasNext()) {
      BinaryVector b1 = iterator.next();
      BinaryVector b2 = iterator.next();

      boolean[] o1 = new boolean[b1.getSize()];
      boolean[] o2 = new boolean[b1.getSize()];
      for (int i = 0; i < b1.getSize(); i++) {
        if (random.nextDouble(0.0, 1.0) < DEFAULT_PROBABILITY) {
          o1[i] = b1.getBit(i);
          o2[i] = b2.getBit(i);
        } else {
          o1[i] = b2.getBit(i);
          o2[i] = b1.getBit(i);
        }
      }
      output.add(new BinaryVector(o1));
      output.add(new BinaryVector(o2));
    }

    return new Population<BinaryVector>(output);
  }
}
