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

package engine.operators;

import engine.Factory;
import engine.Operator;
import engine.Population;

/**
 * Adds a given hardcoded individual to the population.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 * @param <T> type of the individual.
 */
public class AddHardcodedIndividual<T> implements Operator<T> {
 
  /** Factory of individuals to apply. */
  private final Factory<T> factory;

  /**
   * Creates the operator.
   * @param factory Factory of hardcoded individuals to apply.
   */
  public AddHardcodedIndividual(
      Factory<T> factory) {
     this.factory = factory;
  }

  /** {@inheritDoc}. */
  public Population<T> apply(Population<T> population) {
    Population<T> result = population;
    result.addIndividual(factory.get());
    return result;
  }
}
