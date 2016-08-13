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

/** 
 * The result of a single iteration of benchmarking.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class AlgorithmDecisionProposal {
  /** Better portfolio. */
  private final String portfolio;
  /** Additional money needed to transition to this portfolio. */
  private final double additionalMoney;
  /** Cost of transition. */
  private final double transactionCost;
  /** Current portfolio value. */
  private final double portfolioValue;
  /** Days it will take for this potrfolio to pay off. */
  private final double daysToBringProfit;

  /**
   * @param portfolio Better portfolio.
   * @param additionalMoney Money needed to pay.
   * @param transactionCost Cost of transitioning to this portfolio.
   * @param portfolioValue Value of portfolio (current). 
   * @param daysToBringProfit Days to bring protif.
   */
  public AlgorithmDecisionProposal(String portfolio, double additionalMoney,
      double transactionCost, double portfolioValue, double daysToBringProfit) {
    this.portfolio = portfolio;
    this.additionalMoney = additionalMoney;
    this.transactionCost = transactionCost;
    this.portfolioValue = portfolioValue;
    this.daysToBringProfit = daysToBringProfit;
  }

  /**
   * @return the portfolio
   */
  public final String getPortfolio() {
    return portfolio;
  }

  /**
   * @return the additionalMoney
   */
  public final double getAdditionalMoney() {
    return additionalMoney;
  }

  /**
   * @return the transactionCost
   */
  public final double getTransactionCost() {
    return transactionCost;
  }

  /**
   * @return the portfolioValue
   */
  public final double getPortfolioValue() {
    return portfolioValue;
  }

  /**
   * @return the daysToBringProfit
   */
  public final double getDaysToBringProfit() {
    return daysToBringProfit;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return portfolio + " " + additionalMoney + " " + transactionCost 
        + " " + portfolioValue + " " + daysToBringProfit;
  }
}
