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

import samples.objectivefunctions.OneMax;
import engine.Algorithm;
import engine.CachedObjectiveFunction;
import engine.Population;
import engine.PopulationEvaluator;
import engine.SingleThreadedEvaluator;
import engine.exitcriteria.MaxIterations;
import engine.individuals.BinaryVector;
import engine.operators.BestFractionSelection;
import engine.operators.binary.UniformCrossover;
import engine.operators.binary.UniformProbabilityNegationMutation;
import engine.utils.JavaRandom;
import engine.utils.ListUtils;

/**
 * Simple Genetic Algorithm implementation using wevo. Optimizes OneMax.
 * 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 */
public class SGA {
  /** Logger. */
  private final Logger logger =
    Logger.getLogger(SGA.class.getCanonicalName());

  /** Fraction of individuals being saved in next population. */
  @Option(name = "-f", aliases = { "fraction" }, usage = "Fraction of "
      + "individuals being saved in next population.")
  // MagicNumber off
  private double fraction = 0.5;
  // MagicNumber on

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

  /**
   * Main program routine.
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

    Population<BinaryVector> population =
      BinaryVector.generatePopulationOfRandomBinaryIndividuals(
          new JavaRandom(), individualLength, populationSize);

    Algorithm<BinaryVector> alg =
        new Algorithm<BinaryVector>(population);
    CachedObjectiveFunction<BinaryVector> objectiveFunctionWrapper =
        new CachedObjectiveFunction<BinaryVector>(new OneMax(),
            cacheSize);
    alg.addExitPoint(new MaxIterations<BinaryVector>(maxIterations));
    alg.addEvaluationPoint(
        buildObjectiveFunctions(objectiveFunctionWrapper));
    alg.addOperator(new BestFractionSelection<BinaryVector>(new OneMax(),
        fraction));
    alg.addOperator(new UniformCrossover(new JavaRandom()));
    alg.addOperator(new UniformProbabilityNegationMutation(mutationProbability,
        new JavaRandom()));
    alg.run();

    // Regexp off
    // This is just a sample - so we relax the style rules.
    System.out.println(alg.getPopulation().toString());
    // Regexp on
  }

  /**
   * Entry point to the program.
   * @param args Command line arguments.
   */
  public static void main(final String[] args) {
    new SGA().doMain(args);
  }

  /**
   * Creates a list of objective functions (one in this case) to be optimized.
   * @param objectiveFunctionWrapper A function that the list is based on.
   * @return List of objective functions.
   */
  @SuppressWarnings("unchecked")
  private static PopulationEvaluator<BinaryVector> 
      buildObjectiveFunctions(CachedObjectiveFunction<BinaryVector>
          objectiveFunctionWrapper) {
    return new SingleThreadedEvaluator<BinaryVector>(
        ListUtils.buildList(objectiveFunctionWrapper));
  }
}
