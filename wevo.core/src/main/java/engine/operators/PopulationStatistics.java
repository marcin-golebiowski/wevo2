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

import engine.ObjectiveFunction;
import engine.Operator;
import engine.Population;

/**
 * Tracks some statistics of population.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 *
 * @param <T> Type of the individual in the population.
 */
public class PopulationStatistics<T> implements Operator<T> {

  /** Objective functions used in evaluation. */
  private final ObjectiveFunction<T> objectiveFunction;

  /** Current best individual. */
  private T currentBest;

  /** Current best individual value. */
  private double currentAverageValue;

  /** Current best individual. */
  private T currentWorst;

  /**
   * Constructor.
   * @param function Function that is used for comparison.
   */
  public PopulationStatistics(
      final ObjectiveFunction<T> function) {
    this.objectiveFunction = function;
  }

  /** {@inheritDoc} */
  public Population<T> apply(Population<T> population) {
    int i = 0;
    currentAverageValue = 0.0;
    if (currentBest == null && currentWorst == null) {
      T first = population.getIndividuals().get(0);
      currentBest = first;
      currentWorst = first;
      currentAverageValue = objectiveFunction.compute(first);
      i++;
    }

    while (i < population.size()) {
      T currentCandidate = population.getIndividuals().get(i);
      double v1 = objectiveFunction.compute(currentCandidate);
      double v2 = objectiveFunction.compute(currentBest);
      double v3 = objectiveFunction.compute(currentWorst);

      currentAverageValue += v1;
      if (v1 > v2) {
        currentBest = currentCandidate;
      }
      if (v1 < v3) {
        currentWorst = currentCandidate;
      }
      i++;
    }
    currentAverageValue /= population.size();
    return population;
  }

  /**
   * Returns current best individual in the population.
   * @return Best yet found individual or null if none exists.
   */
  public T getBestIndividual() {
    return currentBest;
  }

  /**
   * Returns currently best individual objective value.
   * @return Value of the currently best individual.
   */
  public double getBestIndividualValue() {
    return objectiveFunction.compute(currentBest);
  }

  /**
   * Returns current worst individual in the population.
   * @return Worst yet found individual or null if none exists.
   */
  public T getWorstIndividual() {
    return currentWorst;
  }

  /**
   * Returns currently worst individual objective value.
   * @return Value of the currently worst individual.
   */
  public double getWorstIndividualValue() {
    return objectiveFunction.compute(currentWorst);
  }

  /**
   * Returns current average individual objective value.
   * @return Value of the current average individual.
   */
  public double getAverageIndividualValue() {
    return currentAverageValue;
  }
}
