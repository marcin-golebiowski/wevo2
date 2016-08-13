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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.annotations.Test;

import engine.CachedObjectiveFunction;
import engine.ObjectiveFunction;
import engine.Population;
import engine.utils.ListUtils;

/**
 * Tests for {@link MasterSlaveEvaluator}.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
@SuppressWarnings({ "serial", "unchecked" })
public class MasterSlaveEvaluatorTest {

  /** Mock controller. */
  private IMocksControl mockControl = EasyMock.createControl();

  /** Mock of the population distributor. */
  private PopulationDistributor<String> populationDistributorMock;

  /** Task manager mock. */
  private TaskManager<String> taskManagerMock;

  /** Slave manager mock. */
  private SlaveManager slaveManagerMock;

  /** Objective functions list used in tests. */
  private final List<CachedObjectiveFunction<String>> objectiveFunctionList =
      ListUtils.buildList(
          new CachedObjectiveFunction<String>(
              new ObjectiveFunction<String>() {
                public double compute(final String individual) {
                  return individual.length();
                }
              }, 3));

  /** Population object used in tests. */
  private final Population<String> population = new Population<String>() { {
      addIndividual("a");
      addIndividual("aa");
      addIndividual("aaa");
  } };

  /** List of currently available slaves. */
  private final List<String> slaves = Arrays.asList("slave1", "slave2"); 

  /** Distributed population used in tests. */
  private final DistributedPopulation<String> distributedPopulation =
      new DistributedPopulation<String>(
          new HashMap<String, Population<String>>() { {
              put("slave1", new Population<String>(
                  Arrays.asList("a", "aa")));
              put("slave2", new Population<String>(
                  Arrays.asList("aaa")));
          } });

  /** Signal evaluator waits for. */
  private CountDownLatch populationEvaluatedSignalMock;

  /**
   * Tests whether evaluation throws an exception on failure. 
   * @throws InterruptedException Never thrown.
   */
  @Test(expectedExceptions = { IllegalStateException.class })
  public void testLoudFailure() throws InterruptedException {
    createMocks();

    slaveManagerMock.setSlaveTimeout(-1);
    EasyMock.expectLastCall().anyTimes();

    setUpSingleEvaluation();

    EasyMock.expect(taskManagerMock.getUnevaluatedPart())
        .andReturn(population);
    populationEvaluatedSignalMock.await();
    EasyMock.expectLastCall()
        .andThrow(new InterruptedException());

    mockControl.replay();
    MasterSlaveEvaluator<String> evaluator = setUpEvaluator(-1, 1, 2, -1);
    evaluator.evaluatePopulation(population);
    mockControl.verify();
  }

  /**
   * This test just replays unlimited evaluation scenario.
   * @throws InterruptedException Never thrown.
   */
  @Test
  public void testUnlimitedEvaluation() throws InterruptedException {
    createMocks();

    slaveManagerMock.setSlaveTimeout(-1);
    EasyMock.expectLastCall().anyTimes();

    setUpSingleEvaluation();
    EasyMock.expect(taskManagerMock.getUnevaluatedPart())
        .andReturn(new Population<String>());

    populationEvaluatedSignalMock.await();
    EasyMock.expectLastCall().times(1);

    mockControl.replay();
    MasterSlaveEvaluator<String> evaluator = setUpEvaluator(-1, 1, 2, -1);
    evaluator.evaluatePopulation(population);
    mockControl.verify();
  }

  /**
   * Replays the time-limited evaluation scenario with one timeout.
   * @throws InterruptedException Never thrown.
   */
  @Test
  public void testTimeLimitedEvaluation() throws InterruptedException {
    // MagicNumber off
    createMocks();

    slaveManagerMock.setSlaveTimeout(5);
    EasyMock.expectLastCall().anyTimes();

    setUpSingleEvaluation();
    EasyMock.expect(populationEvaluatedSignalMock.await(
        10, TimeUnit.SECONDS))
            .andReturn(false);
    EasyMock.expect(taskManagerMock.getUnevaluatedPart())
        .andReturn(population);

    setUpSingleEvaluation();
    EasyMock.expect(populationEvaluatedSignalMock.await(
        10, TimeUnit.SECONDS))
            .andReturn(true);
    EasyMock.expect(taskManagerMock.getUnevaluatedPart())
        .andReturn(new Population<String>());

    mockControl.replay();
    MasterSlaveEvaluator<String> evaluator = setUpEvaluator(10, 3, 2, 5);
    evaluator.evaluatePopulation(population);
    mockControl.verify();
    // MagicNumber on
  }

  /**
   * Sets up evaluator instance to test.
   * @param iterationTimeout Iteration timeout.
   * @param trialsPerIteration Trials per iteration.
   * @param minimumNumberOfSlaves Minimum number of slaves to run iteration.
   * @param slaveTimeout Slave timeout.
   * @return Evaluator to be tested.
   */
  private MasterSlaveEvaluator<String> setUpEvaluator(
      final int iterationTimeout,
      final int trialsPerIteration,
      final int minimumNumberOfSlaves,
      final int slaveTimeout) {

    MasterSlaveEvaluator<String> evaluator =
        new MasterSlaveEvaluator<String>(
            objectiveFunctionList,
            populationDistributorMock,
            taskManagerMock,
            slaveManagerMock,
            new StatisticsManager(),
            null);
    evaluator.setIterationTimeout(iterationTimeout);
    evaluator.setTrialsPerIteration(trialsPerIteration);
    evaluator.setMinimumNumberOfSlaves(minimumNumberOfSlaves);
    evaluator.setSlaveTimeout(slaveTimeout);

    return evaluator;
  }

  /**
   * Sets up mock for single evaluation. This set up is common
   * for both time-limited and unlimited evaluation scenarios. 
   */
  private void setUpSingleEvaluation() {
    EasyMock.expect(slaveManagerMock.getAvailableSlaves(2))
        .andReturn(slaves);
    EasyMock.expect(populationDistributorMock.distribute(
        population, slaves))
            .andReturn(distributedPopulation);
    EasyMock.expect(taskManagerMock.enableDistribution(
        distributedPopulation, objectiveFunctionList))
            .andReturn(populationEvaluatedSignalMock);
  }

  /** Creates mock instances used in tests. */
  private void createMocks() {
    mockControl.reset();

    populationDistributorMock =
        mockControl.createMock(PopulationDistributor.class);
    taskManagerMock = mockControl.createMock(TaskManager.class);
    slaveManagerMock = mockControl.createMock(SlaveManager.class);
    populationEvaluatedSignalMock =
        mockControl.createMock(CountDownLatch.class);
  }
}
