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
 * An operator transforming population of individuals into another
 * population of individuals. Operators can be anything: crossover
 * defined between individuals, mutations on random subsample of them,
 * selection that returns only half of the population, etc. 
 * 
 * A good practice to follow is that it returns a new population, and if the
 * individuals are mutable with copies of the ones that were not changed. 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individual in the population.
 */
public interface Operator<T> {
  /**
   * Applies the operator to given population. 
   * @param population Source population.
   * @return Population after transformation.
   */
  Population<T> apply(Population<T> population);
}
