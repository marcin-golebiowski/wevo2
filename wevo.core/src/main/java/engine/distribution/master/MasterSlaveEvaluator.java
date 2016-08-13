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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import engine.CachedObjectiveFunction;
import engine.Population;
import engine.PopulationEvaluator;
import engine.distribution.master.servlets.DistributionServlet;
import engine.distribution.master.servlets.RegistrationServlet;
import engine.distribution.master.servlets.StatisticsServlet;
import engine.distribution.serialization.JavaIOResultSerializer;
import engine.distribution.serialization.JavaIOTaskSerializer;
import engine.distribution.serialization.ResultSerializer;
import engine.distribution.serialization.TaskSerializer;

/**
 * Population evaluator responsible for performing distributed evaluation
 * using master-slave model.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 *
 * @param <T> Type of individual in population being evaluated.
 */
public class MasterSlaveEvaluator<T> extends PopulationEvaluator<T> {

  /** Mapping for exchange servlet. */
  public static final String EXCHANGE_ADDRESS = "/exchange";

  /** Mapping for registration servlet. */
  public static final String REGISTER_ADDRESS = "/register";

  /** Mapping for statistics servlet. */
  public static final String STATISTICS_ADDRESS = "/statistics";

  /** Time to sleep while starting the server. */
  private static final int SLEEP_TIME = 5;

  /** Logging utility. */
  private final Logger logger = Logger.getLogger(
      MasterSlaveEvaluator.class.getCanonicalName());

  /** Server that runs the distribution. */
  private final Server server;

  /** Population distributor. */
  private PopulationDistributor<T> populationDistributor;

  /** Task manager. */
  private TaskManager<T> taskManager;

  /** Tool for keeping track of currently available slaves. */
  private SlaveManager slaveManager;

  /** Tool for keeping history of computation. */
  private StatisticsManager statisticsManager; // initialize, evaluate, setters

  /** Minimum number of slaves to run the distribution. */
  private int minimumSlaves = 1;

  /** Time limit for each iteration. Default: timeout disabled. */
  private long iterationTimeout = -1;

  /** Number of evaluation trials for each iteration. Default: infinite. */
  private int trialsPerIteration = -1;

  /**
   * Constructor. Package-visibility for testing purposes.
   * @param objectiveFunctions List of objective functions
   *      to be calculated for the population.
   * @param populationDistributor Tool used for dividing population
   * into subpopulations.
   * @param taskManager Tool used for managing tasks
   * during evolution.
   * @param slaveManager Tool used for keeping track of currently
   * available slaves.
   * @param statisticsManager Tool used for keeping history of computation.
   * @param server Server instance for distributing the computation.
   */
  // ParameterNumber off
  MasterSlaveEvaluator(
      final List<CachedObjectiveFunction<T>> objectiveFunctions,
      final PopulationDistributor<T> populationDistributor,
      final TaskManager<T> taskManager,
      final SlaveManager slaveManager,
      final StatisticsManager statisticsManager,
      final Server server) {
    super(objectiveFunctions);
    this.server = server;
    this.populationDistributor = populationDistributor;
    this.taskManager = taskManager;
    this.slaveManager = slaveManager;
    this.statisticsManager = statisticsManager;
  }
  // ParameterNumber on

  /**
   * Creates standard evaluator with JavaIO serializer tools and new Jetty
   * server instance. Applicable for most uses.
   * @param objectiveFunctions List of objective functions used in evaluation.
   * @param populationDistributor Population distributor to use. If you're not
   * sure which to use, go for UniformDistributor.
   * @param serverPort Port on which the server should listen to requests.
   * @param <T> Type of individual in the population.
   * @return Configured and initialized evaluator. 
   * @throws Exception Thrown on initialization failures.
   */
  public static <T> MasterSlaveEvaluator<T> createStandardEvaluator(
      final List<CachedObjectiveFunction<T>> objectiveFunctions,
      final PopulationDistributor<T> populationDistributor,
      final int serverPort) throws Exception {
    MasterSlaveEvaluator<T> evaluator = new MasterSlaveEvaluator<T>(
        objectiveFunctions, 
        populationDistributor,
        new TaskManager<T>(),
        new SlaveManager(),
        new StatisticsManager(),
        new Server(serverPort));

    evaluator.initialize(
        new JavaIOTaskSerializer<T>(),
        new JavaIOResultSerializer<T>());
    return evaluator;
  }

