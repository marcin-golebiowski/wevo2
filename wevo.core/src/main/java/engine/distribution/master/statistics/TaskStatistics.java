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

import java.util.GregorianCalendar;

/**
 * Represents statistical data about slave's single 
 * step during cumputation.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class TaskStatistics {

    /** Iteration number. */
    private final long iterationNumber;

    /** Number of individuals in population. */
    private final int populationSize;

    /** Sum of individuals size in bytes. */
    private final int populationSizeInBytes;

    /** Exact time when slave started evaluation. */
    private GregorianCalendar evaluationStartTime;

    /** Exact time when slave ended evaluation. */
    private GregorianCalendar evaluationEndTime;

    /**
     * Constructor. Package visibility for testing.
     * @param iterationNumber Iteration number.
     * @param populationSize Size of population.
     * @param populationSizeInBytes Size of population in bytes.
     * @param evaluationStartTime Time when evaluation started.
     * @param evaluationEndTime Time when evaluation ended.
     */
    TaskStatistics(
        final long iterationNumber,
        final int populationSize, 
        final int populationSizeInBytes,
        final GregorianCalendar evaluationStartTime,
        final GregorianCalendar evaluationEndTime) {
      this.iterationNumber = iterationNumber;
      this.populationSize = populationSize;
      this.populationSizeInBytes = populationSizeInBytes;
      this.evaluationStartTime = evaluationStartTime;
      this.evaluationEndTime = evaluationEndTime;
    }
    // ParameterNumber on

    /**
     * Constructor. Times are null since they should be set by
     * special setter methods.
     * @param iterationNumber Iteration number.
     * @param populationSize Size of population.
     * @param populationSizeInBytes Size of population in bytes.
     */
    public TaskStatistics(
        long iterationNumber,
        int populationSize,
        int populationSizeInBytes) {
      this(iterationNumber, 
          populationSize, 
          populationSizeInBytes, 
          null, 
          null);
    }

    /**
     * Getter returning iteration number.
     * @return Iteration number.
     */
    public long getIterationNumber() {
      return this.iterationNumber;
    }

    /**
     * Getter returning size of population.
     * @return Size of evaluated population.
     */
    public int getPopulationSize() {
      return this.populationSize;
    }

    /**
     * Getter returning size of population in bytes.
     * @return Size of evaluated population in bytes.
     */
    public int getPopulationSizeInBytes() {
      return this.populationSizeInBytes;
    }

    /** Sets evaluation start time. */
    public void setEvaluationStartTime() {
      this.evaluationStartTime = new GregorianCalendar();
    }

    /** Sets evaluation end time. */
    public void setEvaluationEndTime() {
      this.evaluationEndTime = new GregorianCalendar();
    }

    /**
     * Calculates evaluation time.
     * @return Duration of evaluation in milliseconds.
     */
    public int singleIterationTime() {
      // This cast is safe since we assume that evaluation is not too long
      // and int number can store few hundreds of hours in milliseconds
      // representation.
      return (int) (this.evaluationEndTime.getTimeInMillis()
          - this.evaluationStartTime.getTimeInMillis());
    }
}
