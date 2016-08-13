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

import java.util.LinkedList;
import java.util.List;

import engine.Operator;
import engine.Population;
import engine.individuals.RealVector;

/**
 * An operator inspired by differential evolution. Resulting vector is of a form
 * <code>offsprint = parent1 + alpha * (parent2 - parent3)</code>. 
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class DifferentialEvolution implements Operator<RealVector> {

  /** Alpha coefficient of differential evolution. */
  private final double alpha;

  /**
   * Creates differential evolution operator.
   * @param alpha Alpha coefficient of the operator.
   */
  public DifferentialEvolution(double alpha) {
    this.alpha = alpha;
  }
 
  /** {@inheritDoc} */
  public Population<RealVector> apply(Population<RealVector> population) {
    List<RealVector> result = new LinkedList<RealVector>();

    List<RealVector> individuals = population.getIndividuals();
    int size = individuals.size();
    for (int individual = 0; individual < size; individual++) {
      RealVector parent1 = individuals.get(individual % size);
      RealVector parent2 = individuals.get((individual + 1) % size);
      RealVector parent3 = individuals.get((individual + 2) % size);
      double[] offspring = new double[parent2.getSize()];
      for (int position = 0; position < offspring.length; position++) {
        offspring[position] = parent1.getValue(position)
            + alpha * (parent2.getValue(position) - parent3.getValue(position));
      }
      result.add(new RealVector(offspring));
    }
    return new Population<RealVector>(result);
  }
}
