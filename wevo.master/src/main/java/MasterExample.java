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
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import samples.objectivefunctions.OneMax;
import engine.Algorithm;
import engine.CachedObjectiveFunction;
import engine.Population;
import engine.distribution.master.MasterSlaveEvaluator;
import engine.distribution.master.UniformDistributor;
import engine.exitcriteria.MaxIterations;
import engine.individuals.BinaryVector;
import engine.operators.BestFractionSelection;
import engine.operators.binary.UniformCrossover;
import engine.operators.binary.UniformProbabilityNegationMutation;
import engine.utils.JavaRandom;
import engine.utils.ListUtils;

/**
 * Example usage of MasterEvaluator in an evolutionary algorithm.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public final class MasterExample {

  /** Logger. */
  private final Logger logger =
    Logger.getLogger(MasterExample.class.getCanonicalName());

  /** For each individual and gene probability of the mutation. */
  @Option(name = "-mp", aliases = { "mutationProbability" }, usage =
       "Probability of mutating an individual and it's single gene.")
  // MagicNumber off
  private double mutationProbability = 0.03;
  // MagicNumber on

  /** Number of iterations. */
  @Option(name = "-mi", aliases = { "maxIterations" }, usage = "Maximum number "
      + "of iterations for the algorithm to run.")
  // MagicNumber off
  private int maxIterations = 100;
  // MagicNumber on

  /** Size of vector to be optimized. */
  @Option(name = "-il", aliases = { "individualLength" }, usage = "Length of "
      + "each individual in the population.")
  // MagicNumber off
  private int individualLength = 20;
  // MagicNumber on

  /** Size of the population. */
  @Option(name = "-ps", aliases = { "populationSize" }, usage = "Size of the "
      + "population under evaluation.")
  // MagicNumber off
  private int populationSize = 1000;
  // MagicNumber on

  /** Size of the cache. */
  @Option(name = "-cs", aliases = { "cacheSize" }, usage = "Size of the cache "
      + "for evaluation results.")
  // MagicNumber off
  private int cacheSize = 2 * populationSize;
  // MagicNumber on

  /** Iteration timeout, in seconds. */
  @Option(name = "-it", aliases = { "iterationTimeout" }, usage = "Limits the "
      + "time single iteration can take, in seconds. Defaults to 30. This "
      + "option has to be adjusted carefully according to the problem size.")
  // MagicNumber off
  private int iterationTimeout = 30;
  // MagicNumber on

  /** Number of evaluation trials per iteration. */
  @Option(name = "-tpi", aliases = { "trialsPerIteration" }, usage = "In case "
      + " of failed evaluation in single iteration (due to dead slave or "
      + " I/O failure, for example), a couple of additional trials are taken. "
      + " This option limits number of such trials. Negative value indicates "
      + " infinite number of trials. Defaults to 5.")
  // MagicNumber off
  private int trialsPerIteration = 5;
  // MagicNumber on

  /** Number of evaluation trials per iteration. */
  @Option(name = "-st", aliases = { "slaveTimeout" }, usage = "This value is "
      + "used to filter out slaves that do not respond frequently enough. "
      + "Slaves that are filtered out are considered unavailable; if they "
      + "become available again, they need to re-register. Setting negative "
      + "value disables filtering. Defaults to -1.")
  // MagicNumber off
  private int slaveTimeout = -1;
  // MagicNumber on

  /** Minimum number of slaves to run single iteration. */
  @Option(name = "-ms", aliases = { "minimumSlaves" }, usage = "Indicates "
      + "the minimum number of slaves that are needed to participate in single "
      + "iteration. If there is not enough slaves registered to perform single "
      + "iteration, master waits for more to register. Defaults to 1.")
  private int minimumSlaves = 1;

  /** Port number to listen on. */
  @Option(name = "-p", aliases = { "port" }, usage = "Sets the port for Jetty "
      + "server to listen on. Defaults to 8000.")
  // MagicNumber off
  private int serverPort = 8000;
  // MagicNumber on

  /** Avoid instantiation. */
  private MasterExample() { }

  /**
   * Main program entry.
   * @param args Program arguments.
   */
  public static void main(final String[] args) {
    for (Handler handler : Logger.getLogger("").getHandlers()) {
      handler.setLevel(Level.ALL);
    }

    new MasterExample().doMain(args);
  }

  /**
   * Actually constructs and runs the algorithm.
   * @param args Program arguments.
   */
  public void doMain(final String[] args) {
    CmdLineParser parser = new CmdLineParser(this);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      parser.printUsage(System.err);
      logger.log(Level.SEVERE, "Failed to parse command line arguments", e);
      System.exit(1);
    }

    Population<BinaryVector> population =
        BinaryVector.generatePopulationOfRandomBinaryIndividuals(
            new JavaRandom(), individualLength, populationSize);

    Algorithm<BinaryVector> alg =
        new Algorithm<BinaryVector>(population);
    CachedObjectiveFunction<BinaryVector> objectiveFunctionWrapper =
        new CachedObjectiveFunction<BinaryVector>(new OneMax(), 
            cacheSize);

    try {
      MasterSlaveEvaluator<BinaryVector> masterEvaluator =
          MasterSlaveEvaluator.<BinaryVector>createStandardEvaluator(
              createObjectiveFunctionList(objectiveFunctionWrapper),
              new UniformDistributor<BinaryVector>(),
              serverPort);

      masterEvaluator.setIterationTimeout(iterationTimeout);
      masterEvaluator.setTrialsPerIteration(trialsPerIteration);
      masterEvaluator.setSlaveTimeout(slaveTimeout);
      masterEvaluator.setMinimumNumberOfSlaves(minimumSlaves);

      final double selectionFraction = 0.5;
      alg.addEvaluationPoint(masterEvaluator);
      alg.addExitPoint(new MaxIterations<BinaryVector>(maxIterations));
      alg.addOperator(new BestFractionSelection<BinaryVector>(
          new OneMax(), selectionFraction));
      alg.addOperator(new UniformCrossover(new JavaRandom()));
      alg.addOperator(new UniformProbabilityNegationMutation(
          mutationProbability, new JavaRandom()));
      alg.run();

      logger.info(alg.getPopulation().toString());

      masterEvaluator.shutdown();
    } catch (Exception exception) {
      logger.log(Level.SEVERE, "Master module encountered an error.",
          exception);
    }
  }

  /**
   * Creates objective function list.
   * @param objectiveFunctionWrapper Wrapped objective function.
   * @return List of objective functions.
   */
  @SuppressWarnings("unchecked")
  private List<CachedObjectiveFunction<BinaryVector>> 
      createObjectiveFunctionList(
          CachedObjectiveFunction<BinaryVector> objectiveFunctionWrapper) {
    return ListUtils.buildList(objectiveFunctionWrapper);
  }
}

