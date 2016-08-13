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
package engine.operators.reporters;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import engine.ObjectiveFunction;
import engine.Operator;
import engine.Population;
import engine.utils.SystemClock;
import engine.utils.WevoClock;

/**
 * Prints out the best individual and some basic statistics, like
 * the mean value of the objective function in the population,
 * standard deviation. Population should have at least one individual.
 * @param <T> Type of the invidiual in the population.
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class BestIndividualAndBasicStats<T> implements Operator<T> {

  /** Objective function that we're trying to optimize. */
  private final List<? extends ObjectiveFunction<T>> objFunction;

  /** Iteration number. */
  private int iterationNumber;

  /** Logger to which write out the output. */
  private final Logger logger;
 
  /** Last time this operator was executed. */
  private long lastExecutionTime = 0;
 
  /** Interpretation of the individual. */
  private final Interpretation<T> interpretation;

  /** Time measurement utility. */
  private final WevoClock clock;
 
  /**
   * Creates reporter that reports basic statistics about the population
   * and prints out the best individual.
   * @param objFunction Objective function we're optimizing.
   * @param logger Logger to which output is written.
   * @param interpretation Interpretation of the individual. May be null
   *    if raw output is enough.
   * @param newClock Time measurement utility.
   */
  public BestIndividualAndBasicStats(
      final List<ObjectiveFunction<T>> objFunction,
      final Logger logger,
      final Interpretation<T> interpretation,
      final WevoClock newClock) {
    this.objFunction = objFunction;
    this.logger = logger;
    this.interpretation = interpretation;
    this.clock = newClock;
  }
 
  /**
   * Creates reporter that reports basic statistics about the population
   * and prints out the best individual.
   * @param objFunction Objective function we're optimizing.
   * @param interpretation Interpretation of the individual. May be null
   *    if raw output is enough.
   *  This is deprecated. Please switch to using constructor that has all
   *  parameters injected.
   */
  @Deprecated
  public BestIndividualAndBasicStats(
      List<? extends ObjectiveFunction<T>> objFunction,
      Interpretation<T> interpretation) {
    this.objFunction = objFunction;
    this.logger = Logger.getLogger(
        BestIndividualAndBasicStats.class.getCanonicalName());
    this.interpretation = interpretation;
    this.clock = new SystemClock();
  }
 
  /** {@inheritDoc} */
  public Population<T> apply(Population<T> population) {
    HashMap<ObjectiveFunction<T>, Double> meanObjectiveFunctionValues = 
      new HashMap<ObjectiveFunction<T>, Double>();
    T bestIndividual =
        findBestIndividualAndComputeSumOfObjFunctionValues(
            population, meanObjectiveFunctionValues);
    logger.info("Iteration " + iterationNumber++);
    logger.info("Best individual " + interprete(bestIndividual));
    logger.info("Population size " + population.size());
    for (ObjectiveFunction<T> o : objFunction) {
      logger.info("Objective value of " + o + "for best individual is "
          + o.compute(bestIndividual));
      logger.info("Mean value for " + o + " is " 
          + meanObjectiveFunctionValues.get(o) / population.size());
    }
    updateTimer();
    return population;
  }

  /**
   * Interpretes the individual as string. If an interpretation is
   * available, uses it. Otherwise uses toString method.
   * @param bestIndividual Individual to be interpreted.
   * @return String representation of the individual.
   */
  private String interprete(T bestIndividual) {
    if (interpretation == null) {
      return bestIndividual.toString();
    }
    return interpretation.interprete(bestIndividual);
  }

  /**
   * Finds best individual (best as in having highest obj. function values
   * for each objective function) and computes the sum of objective function 
   * values.
   * @param population Population to look in.
   * @param sumOfObjectiveFunctionValues Map in which 
   *    to store sum of obj. function values.
   * @return Best individual in the population.
   */
  private T findBestIndividualAndComputeSumOfObjFunctionValues(
      Population<T> population,
      HashMap<ObjectiveFunction<T>, Double> sumOfObjectiveFunctionValues) {
    T bestIndividual = population.getIndividuals().get(0);
    for (T individual : population.getIndividuals()) {
      boolean isBetter = true;
      for (ObjectiveFunction<T> function : objFunction) {
        double individualObjFunctionValue = function.compute(individual);
        if (individualObjFunctionValue < function.compute(bestIndividual)) {
          isBetter = false;
        }
        Double previousValue = sumOfObjectiveFunctionValues.get(function);
        if (previousValue == null) {
          previousValue = 0.0;
        }
        sumOfObjectiveFunctionValues.put(function,
            previousValue
                + individualObjFunctionValue);
      }
      if (isBetter) {
        bestIndividual = individual;
      }
    }
    return bestIndividual;
  }

  /** Updates timing stats. */
  private void updateTimer() {
    if (lastExecutionTime != 0) {
      long time = clock.getCurrentTimeMillis() - lastExecutionTime;
      logger.info("Last iteration took " + time + "ms");
    }
    lastExecutionTime = clock.getCurrentTimeMillis();
  }
}
