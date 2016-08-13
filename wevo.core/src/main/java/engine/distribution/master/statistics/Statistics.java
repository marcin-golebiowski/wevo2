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

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Responsible for holding computation statistics.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class Statistics {

    /** Stores evolution start time. */
    private GregorianCalendar startTime;

    // TODO(anglart.michal): store the oldest entries from list 
    // in database instead of memory (otherwise this object can
    // grow to hundreds of megabytes during long evaluation).
    /** Holds list of single iteration statistics. */
    private Queue<IterationStatistics> iterations;

    /** Holds statistics about each slave. */
    private Map<String, SlaveStatistics> slaveStatistics;

    /** 
     * Constructor. Package visible. 
     * @param iterations List of each iterations data.
     * @param slaveStatistics Slave's statistics.
     * @param startTime Evolution start time.
     */
    Statistics(Queue<IterationStatistics> iterations,
          Map<String, SlaveStatistics> slaveStatistics,
          GregorianCalendar startTime) {
      this.iterations = iterations;
      this.slaveStatistics = slaveStatistics;
      this.startTime = startTime;
    }

    /** Constructor. */
    public Statistics() {
      this(new LinkedList<IterationStatistics>(),
          new HashMap<String, SlaveStatistics>(),
          new GregorianCalendar());
    }

    /**
     * Gets evolution start time.
     * @return GregorianCalendar object which 
     * stores time when evolution started.
     */
    public GregorianCalendar getStartTime() {
      return startTime;
    }

    /**
     * Gets slave's statistics.
     * @return Slave's statistics registry.
     */
    public Map<String, SlaveStatistics> getSlaveStatistics() {
        return Collections.unmodifiableMap(slaveStatistics);
    }

    /**
     * Gets list of all iterations data stored in memory.
     * @param firstIteration First iteration number which should be on list.
     * @param lastIteration Last iteration number which shoud be on list.
     * @return List with all iterations data. 
     */
    public List<IterationStatistics> getIterations(
        long firstIteration, long lastIteration) {
      // TODO(anglart.michal): implement more clever way of
      // getting this data (iterator probably), because majority
      // of this data will be stored in database.
      LinkedList<IterationStatistics> iters =
          new LinkedList<IterationStatistics>(iterations);
      LinkedList<IterationStatistics> returnList =
          new LinkedList<IterationStatistics>();

      for (IterationStatistics iteration : iters) {
        long itNumber = (long) iteration.getCounterValue(
            IterationStatistics.ITERATION_NUMBER);

        if (itNumber >= firstIteration && itNumber <= lastIteration) {
          returnList.add(iteration);
        }
      }
      return returnList;
    }

    /**
     * Gets statistic data of given iteration.
     * @param iterationNumber Iteration number.
     * @return Iteration statistics. Null if there is no such number.
     */
    public IterationStatistics getIteration(long iterationNumber) {
      // TODO(anglart.michal): this not too smart. But it should wait
      // for database statistics storing.

      LinkedList<IterationStatistics> iters =
          new LinkedList<IterationStatistics>(iterations);

      for (IterationStatistics iteration : iters) {
        long itNumber = (long) iteration.getCounterValue(
            IterationStatistics.ITERATION_NUMBER);

        if (itNumber == iterationNumber) {
          return iteration;
        }
      }
      return null;
    }

    /**
     * Adds statistical data about iteration.
     * @param iterationData Iteration statistical data.
     */
    public void addIterationData(IterationStatistics iterationData) {
      iterations.add(iterationData);
    }

    /**
     * Adds new slave and creates it's statistics object.
     * @param slaveId Slave's identifier.
     * @param firstIteration Iteration when slave have joined.
     */
    public void addSlave(String slaveId, long firstIteration) {
      SlaveStatistics slaveStats = new SlaveStatistics(firstIteration);
      slaveStatistics.put(slaveId, slaveStats);
    }
}
