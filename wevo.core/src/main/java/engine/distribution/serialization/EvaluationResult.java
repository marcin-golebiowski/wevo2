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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Evaluation result returned by slaves.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michał Anglart (anglart.michal@gmail.com)
 * 
 * @param <T> Type of the individual evaluated.
 */
public class EvaluationResult<T> implements Serializable {

  /** Generated serial version UID. */
  private static final long serialVersionUID = -7594432628684891270L;

  /** Evaluation result. */
  private Map<T, List<Double>> result;

  /**
   * Constructor.
   * @param newResult Result to be wrapped.
   */
  public EvaluationResult(Map<T, List<Double>> newResult) {
    this.result = newResult;
  }

  /**
   * Returns mapping from individuals to their objective function values.
   * @param objectiveFunctionNo Number of the objective function to return
   * values for.
   * @return Mapping from individuals to their objective function values.
   */
  public Map<T, Double> getResult(final int objectiveFunctionNo) {
    Map<T, Double> singleResult = new LinkedHashMap<T, Double>();
    for (T individual : result.keySet()) {
      singleResult.put(individual,
          result.get(individual).get(objectiveFunctionNo));
    }
    return singleResult;
  }

  /**
   * Returns size of the evaluation result.
   * @return Size of the evaluation result.
   */
  public int size() {
    return result.size();
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return result.toString();
  }
}
