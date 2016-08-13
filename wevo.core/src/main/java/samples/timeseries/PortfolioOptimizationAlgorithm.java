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
package samples.timeseries;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import engine.Algorithm;
import engine.CachedObjectiveFunction;
import engine.MultiThreadedEvaluator;
import engine.Population;
import engine.exitcriteria.IndividualHasntImproved;
import engine.individuals.NaturalVector;
import engine.operators.BestFractionSelection;
import engine.operators.CombineBestWithOtherOperator;
import engine.operators.natural.GaussianAdditionMutation;
import engine.operators.natural.IntervalCutoff;
import engine.operators.reporters.BestIndividualAndBasicStats;
import engine.utils.JavaRandom;
import engine.utils.WevoRandom;

/**
 * Sample portfolio optimization algorithm. It reads specified time series 
 * and optimizes it according to {@link PortfolioObjectiveFunction}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class PortfolioOptimizationAlgorithm {
 
  /** How many best individuals survive an iteration. */
  private static final double FRACTION_OF_SURVIVORS = 0.9;

  // MagicNumber off
  /** Commission fee percentage. */
  private static final double PERCENTAGE = 0.01;

  /** The source of stock values. */
  @Option(name = "--initialPortfolio",
      usage = "Initial portfolio. Mathematical form, i.e GOOG*3+YHOO*5")
  private String initialPortfolio = "";
 
  /** The source of stock values. */
  @Option(name = "--dataSource",
      usage = "The source of stock values data.")
  private String dataSource = "";

  /** Expected number of stocks in the portfolio. */
  @Option(name = "--expectedNumberOfStocks",
      usage = "Expected number of stocks.")
  private int expectedNumberOfStocks = 10;

  /** Strength of the mutation. */
  @Option(name = "--sigma",
      usage = "Standard deviation in mutation.")
  private double sigma = 5.0;

  /** Probability of mutation in the algorithm. */
  @Option(name = "--probability",
      usage = "Probability of mutation of single gene.")
  private double probability = 0.03;

  /** Maximum number of iterations. */
  @Option(name = "--maxIterationsWithNoChange",
      usage = "Maximum number of iterations with indiviual not being changed.")
  private int maxIterationsWithNoChange = 5;

  /** Population size is equal to size of portfolio times this number. */
  @Option(name = "--populationSizeMultiplier",
      usage = "Population size will be equal to this times number of " 
          + "stocks to choose from.")
  private long populationSizeMultiplier = 10;

  /** Portfolio will never have more than expected +- this number of stocks. */
  @Option(name = "--maxStocksNoError",
      usage = "Maximum difference from expected number of stocks.")
  private int maxStocksNoError = 2;

  /** When improving an existing portfolio, we'll never pay more than this. */
  @Option(name = "--maxPortfolioValueChange",
      usage = "Maximum money we're willing to pay to invest in " 
          + "a better portfolio, used only if improving an existing portfolio")
  private double maxPortfolioValueChange = Double.MAX_VALUE;
  // MagicNumber on

  /** Portfolio of time series to be evaluated. */
  private TimeSeriesPortfolio portfolio;

  /** Best individual in the population. */
  private NaturalVector bestIndividual;

  /** How many days it will take for new portfolio to pay off. */
  private double daysToBringProfit;

  /** Objective functions to optimize. */
  private List<CachedObjectiveFunction<NaturalVector>> objectiveFunctions;

  /**
   * Main entry to the program.
   * @param args Ignored.
   * @throws IOException If source file cannot be read.
   */
  public static void main(String[] args) throws IOException {
    PortfolioOptimizationAlgorithm algorithm 
        = new PortfolioOptimizationAlgorithm();
    algorithm.parseCommandLine(args);
    algorithm.buildObjectiveFunction();
    algorithm.runAlgorithm();
  }

  /**
   * Parses command line arguments. Making public so that benchmark
   * can call it directly.
   * @param args Command line arguments to parse.
   * @throws IOException when help cannot be written. 
   */
  public void parseCommandLine(String[] args) throws IOException {
    CmdLineParser parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      parser.printUsage(System.out);
    }
    if ("".equals(dataSource)) {
      parser.printUsage(System.out);
      System.exit(-1);
    }
  }

  /**
   * Creates the algorithm and runs it.
   * @throws IOException When data on which to learn cannot be read.
   */
  void runAlgorithm() throws IOException {
    Population<NaturalVector> population =
        new Population<NaturalVector>(buildEmptyPopulation(portfolio));

    Algorithm<NaturalVector> alg =
        new Algorithm<NaturalVector>(population);
    MultiThreadedEvaluator<NaturalVector> multiThreadedEvaluator 
        = new MultiThreadedEvaluator<NaturalVector>(objectiveFunctions);
    alg.addEvaluationPoint(
        multiThreadedEvaluator);
    alg.addExitPoint(new IndividualHasntImproved<NaturalVector>(
        maxIterationsWithNoChange, objectiveFunctions.get(0)));
    alg.addOperator(new 
        BestIndividualAndBasicStats<NaturalVector>(objectiveFunctions, 
            new PortfolioInterpretation(portfolio)));
    alg.addOperator(
        new BestFractionSelection<NaturalVector>(objectiveFunctions.get(0), 
            FRACTION_OF_SURVIVORS));
    WevoRandom random = new JavaRandom();
    alg.addOperator(
        new CombineBestWithOtherOperator<NaturalVector>(
            objectiveFunctions,
            new GaussianAdditionMutation(
                random, probability, sigma),
            random));
    alg.addOperator(new IntervalCutoff(0, Integer.MAX_VALUE));
    alg.run();

    multiThreadedEvaluator.shutDown();
    printBestIndividual(objectiveFunctions.get(0), 
        alg.getPopulation());
  }

  /**
   * Prints to stdout the best individual found in the population.
   * @param objectiveFunction Objective function to score the individual.
   * @param finalPopulation Population to print.
   */
  private void printBestIndividual(
      final CachedObjectiveFunction<NaturalVector> objectiveFunction,
      final Population<NaturalVector> finalPopulation) {
    bestIndividual = 
        Collections.max(finalPopulation.getIndividuals(), 
            new Comparator<NaturalVector>() {
              public int compare(NaturalVector o1, NaturalVector o2) {
                return objectiveFunction.compute(o1) 
                    > objectiveFunction.compute(o2) ? 1 : -1;
              }
            });

    long[] originalPortfolio = 
        portfolio.parseMathematicalExpression(this.initialPortfolio);
    double costOfTransition = portfolio.costOfTransition(
        originalPortfolio, 
        bestIndividual.getValues(), PERCENTAGE);
    double additionalMoney = portfolio.differenceOfValues(
        originalPortfolio, bestIndividual.getValues());

    double gainPerDay = 
        portfolio.toTimeSeries(bestIndividual.getValues()).getSlope() 
            - portfolio.toTimeSeries(originalPortfolio).getSlope();

    // Regexp off
    // TODO(marcin.brodziak): you should probably inject output stream
    // and then write to it. It will be more flexible that way.
    System.out.print(
        portfolio.toMathematicalExpression(bestIndividual.getValues()));
    System.out.print(" ");
    System.out.print(additionalMoney);
    System.out.print(" ");
    System.out.print(costOfTransition);
    System.out.print(" ");
    daysToBringProfit = gainPerDay > 0
        ? costOfTransition / gainPerDay : Double.MAX_VALUE;
    System.out.println(daysToBringProfit);
    // Regexp on
  }

  /** 
   * Returns an initial population of individuals.
   * @param targetPortfolio Portfolio for which individuals should be created.
   * @return Population of initial individuals.
   */
  private List<NaturalVector> buildEmptyPopulation(
      TimeSeriesPortfolio targetPortfolio) {
    long[] initialPortfolioWeights = 
        portfolio.parseMathematicalExpression(this.initialPortfolio);
    List<NaturalVector> vectors = new LinkedList<NaturalVector>();
    for (int i = 0; i < targetPortfolio.getSize() 
        * populationSizeMultiplier; i++) {
      NaturalVector naturalVector = 
          new NaturalVector(initialPortfolioWeights);
      vectors.add(naturalVector);
    }
    return vectors;
  }

  /**
   * Creates a set of objective functions for portfolio optimization. Default
   * visibility enables calling from benchmark. 
   * @throws IOException When source file cannot be read.
   */
  void buildObjectiveFunction() throws IOException {
    portfolio = TimeSeriesPortfolioFactory.readCsvFile(
        dataSource);
    portfolio.getSize();
 
    List<CachedObjectiveFunction<NaturalVector>> list = 
        new LinkedList<CachedObjectiveFunction<NaturalVector>>();
    long[] basePortfolio = "".equals(initialPortfolio) ? null 
        : portfolio.parseMathematicalExpression(initialPortfolio);
    list.add(
        new CachedObjectiveFunction<NaturalVector>(
            new PortfolioObjectiveFunction(portfolio, 
                expectedNumberOfStocks, maxStocksNoError,
                basePortfolio, maxPortfolioValueChange), 
                Integer.MAX_VALUE)); 
            // TODO(marcin.brodziak): sanitize this value
    objectiveFunctions = list;
  }

  /**
   * @return A proposal for what to do. 
   */
  public AlgorithmDecisionProposal getDecisionProposal() {
    long[] previousPortfolio = 
        this.portfolio.parseMathematicalExpression(this.initialPortfolio);
    long[] currentPortfolio = this.bestIndividual.getValues();

    double costOfTransaction = 
        this.portfolio.costOfTransition(previousPortfolio, 
            currentPortfolio, PERCENTAGE);
    boolean isWorthInvesting = 
        objectiveFunctions.get(0).compute(bestIndividual) > 0;
    if (!isWorthInvesting) {
      return null;
    }

    return new AlgorithmDecisionProposal(
        portfolio.toMathematicalExpression(bestIndividual.getValues()),
        portfolio.differenceOfValues(currentPortfolio, 
            previousPortfolio), costOfTransaction, 
        portfolio.getValue(bestIndividual.getValues()),
        daysToBringProfit);
  }
}
