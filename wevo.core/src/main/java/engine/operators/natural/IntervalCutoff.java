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

/**
 * For each gene of each individual if the gene is not within an interval
 * [a, b] it is brought back to it. Smaller values are replaced with a
 * and larger values with b.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class IntervalCutoff implements Operator<NaturalVector> {

  /** Minimum allowed value for a gene. */
  private final int min;
 
  /** Maximum allowed value for a gene. */
  private final int max;

  /**
   * Creates a take-back-to-given-interval operator, which for each gene 
   * of the individual if the value is smaller than min, replaces it 
   * with min and if it's larger than max replaces it with max.
   * @param min Minimum value.
   * @param max Maximum value.
   */
  public IntervalCutoff(int min, int max) {
    this.min = min;
    this.max = max;
  }

  /** {@inheritDoc}. */
  public Population<NaturalVector> apply(Population<NaturalVector> population) {
    List<NaturalVector> result = new LinkedList<NaturalVector>();
    List<NaturalVector> individuals = population.getIndividuals();
    for (NaturalVector parent : individuals) {
      NaturalVector offspring = new NaturalVector(parent.getSize());
      for (int i = 0; i < offspring.getSize(); i++) {
        if (parent.getValue(i) < min) {
          offspring.setValue(i, min);
        } else if (parent.getValue(i) > max) {
          offspring.setValue(i, max);
        } else {
          offspring.setValue(i, parent.getValue(i));
        }
      }
      result.add(offspring);
    }
    return new Population<NaturalVector>(result);
  }
}
