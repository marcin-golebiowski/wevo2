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

package engine.operators;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import engine.ObjectiveFunction;
import engine.Operator;
import engine.Population;

/**
 * Returns better half of the individuals in the population.
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individuals the operator should work on.
 */
public class BestFractionSelection<T> implements Operator<T> {

  /** Objective function that scores individuals. */
  private final ObjectiveFunction<T> objectiveFunction;
 
  /** Percentage of best individuals to take. 1 makes identity. */
  private final double ratio;

  /**
   * Creates the operator that will take the population and return best half 
   * of the individuals.
   * @param objectiveFunction Objective function to score individuals against.
   * @param fraction Fraction of individuals to promote.
   */
  public BestFractionSelection(ObjectiveFunction<T> objectiveFunction,
      double fraction) {
    this.objectiveFunction = objectiveFunction;
    this.ratio = fraction;
  }
 
  /** {@inheritDoc} */
  public Population<T> apply(
      Population<T> population) {
    Comparator<T> comparator = new Comparator<T>() {
      public int compare(T o1, T o2) {
        Double v1 = objectiveFunction.compute(o1);
        Double v2 = objectiveFunction.compute(o2);
        if (v1 > v2) {
          return -1;
        } else if (v1 == v2) {
          return 0;
        }
        return 1;
      }
    };
 
    List<T> individuals = population.getIndividuals();
    Collections.sort(individuals, comparator);
 
    List<T> result = new LinkedList<T>();
    int borderLine = (int) (individuals.size() * ratio);
    int i = 0;
    while (result.size() < individuals.size()) {
      result.add(individuals.get(i % borderLine));
      i++;
    }
    return new Population<T>(result);
  }
}
