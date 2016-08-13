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

import java.util.List;

import engine.ObjectiveFunction;
import engine.Operator;
import engine.Population;
import engine.utils.WevoRandom;

/**
 * Takes a result of another operator plus the best individual from
 * a given population (one for each objective function) and combines
 * them.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 * @param <T> type of the individual.
 */
public class CombineBestWithOtherOperator<T> implements Operator<T> {
 
  /** Objective functions used in evaluation. */
  private final List<? extends ObjectiveFunction<T>> objectiveFunctions;
 
  /** Operator that serves the basis for this one. */
  private final Operator<T> operator;

  /** Random number generator. */
  private final WevoRandom random;

  /**
   * Creates the operator.
   * @param functions Objective function to use.
   * @param operator  Operator to apply.
   * @param random Random number generator.
   */
  public CombineBestWithOtherOperator(
      final List<? extends ObjectiveFunction<T>> functions, 
      final Operator<T> operator,
      final WevoRandom random) {
    this.objectiveFunctions = functions;
    this.operator = operator;
    this.random = random;
  }

  /** {@inheritDoc}. */
  public Population<T> apply(Population<T> population) {
    Population<T> result = operator.apply(population);
    for (ObjectiveFunction<T> function : objectiveFunctions) {
      T bestIndividual = population.getIndividuals().get(0);
      for (int i = 1; i < population.size(); i++) {
        if (function.compute(population.getIndividuals().get(i))
            > function.compute(bestIndividual)) {
          bestIndividual = population.getIndividuals().get(i);
        }
      }

      result = Population.removeRandomIndividual(random, result);
      result.addIndividual(bestIndividual);
    }
    return result;
  }
}
