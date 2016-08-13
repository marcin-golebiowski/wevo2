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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements multi-threaded evaluator. By far this is the one you should be 
 * using if you are writing a software for a single, but multi-core machine. 
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 * @param <T> Type of individuals to be evaluated.
 */
public class MultiThreadedEvaluator<T> extends PopulationEvaluator<T> {

  /** Timeout for waiting for executors to compute tasks. */
  private static final int TIMEOUT = 3600;

  /** Maximum time to keep alive worker threads. */
  private static final int KEEP_ALIVE_TIME = 100;

  /** Maximum size of thread pool. */
  private static final int MAXIMUM_POOL_SIZE = 10;

  /** Executor for multi-threaded evaluating of individuals. */
  private ThreadPoolExecutor executor;

  /** Logger. */
  private Logger logger = Logger.getLogger(
      MultiThreadedEvaluator.class.getCanonicalName());
 
  /**
   * Creates multi-threaded evaluator.
   * @param objectiveFunctions List of objective functions to be evaluated.
   */
  public MultiThreadedEvaluator(
      final List<CachedObjectiveFunction<T>> objectiveFunctions) {
    super(objectiveFunctions);
    executor = new ThreadPoolExecutor(2, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, 
        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
  }

  /** {@inheritDoc} */
  @Override
  public synchronized void evaluatePopulation(
      Population<T> populationInternal) {
    final CountDownLatch latch = 
        new CountDownLatch(populationInternal.size());
    for (final T individual : populationInternal.getIndividuals()) {
      executor.execute(new Runnable() {
        public void run() {
          for (CachedObjectiveFunction<T> function : getObjectiveFunctions()) {
            function.computeInternal(individual);
          }
          latch.countDown();
        }
      });
    }
    try {
      latch.await(TIMEOUT, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.log(Level.SEVERE,
          "Problem with computing values of objective functions.", e);
    }
  }

  /** Shuts down the evaluator. */
  public void shutDown() {
    executor.shutdown();
  }
}