  /**
   * Slightly more flexible factory method, allowing custom serialization tools.
   * @param objectiveFunctions List of objective functions used in evaluation.
   * @param populationDistributor Population distributor to use. If you're not
   * sure which to use, go for UniformDistributor.
   * @param taskSerializer Task serialization tool.
   * @param resultSerializer Result serialization tool.
   * @param serverPort Port on which the evaluator has to listen for requests.
   * @param <T> Type of individual in the population.
   * @return Configured and initialized evaluator. 
   * @throws Exception Thrown on initialization failures.
   */
  public static <T> MasterSlaveEvaluator<T> createEvaluatorWithSerializers(
      final List<CachedObjectiveFunction<T>> objectiveFunctions,
      final PopulationDistributor<T> populationDistributor,
      final TaskSerializer<T> taskSerializer,
      final ResultSerializer<T> resultSerializer,
      final int serverPort) throws Exception {
    MasterSlaveEvaluator<T> evaluator = new MasterSlaveEvaluator<T>(
        objectiveFunctions, 
        populationDistributor,
        new TaskManager<T>(),
        new SlaveManager(),
        new StatisticsManager(),
        new Server(serverPort));

    evaluator.initialize(taskSerializer, resultSerializer);
    return evaluator;
  }

  /**
   * Fully configurable factory method for creating MasterSlaveEvaluator,
   * accepting not only custom serialization tools, but also pre-configured
   * Jetty server instance.
   * @param objectiveFunctions List of objective functions used in evaluation.
   * @param populationDistributor Population distributor to use. If you're not
   * sure which to use, go for UniformDistributor.
   * @param taskSerializer Task serializer tool to use.
   * @param resultSerializer Result serializer tool to use.
   * @param <T> Type of individual in the population.
   * @param server Jetty server instance.
   * @return Configured and initialized evaluator. 
   * @throws Exception Thrown on initialization failures.
   */
  public static <T> MasterSlaveEvaluator<T>
        createEvaluatorWithSerializersAndServer(
      final List<CachedObjectiveFunction<T>> objectiveFunctions,
      final PopulationDistributor<T> populationDistributor,
      final TaskSerializer<T> taskSerializer,
      final ResultSerializer<T> resultSerializer,
      final Server server) throws Exception {
    MasterSlaveEvaluator<T> evaluator = new MasterSlaveEvaluator<T>(
        objectiveFunctions, 
        populationDistributor,
        new TaskManager<T>(),
        new SlaveManager(),
        new StatisticsManager(),
        server);

    evaluator.initialize(taskSerializer, resultSerializer);
    return evaluator;
  }

  /**
   * Getter for iteration timeout.
   * @return The iterationTimeout.
   */
  public long getIterationTimeout() {
    return iterationTimeout;
  }

  /**
   * Setter for iteration timeout.
   * @param iterationTimeout The iterationTimeout to set.
   */
  public void setIterationTimeout(long iterationTimeout) {
    this.iterationTimeout = iterationTimeout;
  }

  /**
   * Getter for the number of evaluation trials per iteration.
   * @return Number of evaluation trials per iteration.
   */
  public int getTrialsPerIteration() {
    return trialsPerIteration;
  }

  /**
   * Setter for the number of evaluation trials per iteration.
   * @param trialsPerIteration Number of evaluation trials per iteration.
   */
  public void setTrialsPerIteration(int trialsPerIteration) {
    this.trialsPerIteration = trialsPerIteration;
  }

  /**
   * Sets timeout for slaves.
   * @param slaveTimeout Timeout to set (in milliseconds);
   */
  public void setSlaveTimeout(final long slaveTimeout) {
    slaveManager.setSlaveTimeout(slaveTimeout);
  }

  /**
   * Returns timeout for slaves.
   * @return Timeout for slaves.
   */
  public long getSlaveTimeout() {
    return slaveManager.getSlaveTimeout();
  }

  /**
   * Sets minimum number of slaves to perform single evaluation.
   * @param newMinimumSlaves Lower slave limit.
   */
  public void setMinimumNumberOfSlaves(final int newMinimumSlaves) {
    minimumSlaves = newMinimumSlaves;
  }

  /**
   * Gets the minimum number of slaves to perform single evaluation.
   * @return Lower slave limit.
   */
  public int getMinimumNumberOfSlaves() {
    return minimumSlaves;
  }

