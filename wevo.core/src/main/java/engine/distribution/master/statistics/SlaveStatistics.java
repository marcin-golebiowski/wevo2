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
package engine.distribution.master.statistics;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents statistics about single slave.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class SlaveStatistics {

    /** Stores iteration number when this slave have joined evaluation. */
    private long firstIteration;

    /** Stores number of iterations in which slave was involved. */
    private long iterations;

    /** Stores all the counters associated with slave. */
    private Map<String, Double> counters;

    /**
     * Constructor. Package visibility for testing purposes.
     * @param firstIteration Iteration number when slave joined evaluation.
     * @param iterations Number of involved iterations. 
     * @param counters Stores counters associated with slave.
     */
    SlaveStatistics(long firstIteration, long iterations,
          Map<String, Double> counters) {
      this.firstIteration = firstIteration;
      this.iterations = iterations;
      this.counters = counters;
    }

    /**
     * Constructor.
     * @param firstIteration Iteration number when slave joined evaluation.
     */
    public SlaveStatistics(long firstIteration) {
      this(firstIteration, 0, new HashMap<String, Double>());
    }

    /**
     * Returns iteration number when slave have joined. 
     * @return Value of firstIteration.
     */
    public long getFirstIteration() {
        return firstIteration;
    }

    /**
     * Adds new counter.
     * @param counterName Name of counter.
     * @param value Counter start value.
     */
    public void addCounter(String counterName, Double value) {
      if (!counters.containsKey(counterName)) {
        counters.put(counterName, value);
      }
    }

    /**
     * Sets new value on given counter. 
     * @param counterName Name of counter.
     * @param value Value to set.
     */
    public void setCounterValue(String counterName, Double value) {
      counters.put(counterName, value);
    }

    /**
     * Returns value of given counter.
     * @param counterName Name of counter.
     * @return Counter value.
     */
    public Double getCounterValue(String counterName) {
      return counters.get(counterName);
    }

    /** Increments iteration (should be called in each iteration). */
    public void incrementIteration() {
      iterations++;
    }
}
