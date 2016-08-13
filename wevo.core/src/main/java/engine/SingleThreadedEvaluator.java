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
 * A single-threaded evaluator. Iterates in a loop over all individual in 
 * a population and computes the value of objective function.
 * 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individual being evaluated.
 */
public class SingleThreadedEvaluator<T> extends PopulationEvaluator<T> {

  /**
   * Creates single threaded evaluator.
   * @param objectiveFunctions List of the objective functions to be evaluated.
   */
  public SingleThreadedEvaluator(
      List<CachedObjectiveFunction<T>> objectiveFunctions) {
    super(objectiveFunctions);
  }

  /** {@inheritDoc} */
  @Override
  public void evaluatePopulation(Population<T> populationInternal) {
    for (T individual : populationInternal.getIndividuals()) {
      for (CachedObjectiveFunction<T> objectiveFunction 
          : getObjectiveFunctions()) {
        objectiveFunction.computeInternal(individual);
      }
    }
  }

}
