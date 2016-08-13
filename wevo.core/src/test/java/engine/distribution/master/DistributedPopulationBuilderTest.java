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

import org.testng.Assert;
import org.testng.annotations.Test;

import engine.Population;

/**
 * Tests for {@link DistributedPopulationBuilder}.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class DistributedPopulationBuilderTest {

  /** Tested instance. */
  private DistributedPopulationBuilder<String> populationBuilder;

  /** Dummy slave for tests. */
  private final String dummySlave = new String("slave");

  /** Dummy individual number 1. */
  private final String dummyIndividual1 = new String("individual1");

  /** Dummy individual number 2, distrinct from the first one. */
  private final String dummyIndividual2 = new String("individual2");

  /**
   * Tests if a slave with an empty population is created
   * and successfully updated when there is no slave
   * in yet constructed mapping.
   */
  @Test
  public void addIndividualToNonExistingSlave() {
    populationBuilder = new DistributedPopulationBuilder<String>();
    populationBuilder.addIndividualToSlave(dummySlave, dummyIndividual1);

    Population<String> subpopulation = new Population<String>();
    subpopulation.addIndividual(dummyIndividual1);
    DistributedPopulation<String> distributedPopulation =
        populationBuilder.toDistributedPopulation();

    Assert.assertEquals(subpopulation,
        distributedPopulation.getPopulation(dummySlave));
  }

  /**
   * Tests if population associated to an existing slave
   * is properly updated.
   */
  @Test
  public void addIndividualToExistingSlave() {
    populationBuilder = new DistributedPopulationBuilder<String>();
    populationBuilder.addIndividualToSlave(dummySlave, dummyIndividual1);
    populationBuilder.addIndividualToSlave(dummySlave, dummyIndividual2);

    Population<String> subpopulation = new Population<String>();
    subpopulation.addIndividual(dummyIndividual1);
    subpopulation.addIndividual(dummyIndividual2);

    DistributedPopulation<String> distributedPopulation =
        populationBuilder.toDistributedPopulation();

    Assert.assertEquals(subpopulation,
        distributedPopulation.getPopulation(dummySlave));
  }
}
