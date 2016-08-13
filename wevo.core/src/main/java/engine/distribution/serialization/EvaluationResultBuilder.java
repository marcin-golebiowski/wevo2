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
package engine.distribution.serialization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for building evaluation results.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * 
 * @param <T> Type of individual in the result.
 */
public class EvaluationResultBuilder<T> {

  /** Logging utility. */
  private final Logger logger = Logger.getLogger(
      EvaluationResultBuilder.class.getCanonicalName());

  /** Evaluation result being built. */
  private List<Map<T, Double>> partialResult = new ArrayList<Map<T, Double>>();

  /**
   * Appends results of another objective function.
   * @param results Results of objective function.
   */
  public void appendObjectiveFunctionResults(Map<T, Double> results) {
    partialResult.add(results);
  }

  /**
   * Turns collected data into single evaluation result. Individual sets
   * in each mapping must be equal!
   * @return Evaluation result made from collected data.
   */
  public EvaluationResult<T> toEvaluationResult() {

    if (partialResult.isEmpty()) {
      logger.log(Level.FINER, "Returning empty evaluation result.");
      return new EvaluationResult<T>(new HashMap<T, List<Double>>());
    }

    final Set<T> individuals = new LinkedHashSet<T>(
        partialResult.get(0).keySet());
    final Map<T, List<Double>> results = new LinkedHashMap<T, List<Double>>();
    for (T individual : individuals) {
      results.put(individual, new LinkedList<Double>());
    }

    for (int i = 0; i < partialResult.size(); i++) {
      Map<T, Double> objectiveFunctionResults = partialResult.get(i);
 
      logger.log(Level.FINEST, "Merging data from objective function "
          + partialResult.get(i) + " with data:"
          + objectiveFunctionResults);

      for (T individual : individuals) {
        List<Double> functionValues = results.get(individual);
        Double nextValue = objectiveFunctionResults.get(individual);
        functionValues.add(nextValue);

        logger.log(Level.FINEST, "Individual processed: " + individual
            + " (" + nextValue + ")");
      }
    }

    final EvaluationResult<T> evaluationResult =
        new EvaluationResult<T>(results);

    logger.log(Level.FINEST, "Returning evaluation result ("
        + evaluationResult.size() + "): "
        + evaluationResult.toString());

    return evaluationResult;
  }
}
