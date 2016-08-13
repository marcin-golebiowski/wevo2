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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encapsulates the list of operators, evaluators, termination conditions, etc.
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individuals to be evolved.
 */
public class Algorithm<T> {

  /** Logger. */
  private final Logger logger =
      Logger.getLogger(Algorithm.class.getCanonicalName());

  /** Has the algorithm finished. */
  private boolean isFinished;

  /** Population on which algorithms works. */
  private Population<T> population;

  /** List of operators that the algorithm is based on. */
  private List<Operator<T>> operators;

  /** Forces algorithm to reset termination condition. */
  private boolean shouldBeReset = false;

  /**
   * Creates the Algorithm that will form the basis for the evolution.
   * @param population Initial population.
   */
  public Algorithm(Population<T> population) {
    this.population = population;
    operators = new ArrayList<Operator<T>>();
  }

  /**
   * Adds evaluation point in the algorithm.
   * @param evaluator Evaluator to use in the algorithm.
   */
  public void addEvaluationPoint(PopulationEvaluator<T> evaluator) {
    operators.add(evaluator);
  }

  /**
   * Adds exit point to the algorithm, if the condition is met, the algorithm
   * terminates at the earliest possible point.
   * @param terminationCondition Termination condition which, when met, 
   *      terminates the execution of the algorithm. 
   */
  public void addExitPoint(final TerminationCondition<T> terminationCondition) {
    operators.add(new Operator<T>() {
      public Population<T> apply(Population<T> populationInternal) {
        if (Algorithm.this.shouldBeReset) {
          Algorithm.this.shouldBeReset = false;
          terminationCondition.reset();
        }
        if (terminationCondition.isSatisfied(populationInternal)) {
          Algorithm.this.isFinished = true;
        }
        return populationInternal;
      }
    });
  }

  /**
   * Adds an operator to the algorithm.
   * @param operator Operator to be added.
   */
  public void addOperator(Operator<T> operator) {
    operators.add(operator);
  }

  /**
   * Runs the algorithm.
   */
  public void run() {
    long iterationNo = 0;
    while (true) {
      iterationNo++;
      logger.log(Level.FINE, "Iteration " + iterationNo + " started");
      for (Operator<T> operator : operators) {
        if (this.isFinished) {
          return;
        }
        logger.fine("Applying operator " 
            + operator.getClass().getCanonicalName());
        population = operator.apply(population);
      }

      logger.log(Level.FINER, "Iteration " + iterationNo + " finished");
    }
  }

  /**  Sets true to flag which forces termination condition to reset. */
  public void reset() {
    shouldBeReset = true;
    isFinished = false;
  }

  /**
   * Returns the population at the end of the algorithm.
   * @return The population at the end of the algorithm.
   */
  public Population<T> getPopulation() {
    return population;
  }

  /**
   * Sets population object.
   * @param population Population object to set.
   */
  public void setPopulation(Population<T> population) {
    this.population = population;
  }
}
