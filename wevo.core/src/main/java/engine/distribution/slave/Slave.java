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
package engine.distribution.slave;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import engine.CachedObjectiveFunction;
import engine.Population;
import engine.PopulationEvaluator;
import engine.distribution.serialization.EvaluationResult;
import engine.distribution.serialization.EvaluationResultBuilder;
import engine.distribution.serialization.EvaluationTask;
import engine.distribution.serialization.JavaIOResultSerializer;
import engine.distribution.serialization.JavaIOTaskSerializer;
import engine.distribution.serialization.ResultSerializer;
import engine.distribution.serialization.TaskSerializer;
import engine.utils.SystemClock;
import engine.utils.WevoClock;

/**
 * Slave in the master-slave distribution model.
 * @param <T> Type of individuals in the evaluated populations.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class Slave<T> {

  /** Logging utility. Package visibility for testing. */
  private final Logger logger = Logger.getLogger(
      Slave.class.getCanonicalName());

  /** Evaluator for population evaluation. */
  private final PopulationEvaluator<T> evaluator;

  /** Handles server connection. */
  private final TaskExchanger<T> exchanger;

  /** Registers slave in the distribution. */
  private SlaveRegistrator registrator;

  /** Master's url. */
  private final String url;

  /** This slave's name. Set by user. */
  private final String slaveName;

  /** Slave id allocated by master (after registration). */
  private String slaveId = null;

  /** Time measurement utility. */
  private WevoClock clock;

  /**
   * Major constructor. Package-visibility for testing.
   * @param url Master's url.
   * @param evaluator Population evaluator. Must not be null.
   * @param newPopulationExchanger Exchanges population with server.
   * Must not be null.
   * @param newSlaveRegistrator Registers slave in the distribution.
   * @param slaveName Name of this slave.
   * @param newClock Time measurement utility.
   */
  // ParameterNumber off
  Slave(final String url, 
      final PopulationEvaluator<T> evaluator,
      final TaskExchanger<T> newPopulationExchanger,
      final SlaveRegistrator newSlaveRegistrator,
      final String slaveName,
      final WevoClock newClock) {
    this.url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    this.evaluator = evaluator;
    this.exchanger = newPopulationExchanger;
    this.registrator = newSlaveRegistrator;
    this.slaveName = slaveName;
    this.clock = newClock;
  }
  // ParameterNumber on

  /**
   * Creates standard slave unit.
   * @param evaluator Population evaluator to use.
   * @param slaveName Name of this slave unit.
   * @param serverUrl Url of the master unit.
   * @param <T> Type of an individual in the population evaluated.
   * @throws MalformedURLException Thrown on malformed urls.
   * @return Standard slave unit.
   */
  public static <T> Slave<T> createSlave(
      final PopulationEvaluator<T> evaluator,
      final String slaveName,
      final String serverUrl) throws MalformedURLException {
    return new Slave<T>(
        serverUrl,
        evaluator,
        new TaskExchanger<T>(
            serverUrl,
            new JavaIOTaskSerializer<T>(),
            new JavaIOResultSerializer<T>()),
        new SlaveRegistrator(serverUrl),
        slaveName,
        new SystemClock());
  }

  /**
   * Creates standard slave unit.
   * @param evaluator Population evaluator to use.
   * @param slaveName Name of this slave unit.
   * @param serverUrl Url of the master unit.
   * @param <T> Type of an individual in the population evaluated.
   * @param taskSerializer Task serializer tool to use.
   * @param resultSerializer Result serializer tool to use.
   * @throws MalformedURLException Thrown on malformed urls.
   * @return Standard slave unit.
   */
  public static <T> Slave<T> createSlaveWithSerializers(
      final PopulationEvaluator<T> evaluator,
      final String slaveName,
      final String serverUrl,
      final TaskSerializer<T> taskSerializer,
      final ResultSerializer<T> resultSerializer) throws MalformedURLException {
    return new Slave<T>(
        serverUrl,
        evaluator,
        new TaskExchanger<T>(
            serverUrl,
            taskSerializer,
            resultSerializer),
        new SlaveRegistrator(serverUrl),
        slaveName,
        new SystemClock());
  }

  /**
   * Setter for slaveId. For testing purposes only!
   * @param slaveId Id of slave to set.
   */
  public void setSlaveId(String slaveId) {
    this.slaveId = slaveId;
  }

  /**
   * Setting up slave. Registers this slave in master server. 
   * @param times Maximum number of times a slave should try to register. 
   * Negative value indicates infinite loop.
   * @param backoff Number of milliseconds for slave to seize
   * before retrying to register.
   * @param timeout Number of seconds for slave to timeout.
   * @throws ClassNotFoundException Thrown when definition of a class
   * found in the response from master was not found.
   * */
  public void register(final int times, final int backoff, final int timeout)
      throws ClassNotFoundException {

    final String cyclesString = times < 0
        ? "infinite" : String.valueOf(times);
    int counter = 1;

    logger.log(Level.INFO, "Registering slave with " + cyclesString
        + " number of cycles. Evolution server address: " + url);

    int timesLeft = times;
    final long startTime = clock.getCurrentTimeMillis();
    while (timesLeft != 0) {
      timesLeft = timesLeft < 0 ? timesLeft : timesLeft - 1;

      try {
        logger.log(Level.INFO, "Registering slave. Trial " + counter++
            + " of " + cyclesString);

        slaveId = registrator.register(slaveName);

        logger.log(Level.INFO, "Slave '" + slaveName
            + "' was assigned an ID: " + slaveId);

        return;
      } catch (IOException exception) {
        logException(exception, backoff);
        maybeTimeout(timeout, startTime);
      } catch (ClassNotFoundException exception) {
        logException(exception, 0);
        throw exception;
      }
    }

    throw new IllegalStateException("Failed to register with evolution server "
        + url);
  }

  /**
   * Runs the slave main routine. Slave must be registered
   * before this method is called.
   * @param backoff Number of milliseconds for slave to seize asking master
   * for population after a single rejection.
   * @param timeout Timeout (in seconds).
   * @throws ClassNotFoundException Thrown when definition for classes sent
   * by master are missing.
   */
  public void run(final int backoff, final int timeout)
      throws ClassNotFoundException {

    // Occurs when registration fails (e.g. due to unavailable master). 
    if (slaveId == null) {
        throw new IllegalStateException("Must be registered first");
    }

    int iteration = 0;
    while (true) {
      final long startTime = clock.getCurrentTimeMillis();
      try {
        logger.log(Level.INFO, "Starting iteration " + (++iteration));
        logger.log(Level.INFO, "Retrieving evaluation task from server.");
        EvaluationTask<T> evaluationTask = exchanger.getTask(slaveId, backoff);
        logger.log(Level.FINEST, "Task retrieved (: "
            + evaluationTask.size() + ") : \n" + evaluationTask.toString());

        logger.log(Level.INFO, "Starting task evaluation.");
        evaluator.evaluatePopulation(evaluationTask.getPopulation());
        logger.log(Level.INFO, "Task evaluated.");

        logger.log(Level.INFO, "Sending evaluation result to server.");
        exchanger.sendResult(createEvaluationResult(
            evaluationTask.getPopulation()), slaveId);
        logger.log(Level.INFO, "Evaluation result sent to server.");

      } catch (IOException exception) {
        logException(exception, backoff);
        maybeTimeout(timeout, startTime);
      } catch (ClassNotFoundException exception) {
        logException(exception, 0);
        throw exception;
      }
    }
  }

  /**
   * Creates an evaluation result from the data in the evaluator.
   * @param currentPopulation Population limiting the evaluation result size.
   * @return Evaluation result.
   */
  private EvaluationResult<T> createEvaluationResult(
      final Population<T> currentPopulation) { 
    EvaluationResultBuilder<T> evaluationResultBuilder =
        new EvaluationResultBuilder<T>();
    for (CachedObjectiveFunction<T> objectiveFunction 
        : evaluator.getObjectiveFunctions()) {
      logger.log(Level.INFO, "Storing evaluation results in local cache");
      logger.log(Level.FINEST, "Local cache content ("
          + objectiveFunction.getCache().size() + "): "
          + objectiveFunction.getCache().toString());

      evaluationResultBuilder.appendObjectiveFunctionResults(cropCache(
          objectiveFunction.getCache(), currentPopulation));
    }

    EvaluationResult<T> evaluationResult =
        evaluationResultBuilder.toEvaluationResult();

    logger.log(Level.FINEST, "Evaluation result ("
        + evaluationResult.size() + "): \n"
        + evaluationResult.toString());

    return evaluationResult;
  }

  /**
   * Returns copy of the given cache, cropped to given set of individuals.
   * @param cache Cache to be cropped.
   * @param currentPopulation Set of individuals to retain in the cache.
   * @return Copy of the given cache cropped to the given set of individuals.
   */
  private Map<T, Double> cropCache(final Map<T, Double> cache,
      final Population<T> currentPopulation) {
    LinkedHashMap<T, Double> croppedCache =
        new LinkedHashMap<T, Double>();
    for (T individual : currentPopulation.getIndividuals()) {
      if (!cache.containsKey(individual)) {
        throw new IllegalStateException("Ouch! Missing " + individual
            + "in cache");
      }
      croppedCache.put(individual, cache.get(individual));
    }
    return croppedCache;
  }

  /**
   * Triggers timeout mechanism iff the timeout expired.
   * @param timeout Timeout (in seconds).
   * @param startTime Time when the clock started.
   */
  private void maybeTimeout(final int timeout, final long startTime) {
    if (timeout < 0) {
      return;
    }

    final long currentTime = clock.getCurrentTimeMillis();
    // MagicNumber off
    if (currentTime - startTime >= timeout * 1000) {
      throw new IllegalStateException("Slave registration timed out.");
    }
    // MagicNumber on
  }

  /**
   * Logs given exception and falls asleep for a while.
   * @param exception Exception to be logged.
   * @param millisToSleep Time to sleep in milliseconds. Must not be
   * less than zero.
   */
  private void logException(
      final Exception exception,
      final int millisToSleep) {
    logger.log(Level.SEVERE, "A severe exception occured.", exception);

    try {
      if (millisToSleep > 0) {
        // Regexp off
        Thread.sleep(millisToSleep);
        // Regexp on
      }
    } catch (InterruptedException interrupt) {
      logger.log(Level.WARNING, "Slave was interrupted.", interrupt);
    }
  }
}
