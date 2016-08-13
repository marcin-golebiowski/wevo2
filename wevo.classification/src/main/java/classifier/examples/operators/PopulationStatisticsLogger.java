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

import java.util.logging.Level;
import java.util.logging.Logger;

import engine.ObjectiveFunction;
import engine.Operator;
import engine.Population;

/**
 * Tracks statistics of population and logs it.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 *
 * @param <T> Type of the individual in the population.
 */
public class PopulationStatisticsLogger<T> implements Operator<T> {

  /** Logger. */
  private final Logger logger =
      Logger.getLogger(PopulationStatisticsLogger.class.getCanonicalName());

  /** Objective functions used in evaluation. */
  private final ObjectiveFunction<T> objectiveFunction;

  /** Current best individual value. */
  private double bestValue = Double.MIN_VALUE;

  /** Current worst individual value. */
  private double worstValue = Double.MAX_VALUE;

  /** Current best individual objective function value. */
  private double averageValue;


  /**
   * Constructor.
   * @param function Function that is used for comparison.
   */
  public PopulationStatisticsLogger(
      final ObjectiveFunction<T> function) {
    this.objectiveFunction = function;
  }

  /** {@inheritDoc} */
  public Population<T> apply(Population<T> population) {
    bestValue = Double.MIN_VALUE;
    worstValue = Double.MAX_VALUE;
    averageValue = 0.0;

    for (T individual : population.getIndividuals()) {
      double currentValue = objectiveFunction.compute(individual);
      bestValue = Math.max(bestValue, currentValue);
      worstValue = Math.min(worstValue, currentValue);
      averageValue += currentValue;
    }

    averageValue /= population.size();

    logger.log(Level.INFO,
        "Best individual value: " + bestValue + "\n"
        + "Worst individual value: " + worstValue + "\n"
        + "Average individual value: " + averageValue + "\n");

    return population;
  }
}
