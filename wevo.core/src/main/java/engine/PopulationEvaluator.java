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
package engine;

import java.util.List;

/**
 * Evaluator that computes values of objective functions for given population.
 * It is implemented as an operator so that it can be easily added at any step
 * in the algorithm.
 *
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 *
 * @param <T> Type of individuals being evaluated.
 */
public abstract class PopulationEvaluator<T> implements Operator<T> {
  /**
   * List of objective functions that will be evaluated.
   */
  // VisibilityModifier off
  protected final List<CachedObjectiveFunction<T>> objectiveFunctions;
  // VisibilityModifier on

  /**
   * Creates population evaluator.
   * @param objectiveFunctions List of objective functions
   *    that will be evaluated.
   */
  protected PopulationEvaluator(
      List<CachedObjectiveFunction<T>> objectiveFunctions) {
    this.objectiveFunctions = objectiveFunctions;
  }

  /** {@inheritDoc} */
  public Population<T> apply(Population<T> populationInternal) {
    evaluatePopulation(populationInternal);
    return populationInternal;
  }

  /**
   * Returns list of objective functions to be evaluated.
   * @return List of objective functions to be evaluated.
   */
  public List<CachedObjectiveFunction<T>> getObjectiveFunctions() {
    return objectiveFunctions;
  }

  /**
   * Evaluates given population.
   * @param populationInternal Population to be evaluated.
   */
  public abstract void evaluatePopulation(Population<T> populationInternal);
}
