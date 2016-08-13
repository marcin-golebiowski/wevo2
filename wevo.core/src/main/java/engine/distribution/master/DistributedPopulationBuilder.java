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

import java.util.LinkedHashMap;
import java.util.Map;

import engine.Population;

/**
 * Helper class for building distributed populations.
 * 
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 *
 * @param <T> Type of the individual in the population.
 */
public class DistributedPopulationBuilder<T> {

  /**
   * Mapping from slave to population assigned to it,
   * from which DistributedPopulation will be built.
   */
  private final Map<String, Population<T>> mapping;

  /** Constructor. */
  public DistributedPopulationBuilder() {
    this.mapping = new LinkedHashMap<String, Population<T>>();
  }

  /**
   * Adds individual to the population assigned to a slave.
   * If the mapping for a given slave does not exists, it's created
   * with an initially empty population.
   * @param slave Slave whom population has to be expanded.
   * @param individual Individual expanding the population.
   */
  public void addIndividualToSlave(String slave, T individual) {
    Population<T> population = mapping.containsKey(slave) 
        ? mapping.get(slave) : new Population<T>();
    population.addIndividual(individual);
    mapping.put(slave, population);
  }

  /**
   * Returns a new Distributed Population object containing all
   * the data gathered before.
   * @return Distributed Population object, never null.
   */
  public DistributedPopulation<T> toDistributedPopulation() {
    return new DistributedPopulation<T>(mapping);
  }
}
