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

/**
 * Condition which, when met, signals the {@link Algorithm} to terminate
 * computation. For example it can count the number of iterations of the 
 * algorithm that passed or compute the variance of objective function values
 * in the population. It can be arbitrarily simple or complex. It is executed 
 * only on the <strong>master machine</strong> in master-slave distribution.
 * 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individual for which condition is evaluated.
 */
public interface TerminationCondition<T> {
  /**
   * Is the condition met.
   * @param population Population for which the condition can be evaluated.
   * @return Whether condition is satisfied.
   */
  boolean isSatisfied(Population<T> population);
 
  /** Resets termination condition. */
  void reset();
}
