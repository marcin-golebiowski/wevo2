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
package classifier.examples.operators;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import engine.ObjectiveFunction;
import engine.Operator;
import engine.Population;
import engine.utils.JavaRandom;

/**
 * Returns better part of the individuals in the population.
 * 
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * 
 * @param <T> Type of the individual in the population.
 */
public class BestIndividualsSelection<T> implements Operator<T> {

  // TODO (anglart.michal) : there is something I don't understand
  // in similar operator in core. Check if it can be merged.

  /** Objective function for individual computing. */
  private final ObjectiveFunction<T> objectiveFunction;
 
  /** Fraction of best individuals to take. 1 makes identity. */
  private final double ratio;

  /**
   * Creates the operator that will return set of best individuals taken
   * from it.
   * 
   * @param objectiveFunction Objective function for evaluating individuals.
   * @param ratio Fraction of best individuals to take. This value should be
   *   in range [0.0, 1.0]
   */
  public BestIndividualsSelection(ObjectiveFunction<T> objectiveFunction,
      double ratio) {
    this.objectiveFunction = objectiveFunction;
    this.ratio = ratio;
  }
 
  /** {@inheritDoc} */
  public Population<T> apply(
      Population<T> population) {
    Comparator<T> comparator = new Comparator<T>() {
      public int compare(T o1, T o2) {
        Double v1 = objectiveFunction.compute(o1);
        Double v2 = objectiveFunction.compute(o2);
        return -v1.compareTo(v2);
      }
    };

    // Sort rules in descending order
    List<T> individuals = population.getIndividuals();
    Collections.sort(individuals, comparator);

    // Get best individuals and shuffle the population.
    List<T> result = new LinkedList<T>();
    int borderLine = (int) (individuals.size() * ratio);
    for (int i = 0; i < borderLine; i++) {
      result.add(individuals.get(i % individuals.size()));
    }
    Population<T> resultPopulation = new Population<T>(result);

    return Population.shuffle(new JavaRandom(), resultPopulation);
  }
}
