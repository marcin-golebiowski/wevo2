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
package engine.exitcriteria;

import java.util.Collections;
import java.util.Comparator;

import engine.ObjectiveFunction;
import engine.Population;
import engine.TerminationCondition;

/**
 * Terminates the evolution after for given number of iterations the
 * individual hasn't changed.
 * 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individual for which it is evaluated.
 */
public class IndividualHasntImproved<T> 
    implements TerminationCondition<T> {
  /** Number of iterations the individual is allowed to not change. */
  private final int maxIter;

  /** Best individual so far. */
  private T individual;

  /** Number of iterations individual hasn't changed. */
  private int iterationsWithNoChange;

  /** Objective function to look at when finding best individual. */
  private final ObjectiveFunction<T> objFunction;
 
  /** 
   * Creates the termination condition which will succeed after
   * maximum number of iterations has passed.
   * @param maxIter Maximum number of iterations.
   * @param objFunction Objective function to look at.
   */
  public IndividualHasntImproved(int maxIter, 
      ObjectiveFunction<T> objFunction) {
    this.maxIter = maxIter;
    this.objFunction = objFunction;
  }

  /** {@inheritDoc} */
  public boolean isSatisfied(Population<T> population) {
    T bestIndividual = 
      Collections.max(population.getIndividuals(), new Comparator<T>() {
        public int compare(T o1, T o2) {
          return objFunction.compute(o1) > objFunction.compute(o2)
              ? 1 : -1;
        }
      });
    if (individual != null && objFunction.compute(bestIndividual) 
          <= objFunction.compute(individual)) {
      iterationsWithNoChange++;
    } else {
      individual = bestIndividual;
      iterationsWithNoChange = 0;
    }
    return iterationsWithNoChange > maxIter - 1;
  }

  /** {@inheritDoc} */
  public void reset() {
    iterationsWithNoChange = 0;
  }
}
