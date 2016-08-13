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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import samples.objectivefunctions.MaxClique;
import engine.Algorithm;
import engine.CachedObjectiveFunction;
import engine.MultiThreadedEvaluator;
import engine.Population;
import engine.exitcriteria.MaxIterations;
import engine.individuals.BinaryVector;
import engine.operators.BestFractionSelection;
import engine.operators.binary.UniformProbabilityNegationMutation;
import engine.operators.binary.UniformCrossover;
import engine.utils.JavaRandom;
import engine.utils.ListUtils;

/**
 * Examplary algorithm looking for a maximum clique in given graph.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class MaxCliqueExample {
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
  private int individualLength = 1000;
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
    new MaxCliqueExample().doMain(args);
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

    // Magic Number off
    List<List<Integer>> graphInstance = createRandomGraphInstance(100, 0.66);
    // Magic Number on

    Population<BinaryVector> population =
        BinaryVector.generatePopulationOfRandomBinaryIndividuals(
            new JavaRandom(), individualLength, populationSize);

    Algorithm<BinaryVector> alg =
        new Algorithm<BinaryVector>(population);
    CachedObjectiveFunction<BinaryVector> objectiveFunctionWrapper =
        new CachedObjectiveFunction<BinaryVector>(
            new MaxClique(graphInstance), 
            cacheSize);

    MultiThreadedEvaluator<BinaryVector> multiThreadedEvaluator =
        buildObjectiveFunctions(objectiveFunctionWrapper);

    final double fraction = 0.3;
    final double mutationProbability = 0.02;
    alg.addEvaluationPoint(multiThreadedEvaluator);
    alg.addExitPoint(new MaxIterations<BinaryVector>(maxIterations));
    alg.addOperator(new BestFractionSelection<BinaryVector>(
            objectiveFunctionWrapper, fraction));
    alg.addOperator(new UniformCrossover(new JavaRandom()));
    alg.addOperator(new UniformProbabilityNegationMutation(
        mutationProbability, new JavaRandom()));
    alg.run();

    logger.info(alg.getPopulation().toString());
  }

  /**
   * Creates random graph instance for this example.
   * @param vertices Number of graph vertices
   * @param edgesPercentage Number of edges given by a percent
   * of maximum number of edges (which is vertices*(vertices-1)/2).
   * @return Graph represented by list of neighboring nodes.
   */
  private List<List<Integer>> createRandomGraphInstance(int vertices, 
      double edgesPercentage) {

    JavaRandom random = new JavaRandom();
    List<List<Integer>> graph = new ArrayList<List<Integer>>();

    int maxEdges = (vertices * (vertices - 1)) / 2;
    int edges = (int) (maxEdges * edgesPercentage);

    for (int i = 0; i < vertices; i++) {
        graph.add(new ArrayList<Integer>());
    }

    int k = edges;
    while (k > 0) {
        int vertex = random.nextInt(0, vertices);
        int neighbor = random.nextInt(0, vertices);

        if (neighbor != vertex && !graph.get(vertex).contains(neighbor)) {
            graph.get(vertex).add(neighbor);
            graph.get(neighbor).add(vertex);
            k--;
        }
    }

    return graph;
  }

  /**
   * Reads graph instance from given file.
   * @param filename File to read graph from.
   * @return Graph object as a neighbors list.
   */
  @SuppressWarnings({ "unchecked", "unused" })
  private List<List<Integer>> readGraphInstanceFromFile(String filename) {
    try {
        FileInputStream fis = new FileInputStream(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        List<List<Integer>> graph = (List<List<Integer>>) ois.readObject();
        ois.close();
        return graph;
    } catch (IOException exception) {
        logger.log(Level.SEVERE, "Can't read from file '" + filename + "'",
                exception);
    } catch (ClassNotFoundException exception) {
        logger.log(Level.SEVERE, "Can't find class.", exception);
    }
    return null;
  }

  /**
   * Creates a list of objective functions (one in this case) to be optimized.
   * @param objectiveFunctionWrapper A function that the list is based on.
   * @return List of objective functions.
   */
  @SuppressWarnings("unchecked")
  private static MultiThreadedEvaluator<BinaryVector>
      buildObjectiveFunctions(
          CachedObjectiveFunction<BinaryVector> objectiveFunctionWrapper) {
    MultiThreadedEvaluator<BinaryVector> evaluator =
        new MultiThreadedEvaluator<BinaryVector>(
            ListUtils.buildList(objectiveFunctionWrapper));
    return evaluator;
  }
}