  /**
   * Initializes distribution model. This method must be called before
   * the evolutionary algorithm starts.
   * @param taskSerializer Task serializer tool to use.
   * @param resultSerializer Resulr serializer tool to use.
   * @throws Exception Thrown when server fails to start.
   */
  public void initialize(
      TaskSerializer<T> taskSerializer,
      ResultSerializer<T> resultSerializer) throws Exception {
    

    Context servletContext = new Context(server, "/");

    logger.log(Level.FINE, "Registering distribution servlet.");
    servletContext.addServlet(
        new ServletHolder(
            new DistributionServlet<T>(
                taskManager,
                slaveManager,
                statisticsManager,
                taskSerializer,
                resultSerializer)),
            EXCHANGE_ADDRESS);

    logger.log(Level.FINE, "Registering registration servlet.");
    servletContext.addServlet(
        new ServletHolder(
            new RegistrationServlet(slaveManager, statisticsManager)),
            REGISTER_ADDRESS);

    logger.log(Level.FINE, "Registering statistics servlet.");
    servletContext.addServlet(
        new ServletHolder(
            new StatisticsServlet(statisticsManager)),
            STATISTICS_ADDRESS);

    try {
      logger.log(Level.INFO, "Starting server");
      server.start();

      while (server.isStarting()) {
        // Regexp off
        Thread.sleep(SLEEP_TIME);
        // Regexp on
      }
      logger.log(Level.FINE, "Server started successfully");
    } catch (InterruptedException e) {
      logger.log(Level.WARNING, "Interrupted while starting server", e);
      if (server.isFailed()) {
        logger.log(Level.SEVERE, "Failed to start Jetty server");
        throw new IllegalStateException("Server failed to start.");
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public void evaluatePopulation(final Population<T> populationInternal) {
    statisticsManager.atIterationStart(new Population<T>(populationInternal));
    int times = trialsPerIteration;
    Population<T> populationToEvaluate = populationInternal;
    while (times > 0) {
      times = Math.max(0, times - 1);

      if (iterationTimeout < 0) {
        populationToEvaluate = runUnlimitedEvaluation(populationToEvaluate);
      } else {
        populationToEvaluate = runTimeLimitedEvaluation(populationToEvaluate);
      }

      if (populationToEvaluate == null) {
        statisticsManager.atIterationEnd(
            new Population<T>(populationInternal));
        return;
      }
    }

    throw new IllegalStateException("Population may not be fully evaluated.");
  }

  /**
   * Executes single evaluation step without any time limit.
   * @param populationToEvaluate Population to be evaluated.
   * @return Null if finished, original population if failed.
   */
  private Population<T> runUnlimitedEvaluation(
      final Population<T> populationToEvaluate) {
    CountDownLatch populationEvaluatedSignal =
        startEvaluation(populationToEvaluate);

    try {
      populationEvaluatedSignal.await();
    } catch (InterruptedException e) {
      logger.log(Level.WARNING, "Interrupted while waiting for population "
          + "evaluation", e);
    }

    final Population<T> unevaluatedPopulation =
        taskManager.getUnevaluatedPart();

    // We return the unevaluated part of the population, if there is anything
    // unevaluated or null to indicate that we are finished.
    return unevaluatedPopulation.size() > 0 ? populationToEvaluate : null;
  }

  /**
   * Executes single evaluation step with time limit.
   * @param populationToEvaluate Population to be evaluated.
   * @return Null if finished or unevaluated part of the population otherwise.
   */
  private Population<T> runTimeLimitedEvaluation(
      Population<T> populationToEvaluate) {
    CountDownLatch populationEvaluatedSignal =
        startEvaluation(populationToEvaluate);

    try {
      populationEvaluatedSignal.await(iterationTimeout, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.log(Level.WARNING, "Interrupted while waiting for population "
          + "evaluation", e);
    } 

    final Population<T> unevaluatedPopulation =
        taskManager.getUnevaluatedPart();

    // We return the unevaluated part of the population, if there is anything
    // unevaluated or null to indicate that we are finished.
    return unevaluatedPopulation.size() > 0 ? unevaluatedPopulation : null;
  }

  /**
   * Start single evaluation.
   * @param populationToEvaluate Population to be evaluated.
   * @return Object to be signalled when evaluation is finished.
   */
  private CountDownLatch startEvaluation(Population<T> populationToEvaluate) {
    List<String> currentSlaves =
        slaveManager.getAvailableSlaves(minimumSlaves);

    DistributedPopulation<T> distributedPopulation =
        populationDistributor.distribute(
            populationToEvaluate, currentSlaves);

    return taskManager.enableDistribution(
        distributedPopulation,
        objectiveFunctions);
  }

  /**
   * Shuts down the evaluator. This method is mandatory to call
   * after the algorithm is finished.
   * @throws Exception Thrown on server stop failures.
   */
  public void shutdown() throws Exception {
     logger.log(Level.INFO, "Shutting down Jetty server");
     server.stop();
     logger.log(Level.FINE, "Server shut down");
  }
}
