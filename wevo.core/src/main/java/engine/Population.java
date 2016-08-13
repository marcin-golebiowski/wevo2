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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import engine.utils.WevoRandom;

/**
 * Represents the population on which {@link Algorithm} works.
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 *
 * @param <T> Type of the individual that this population contains.
 */
public class Population<T> implements Serializable {

  /** Generated serial version UID. */
  private static final long serialVersionUID = 4485607100662519133L;

  /** List of individuals in the population. */
  private final List<T> individuals;

  /**
   * Creates the population from given initial list of individuals.
   * @param list List of individuals in initial population.
   */
  public Population(List<T> list) {
    individuals = list;
  }
 
  /** Creates an empty population. */
  public Population() {
    individuals = new ArrayList<T>();
  }

  /**
   * Copying constructor.
   * @param population Population to copy.
   */
  @SuppressWarnings("unchecked")
  public Population(Population<T> population) {
    this.individuals = Arrays.asList((T[]) 
        population.individuals.toArray().clone());
  }

  /**
   * Returns individuals in the population.
   * @return List of individuals in the population.
   */
  public List<T> getIndividuals() {
    return individuals;
  }

  /**
   * Adds a new individual to the population.
   * @param individual Individual to be added.
   */
  public void addIndividual(T individual) {
    individuals.add(individual);
  }

  /**
   * Merges this population with a given one without removing duplicates.
   * @param population Population to merge with. Must not be null.
   */
  public void mergeWith(Population<T> population) {
    this.individuals.addAll(population.individuals);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (T i : individuals) {
      sb.append(i);
      sb.append("\n");
    }
    return sb.toString();
  }

  /** 
   * Returns size of the population.
   * @return Size of the population.
   */
  public int size() {
    return individuals.size();
  }

  /** {@inheritDoc} */
  public boolean equals(Object object) {
    if (object == null || !(object instanceof Population)) {
      return false;
    }

    Population<T> that = castToPopulation(object);
    return this.individuals.equals(that.individuals);
  }

  /** {@inheritDoc} */
  public int hashCode() {
    return individuals.hashCode();
  }

  /**
   * Casts given object to a Population object. Will throw
   * a ClassCastException when given object is not an instance
   * of Population class.
   * 
   * @param object Object to be casted to Population.
   * @return Population object that is the result of a cast.
   * Never null.
   */
  @SuppressWarnings("unchecked")
  private Population<T> castToPopulation(Object object) {
    Population<T> population = (Population<T>) object;
    // EmptyBlock off
    // We're doing only type-checking here, empty block is OK.
    for (@SuppressWarnings("unused") T individual
        : population.getIndividuals()) { }
    // EmptyBlock on
    return population;
  }

  /**
   * Shuffles population with given random number generator.
   * @param random Random number generator.
   * @param population Population object to be shuffled. Will not be
   * @param <T> Type of individual in the population.
   * changed during shuffle.
   * @return Shuffled version of the population object.
   */
  public static <T> Population<T> shuffle(
      final WevoRandom random,
      final Population<T> population) {
    final List<T> reorderedIndividuals = new ArrayList<T>(
        population.size());
    final List<T> originalIndividuals = new ArrayList<T>(
        population.getIndividuals());

    // We pick an individual from the list and append it
    // to the end of new list containing reordered individuals.
    for (int i = population.size(); i > 0; i--) {
      final int pickedIndividualIndex = random.nextInt(0, i);
      final T pickedIndividual = originalIndividuals.get(pickedIndividualIndex);
      originalIndividuals.remove(pickedIndividualIndex);
      reorderedIndividuals.add(pickedIndividual);
    }

    return new Population<T>(reorderedIndividuals);
  }

  /**
   * Removes random individual from population.
   * @param random Random number generator.
   * @param population Population from which the individual will be removed.
   * This object will be intact.
   * @param <T> Type of individual in the population.
   * @return Population without random individual.
   */
  public static <T> Population<T> removeRandomIndividual(
      final WevoRandom random,
      final Population<T> population) {
    List<T> newIndividuals = new ArrayList<T>(population.getIndividuals());
    newIndividuals.remove(random.nextInt(0, population.size()));

    return new Population<T>(newIndividuals);
  }
}
