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
package engine.distribution.serialization;

import java.io.Serializable;

import engine.Population;

/**
 * Class representing an evaluation task sent to the slave.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * 
 * @param <T> Type of the individual in the task.
 */
public class EvaluationTask<T> implements Serializable {

  /** Generated serial version UID. */
  private static final long serialVersionUID = 7335573552212749764L;

  /** State of the task. */
  private enum State { NOT_EVALUATED, EVALUATED };

  /** Population that has to be evaluated. */
  private final Population<T> population;

  /** State of the evaluation task. */
  private State state;

  /**
   * Constructor.
   * @param newPopulation Population to create task from.
   * @param newState State of the task.
   */
  EvaluationTask(final Population<T> newPopulation, final State newState) {
    this.population = newPopulation;
    this.state = newState;
  }

  /**
   * Constructor of inevaluated task.
   * @param newPopulation Population to create task from.
   */
  public EvaluationTask(final Population<T> newPopulation) {
    this(newPopulation, State.NOT_EVALUATED);
  }

  /**
   * Gets the population to be evaluated.
   * @return Population to be evaluated.
   */
  public Population<T> getPopulation() {
    return population;
  }

  /**
   * Gets the state of the task.
   * @return True iff the task is evaluated, false otherwise.
   */
  public boolean isEvaluated() {
    return state == State.EVALUATED;
  }

  /** Marks the task as already evaluated. */
  public void markAsEvaluated() {
    state = State.EVALUATED;
  }

  /**
   * Returns size of the task.
   * @return Size of the task.
   */
  public int size() {
    return population.size();
  }
}
