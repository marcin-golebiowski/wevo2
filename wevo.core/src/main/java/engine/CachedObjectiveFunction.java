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

package engine;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import engine.utils.LruMap;

/**
 * Wrapper for an objective function that caches the result of computation.
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individual being evaluated.
 */
public class CachedObjectiveFunction<T> implements ObjectiveFunction<T> {
  /** Parent objective function that will be cached. */
  private final ObjectiveFunction<T> function;

  /** Map &mdash; cache from individual to its value. */
  private final LruMap<T, Double> map;
 
  /** Logger. */
  private final Logger logger = Logger.getLogger(
      CachedObjectiveFunction.class.getCanonicalName());

  /**
   * Creates caching wrapper for given objective function.
   * @param function Objective function to be cached.
   * @param cacheSize Size of the cache.
   */
  public CachedObjectiveFunction(ObjectiveFunction<T> function, 
      int cacheSize) {
    this.function = function;
    map = new LruMap<T, Double>(cacheSize);
  }

  /**
   * Precomputes value of the objective function.
   * @param individual Individual to be evaluated.
   */
  void computeInternal(T individual) {
    logger.fine("Computing value of objective function for " + individual);
    if (map.containsKey(individual)) {
      map.get(individual); // Update the access time!
      return;
    }
    double v = function.compute(individual);
    synchronized (map) {
      map.put(individual, v);
    }
  }

  /** {@inheritDoc} */
  public double compute(T o) {
    if (!map.containsKey(o)) {
      throw new IllegalStateException("Cache of objective function values " 
          + "does not contain entry for " + o);
    }
    return map.get(o);
  }

  /**
   * Merges this function's cache with cache of the argument.
   * @param input Objective function that contains the cache
   * to merge.
   */
  public void merge(Map<T, Double> input) {
    map.putAll(input);
  }

  /**
   * Returns immutable cache copy.
   * @return Immutable cache copy.
   */
  public Map<T, Double> getCache() {
    return Collections.unmodifiableMap(map);
  }
}
