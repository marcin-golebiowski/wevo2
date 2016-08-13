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
package engine.distribution.master;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import engine.CachedObjectiveFunction;
import engine.Population;
import engine.distribution.serialization.EvaluationResult;
import engine.distribution.serialization.EvaluationTask;

/**
 * This class is responsible for synchronized access from servlets
 * to distributed population. It works as a mediator between servlets
 * and MasterEvaluator.
 * @param <T> Type of the individuals in the population.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class TaskManager<T> {

  /** Logging utility. */
  private final Logger logger =
      Logger.getLogger(TaskManager.class.getCanonicalName());

  /** Population to be distributed among slaves. */
  private Map<String, EvaluationTask<T>> taskDistribution;

  /** Signal indicating whether population evaluation is over or not. */
  private CountDownLatch unevaluatedPopulationShards;

  /** List of objective functions to calculate individual values. */
  private List<CachedObjectiveFunction<T>> objectiveFunctions;

  /**
   * Sets population to be distributed. Package visibility for testing purposes.
   * @param newTaskDistribution Distribution of the tasks among slaves.
   * @param newObjectiveFunctions List of objective functions.
   * @return Signal object indicating whether population is evaluated or not.
   */
  CountDownLatch enableDistribution(
      final Map<String, EvaluationTask<T>> newTaskDistribution,
      final List<CachedObjectiveFunction<T>> newObjectiveFunctions) {

    logger.log(Level.FINEST, "Enabling distribution. Task distribution "
        + "(" + newTaskDistribution.size() + "): " + newTaskDistribution);

    this.taskDistribution = newTaskDistribution;
    this.objectiveFunctions = newObjectiveFunctions;
    this.unevaluatedPopulationShards =
        new CountDownLatch(newTaskDistribution.size());
    return unevaluatedPopulationShards;
  }

  /**
   * Sets population to be distributed. This method should be called only
   * by MasterEvaluator.
   * @param newDistributedPopulation Population to be distributed among slaves.
   * @param newObjectiveFunctions List of cached objective functions.
   * @return Signal object indicating whether population is evaluated or not.
   */
  public CountDownLatch enableDistribution(
      final DistributedPopulation<T> newDistributedPopulation,
      final List<CachedObjectiveFunction<T>> newObjectiveFunctions) {

    logger.log(Level.FINEST, "Turning distributed population into task "
        + "distribution.Distributed population: "
        + newDistributedPopulation.toString());

    Map<String, EvaluationTask<T>> newTaskDistribution =
        new LinkedHashMap<String, EvaluationTask<T>>();
    for (Entry<String, Population<T>> mapping : newDistributedPopulation) {
      newTaskDistribution.put(mapping.getKey(),
          new EvaluationTask<T>(mapping.getValue()));
    }

    logger.log(Level.FINEST, "Resulting task distribution: \n"
        + newTaskDistribution.toString());

    return enableDistribution(newTaskDistribution, newObjectiveFunctions);
  }

  /**
   * Indicates whether population is available for distribution.
   * @return True iff population is available for distribution among slaves.
   */
  public synchronized boolean isDistributionEnabled() {
    return taskDistribution != null
        && unevaluatedPopulationShards != null
        && unevaluatedPopulationShards.getCount() > 0;
  }

  /**
   * Gets population for given slave.
   * @param slaveId Slave asking for population.
   * @return Population for a slave to be evaluated or null, if there is
   * no population to be evaluated.
   */
  public synchronized EvaluationTask<T> getTaskForSlave(String slaveId) {
    logger.log(Level.INFO, "Getting task for slave " + slaveId);
    EvaluationTask<T> evaluationTask = taskDistribution.get(slaveId);
    logger.log(Level.FINER, "Task for slave " + slaveId + ": "
        + evaluationTask.toString());
    return evaluationTask;
  }

  /**
   * Indicates whether given slave has a population to evaluate or not.
   * @param slaveId ID of the slave asking for population.
   * @return True iff there is a population to be evaluated by given slave.
   */
  public synchronized boolean isTaskAvailableForSlave(String slaveId) {
    logger.log(Level.INFO, "Checking if there is a task for slave " + slaveId);
    EvaluationTask<T> potentialTask = taskDistribution.get(slaveId);
    logger.log(Level.FINEST, "Task for slave " + slaveId + ": "
        + potentialTask.toString() + (potentialTask == null 
            ? "unknown" : potentialTask.isEvaluated() 
                ? "evaluated" : "not evaluated"));

    return potentialTask != null && !potentialTask.isEvaluated();
  }

  /**
   * Updates slave's task with given evaluation result.
   * @param slaveId ID of the slave whom task is being updated.
   * @param evaluationResult Result of the evaluation.
   */
  public synchronized void updateTask(
      final String slaveId,
      final EvaluationResult<T> evaluationResult) {

    logger.log(Level.INFO, "Updating task for slave " + slaveId
        + " with evaluation results");
    logger.log(Level.FINEST, "Evaluation result (" 
        + evaluationResult.size() + "): \n"
        + evaluationResult.toString());

    EvaluationTask<T> assignedTask = taskDistribution.get(slaveId);
    assignedTask.markAsEvaluated();

    logger.log(Level.FINEST, "Updated task (" 
        + assignedTask.size() + "): \n"
        + assignedTask.toString());

    logger.log(Level.FINE, "Merging evaluation results with objective "
        + "functions data");
    for (int i = 0; i < objectiveFunctions.size(); i++) {
      final CachedObjectiveFunction<T> cachedObjectiveFunction =
          objectiveFunctions.get(i);
      final Map<T, Double> singleEvaluationResult =
          evaluationResult.getResult(i);

      logger.log(Level.FINEST, "Merging objective function's ("
          + cachedObjectiveFunction.toString() + ") cache (" 
          + cachedObjectiveFunction.getCache().size() + "):"
          + cachedObjectiveFunction.getCache().toString()
          + " with evaluation result ("
          + singleEvaluationResult.size() + "): \n"
          + singleEvaluationResult.toString());

      cachedObjectiveFunction.merge(singleEvaluationResult);
    }

    unevaluatedPopulationShards.countDown();
    logger.log(Level.FINER, "Tasks to evaluate left: "
        + unevaluatedPopulationShards.getCount());
  }

  /**
   * Returns the unevaluated population, merged from unevaluated tasks.
   * @return Subpopulation that was not evaluated yet.
   */
  public synchronized Population<T> getUnevaluatedPart() {
    Population<T> unevaluatedPart = new Population<T>();
    for (EvaluationTask<T> task : taskDistribution.values()) {
      if (!task.isEvaluated()) {
        unevaluatedPart.mergeWith(task.getPopulation());
      }
    }
    return unevaluatedPart;
  }
}
