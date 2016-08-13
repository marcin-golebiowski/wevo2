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

import java.io.File;
import java.io.FileWriter;
import java.util.logging.Logger;

/**
 * A benchmark for portfolio optimization algorithm. Pretends to be an investor 
 * playing on a stock market, using {@link PortfolioOptimizationAlgorithm}.
 * At the end of simulation spits out result of long-term play.
 * 
 * This is a very preliminary version, don't look too strongly at it yet.
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class PortfolioOptimizationBenchmark {
 
  /** How quickly we have to make money on the project. */
  private static final int DAYS_TO_BRING_PROFIT = 30;

  /** How many iterations to test. */
  private static final int ITERATIONS = 27;

  // TODO(marcin.brodziak): move things below to flags. 
  /** How much money do we have in the pocket? */
  private static final int MONEY_IN_THE_POCKET = 5000;

  /** Size of iteration. */
  private static final int ITERATION_SIZE = 50;

  /** Data source. */
  private static String dataSource = 
      "/Users/marcinb/Desktop/private-docs/git-repo/gielda/" 
      + "output/allStocksWithDates";

  /** Logger. */
  private final Logger logger = Logger.getLogger(
      PortfolioOptimizationBenchmark.class.getCanonicalName());

  /** 
   * Structure representing result of a simulation.
   * Similar to {@link AlgorithmDecisionProposal}, but mutable.
   * @author Marcin Brodziak (marcin.brodziak@gmail.com)
   */
  private static class SimulationResultStruct {
    // Visibility Modifier off
    // TODO(marcin.brodziak): Change variables visibility to private
    // and add accessor methods.

    /** Portfolio name. */
    public String portfolio;

    /** AMount of money in the pocket. */
    public double moneyInThePocket = 0.0;

    /** Cost of the transaction. */
    public double transactionCost = 0.0;

    /** Value of the porftolio. */
    public double portfolioValue = 0.0;

    /** Number of days to bring the profit. */
    public double daysToBringProfit = 0.0;
    // Visibility Modifier on

    /** {@inheritDoc} */
    @Override
    public String toString() {
      return moneyInThePocket + " " + transactionCost 
          + " " + portfolioValue + " " + daysToBringProfit 
          + " " + portfolio;
    }
  }

  /**
   * Main program entry.
   * @param args Ignored.
   * @throws Exception Whatever {@link PortfolioOptimizationAlgorithm} 
   *    decides to throw.
   */
  public static void main(String[] args) throws Exception {
    new PortfolioOptimizationBenchmark().doMain();
  }

  /**
   * Business as usual.
   * @throws Exception Whatever {@link PortfolioOptimizationAlgorithm} 
   *    decides to throw.
   */
  public void doMain() throws Exception {
    String bestPortfolio = "";
    // This will be used later to analyze the performance of the program in R.
    StringBuilder outputForR = new StringBuilder();

    SimulationResultStruct finalResult = new SimulationResultStruct();
    finalResult.moneyInThePocket = MONEY_IN_THE_POCKET;

    StringBuilder rScript = new StringBuilder();
    rScript.append("moneyInThePocket = rep(" + MONEY_IN_THE_POCKET 
        + "," + ITERATION_SIZE + ")\n");
    rScript.append("rollingPortfolio = rep(" + 0.0 
        + "," + ITERATION_SIZE + ")\n");
    rScript.append("learnedFrom = c()\n");

    for (int i = 0; i < ITERATIONS; i++) {
      PortfolioOptimizationAlgorithm algorithm = 
          new PortfolioOptimizationAlgorithm();
      algorithm.parseCommandLine(new String[] {"--dataSource", 
          dataSource + i + ".csv",
          "--initialPortfolio", bestPortfolio,
          "--maxPortfolioValueChange", finalResult.moneyInThePocket + "" });
      algorithm.buildObjectiveFunction();
      algorithm.runAlgorithm();

      AlgorithmDecisionProposal proposal = algorithm.getDecisionProposal();
      if (proposal != null
          && proposal.getDaysToBringProfit() < DAYS_TO_BRING_PROFIT) {
        bestPortfolio = proposal.getPortfolio();

        logger.info("Using portfolio " + proposal);
        outputForR.append("lines(" + proposal.getPortfolio() + ");\n");
        finalResult.portfolio = proposal.getPortfolio();
        finalResult.portfolioValue = proposal.getPortfolioValue();
        finalResult.moneyInThePocket += proposal.getAdditionalMoney() 
            + proposal.getTransactionCost();
        finalResult.transactionCost += proposal.getTransactionCost();
      }

      String iterationPortfolio = "portfolioFromIteration";
      rScript.append("moneyInThePocket = c(moneyInThePocket, rep(" 
          + finalResult.moneyInThePocket + "," + ITERATION_SIZE + "))\n");
      rScript.append(iterationPortfolio + " = " + bestPortfolio + "\n");
      rScript.append("learnedFrom =  c(learnedFrom, " + iterationPortfolio
          + "[" + (i * ITERATION_SIZE) + ":" 
          + ((i + 1) * ITERATION_SIZE - 1) + "])\n");
      rScript.append(iterationPortfolio + " = " + iterationPortfolio 
          + "[" + ((i + 1) * ITERATION_SIZE) + ":" 
          + ((i + 2) * ITERATION_SIZE - 1) + "]\n");
      rScript.append("rollingPortfolio = c(rollingPortfolio, " 
          + iterationPortfolio + ")\n");
    }

    // Regexp off
    // TODO(marcin.brodziak): you should probably inject an output stream
    // instead of using System.out all the time.
    System.out.println("Last portfolio " + finalResult.portfolio);
    System.out.println("Money in the pocket " + finalResult.moneyInThePocket);
    System.out.println("Cost of all transactions " 
        + finalResult.transactionCost);
    System.out.println("Value of last portfolio "
        + finalResult.portfolioValue);
    System.out.println(outputForR);
    System.out.println(rScript);
    // Regexp on

    FileWriter writer = new FileWriter(new File("/tmp/finalResult.r"));
    writer.append(rScript);
    writer.close();
  }
}
