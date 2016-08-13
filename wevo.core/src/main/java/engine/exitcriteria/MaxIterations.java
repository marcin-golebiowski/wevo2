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
package engine.exitcriteria;

import engine.Population;
import engine.TerminationCondition;

/**
 * Terminates the evolution after given number of iterations has passed.
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individual for which it is evaluated.
 */
public class MaxIterations<T> implements TerminationCondition<T> {
  /** Number of iterations. */
  private final int maxIter;
 
  /** Number of current iteration. */
  private int currentIter;

  /** 
   * Creates the termination condition which will succeed after
   * maximum number of iterations has passed.
   * @param maxIter Maximum number of iterations.
   */
  public MaxIterations(int maxIter) {
    this.maxIter = maxIter;
  }

  /** {@inheritDoc} */
  public boolean isSatisfied(Population<T> population) {
    currentIter++;
    return currentIter > maxIter;
  }

  /** {@inheritDoc} */
  public void reset() {
    this.currentIter = 0;
  }
}
