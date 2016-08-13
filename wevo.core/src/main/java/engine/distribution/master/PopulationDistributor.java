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

import java.util.List;

import engine.Population;

/**
 * Interface for all population distributors.
 * @param <T> Type of individual in the population.
 * 
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public interface PopulationDistributor<T> {

  /**
   * Distributes population among given set of slaves
   * according to the distribution rules specific to
   * each implementation.
   * @param population Population to distribute. Must not be null.
   * @param currentSlaves List of slaves to distribute population among.
   * Must not be null and have at least one slave.
   * @return Distributed population that represents mapping from slave
   * to a subpopulation.
   */
  DistributedPopulation<T> distribute(Population<T> population, 
      List<String> currentSlaves);
}
