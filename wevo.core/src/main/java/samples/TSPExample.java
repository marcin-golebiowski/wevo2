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
package samples;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import samples.objectivefunctions.EuclideanTSP;
import engine.Algorithm;
import engine.CachedObjectiveFunction;
import engine.Population;
import engine.SingleThreadedEvaluator;
import engine.exitcriteria.MaxIterations;
import engine.individuals.Permutation;
import engine.operators.BestFractionSelection;
import engine.operators.permutation.InversionMutation;
import engine.operators.permutation.PMXCrossover;
import engine.utils.JavaRandom;
import engine.utils.ListUtils;

/**
 * Exemplary solution to TSP problem.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public final class TSPExample {

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

  /** Logger. */
  private final Logger logger =
      Logger.getLogger(TSPExample.class.getCanonicalName());

  /**
   * Main program entry.
   * @param args Command line args.
   */
  public static void main(String[] args) {
    new TSPExample().doMain(args);
  }

  /**
   * Actually constructs and runs the algorithm.
   * @param args Command line arguments.
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

    Population<Permutation> population =
        Permutation.generatePopulationOfRandomIndividuals(
            new JavaRandom(), individualLength, populationSize);

    Algorithm<Permutation> alg =
        new Algorithm<Permutation>(population);
    CachedObjectiveFunction<Permutation> objectiveFunctionWrapper =
        new CachedObjectiveFunction<Permutation>(
            new EuclideanTSP(), 
            cacheSize);

    SingleThreadedEvaluator<Permutation> singleThreadedEvaluator =
        buildObjectiveFunctions(objectiveFunctionWrapper);

    final double fraction = 0.3;
    final double mutationProbability = 0.02;
    alg.addEvaluationPoint(singleThreadedEvaluator);
    alg.addExitPoint(new MaxIterations<Permutation>(maxIterations));
    alg.addOperator(
        new BestFractionSelection<Permutation>(
            objectiveFunctionWrapper, fraction));
    alg.addOperator(
        new PMXCrossover(new JavaRandom(), 1, 2));
    alg.addOperator(
        new InversionMutation(
            new JavaRandom(), mutationProbability));
    alg.run();

    logger.info(alg.getPopulation().toString());
  }

  /**
   * Creates a list of objective functions (one in this case) to be optimized.
   * @param objectiveFunctionWrapper A function that the list is based on.
   * @return List of objective functions.
   */
  @SuppressWarnings("unchecked")
  private static SingleThreadedEvaluator<Permutation>
      buildObjectiveFunctions(
          final CachedObjectiveFunction<Permutation> objectiveFunctionWrapper) {
    SingleThreadedEvaluator<Permutation> evaluator =
        new SingleThreadedEvaluator<Permutation>(
            ListUtils.buildList(objectiveFunctionWrapper));
    return evaluator;
  }
}

