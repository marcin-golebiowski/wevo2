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
package engine.distribution.master;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import engine.Population;
import engine.distribution.master.statistics.IterationStatistics;
import engine.distribution.master.statistics.Statistics;
import engine.distribution.master.statistics.StatisticsHtmlPrinter;
import engine.distribution.serialization.EvaluationResult;
import engine.distribution.serialization.EvaluationTask;
import engine.utils.ClockUtilities;

/**
 * Utility responsible for storing history and statistic
 * of evaluation. It keeps statistical data about each
 * slave which participate in computation and also 
 * general statistics about algorithm.
 * TODO(anglart.michal): decorate with loggers.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class StatisticsManager {

    /** General statistic about algorithm. */
    private Statistics generalStatistic;

    /** Counter which holds the counter of iteration. */
    private AtomicLong iterationCounter;

    /** Gathered single iteration statistics. */
    private IterationStatistics iterationStatistics;

    /**
     * Constructor.
     * Package visibility for testing purposes.
     * @param generalStatistic General statistics about algorithm.
     * @param iterationCounter Iteration counter.
     */
    StatisticsManager(Statistics generalStatistic, long iterationCounter) {
      this.generalStatistic = generalStatistic;
      this.iterationCounter = new AtomicLong(iterationCounter);
      this.iterationStatistics = null;
    }

    /** Constructor. */
    public StatisticsManager() {
      this(new Statistics(), 1L);
    }

    /**
     * Getter for iteration counter.
     * @return Iteration counter.
     */
    public long getIterationCounter() {
      return iterationCounter.get();
    }

    /**
     * Gets string representation of evolution start time.
     * @return Evolution start time as string.
     */
    public String getStartTimeString() {
      GregorianCalendar calendar = generalStatistic.getStartTime();

      String ampm = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
      return calendar.get(Calendar.DAY_OF_MONTH) + "."
          + (calendar.get(Calendar.MONTH) + 1) + "."
          + calendar.get(Calendar.YEAR) + " " 
          + calendar.get(Calendar.HOUR) + ":" 
          + calendar.get(Calendar.MINUTE) + ":" 
          + calendar.get(Calendar.SECOND) + " " + ampm;
    }

    /**
     * Returns total running time of evolution.
     * @return Time given in seconds.
     */
    public double getRunningTime() {
      return ClockUtilities.getTimeSpanInSeconds(
          generalStatistic.getStartTime());
    }

    /**
     * Gets all slave's identifiers.
     * @return List of slave's identifiers.
     */
    public List<String> getSlaves() {
      ArrayList<String> slaves = 
          new ArrayList<String>(generalStatistic.getSlaveStatistics().keySet());
      return slaves;
    }

    /**
     * Getter for iterations statistics list. Gets iterations
     * from given range.
     * @param lastIteration Last iteration number which shoud be on list.
     * @return List with List of iteration statistics.
     */
    public List<IterationStatistics> getIterationsStatistics(
        long lastIteration) {
      long firstIteration = 
          lastIteration - StatisticsHtmlPrinter.DEFAULT_TABLE_LENGTH + 1;
      firstIteration = Math.max(firstIteration, 1);

      return generalStatistic.getIterations(firstIteration, lastIteration);
    }

    /**
     * Returns statistical data about given iteration.
     * @param iterationNumber Iteration number.
     * @return Iteration statistics.
     */
    public IterationStatistics getSingleIteration(long iterationNumber) {
      return generalStatistic.getIteration(iterationNumber);
    }

    /**
     * Adds new slave. Should be called on slave registration.
     * @param slaveId Slave's identifier.
     */
    public void addSlave(String slaveId) {
      generalStatistic.addSlave(slaveId, iterationCounter.get());
    }

    /**
     * This method is called immediately after entering 
     * {@link MasterSlaveEvaluator#evaluatePopulation} method.
     * This method should gather some date which can be used
     * in statistics (population object should be useful). 
     * This is counter-partner method of
     * {@link StatisticsManager#doAtIterationEnd}.
     * @param population Population object before executing iteration. 
     */
    public void atIterationStart(Population<?> population) {
      long iterValue = iterationCounter.get();

      iterationStatistics = new IterationStatistics();

      iterationStatistics.setIterationStartTime();
      iterationStatistics.addCounterValue(
          IterationStatistics.ITERATION_NUMBER, iterValue);
      iterationStatistics.addCounterValue(
          IterationStatistics.POPULATION_SIZE, population.size());
      iterationStatistics.addCounterValue(
          IterationStatistics.POPULATION_SIZE_IN_BYTES, 0.0);
      iterationStatistics.addCounterValue(
          IterationStatistics.BEST_INDIVIDUAL_VALUE, 0.0);
      iterationStatistics.addCounterValue(
          IterationStatistics.WORST_INDIVIDUAL_VALUE, 0.0);
      iterationStatistics.addCounterValue(
          IterationStatistics.AVERAGE_INDIVIDUAL_VALUE, 0.0);
      iterationStatistics.addCounterValue(
          IterationStatistics.STD_DEVIATION_INDIVIDUAL_VALUE, 0.0);
      return;
    }

    /**
     * This method is called just before leaving
     * {@link MasterSlaveEvaluator#evaluatePopulation} method.
     * This method should gather some date which can be used
     * in statistics (population object should be useful).
     * This is counter-partner method of
     * {@link StatisticsManager#doAtIterationStart}.
     * @param population Population object after executing iteration. 
     */
    public void atIterationEnd(Population<?> population) {
      iterationStatistics.setIterationEndTime();
      double timespan = ClockUtilities.getTimeSpanInSeconds(
          iterationStatistics.getIterationStartTime(),
          iterationStatistics.getIterationEndTime());

      iterationStatistics.addCounterValue(
          IterationStatistics.TIME_SPENT_IN_SECONDS, timespan);
      generalStatistic.addIterationData(iterationStatistics);

      iterationCounter.incrementAndGet();
    }

    /**
     * This method is called just after sending new task to slave
     * (before return point of {@link DistributionServlet#doGet}).
     * This method should gather some date which can be used
     * in statistics. This is counter-partner method of
     * {@link StatisticsManager#doAfterReceivingResult}. 
     * @param slaveId Identifier of slave which gets the task.
     * @param task Evaluation task which was sent.
     */
    public void afterSendingTask(String slaveId, EvaluationTask<?> task) {
      return;
    }

    /**
     * This method is called just after receiving result from slave
     * (before return point of {@link DistributionServlet#doPost}).
     * This method should gather some date which can be used
     * in statistics. This is counter-partner method of
     * {@link StatisticsManager#doAfterSendingResult}. 
     * @param slaveId Identifier of slave which posts the result.
     * @param result Result received from slave.
     */
    public void afterReceivingResult(String slaveId, 
        EvaluationResult<?> result) {
      return;
    }
}
