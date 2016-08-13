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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import engine.Population;
import engine.PopulationEvaluatorTest.DummyIndividual;

/**
 * Tests for {@link DistributedPopulation}.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class DistributedPopulationTest {

  /** List of populations used in tests. */
  private List<Population<DummyIndividual>> populations = 
      createPopulationList();

  /** List of slave ids used in tests. */
  private List<String> slaveIds =
      createSlaveIdList(populations.size());

  /**
   * Tests if {@link DistributedPopulation#getPopulation(SlaveId)} returns
   * appropriate parts of the population.
   */
  @Test
  public void getPopulationFromSlaveTest() {
    HashMap<String, Population<DummyIndividual>> slavesToPopulation =
        createSlaveToPopulationMap();

    DistributedPopulation<DummyIndividual> distributedPopulation =
        new DistributedPopulation<DummyIndividual>(slavesToPopulation);

    Assert.assertEquals(distributedPopulation.getPopulation(slaveIds.get(0)),
        populations.get(0));
    Assert.assertEquals(distributedPopulation.getPopulation(slaveIds.get(1)),
        populations.get(1));
    Assert.assertEquals(distributedPopulation.getPopulation(slaveIds.get(2)),
        populations.get(2));
    Assert.assertEquals(distributedPopulation.getPopulation(
        new String("slave" + String.valueOf(slaveIds.size()))), null);
  }

  /**
   * Creates a map from slave ids to populations.
   * @return Map from slave ids to populations.
   */
  private HashMap<String, Population<DummyIndividual>>
      createSlaveToPopulationMap() {
    HashMap<String, Population<DummyIndividual>> slaveToPopulation =
      new HashMap<String, Population<DummyIndividual>>();
    for (int i = 0; i < populations.size(); i++) {
      slaveToPopulation.put(slaveIds.get(i), populations.get(i));
    }
    return slaveToPopulation;
  }
 
  /**
   * Creates list of slave ids for testing purposes.
   * @param size Number of slave ids to create.
   * @return List of slave ids; each slave has an id equal to "slavei",
   * where i is the index in the list.
   */
  private List<String> createSlaveIdList(int size) {
    List<String> slaveIdList = new ArrayList<String>(size);

    for (int i = 0; i < size; i++) {
      slaveIdList.add(new String("slave" + String.valueOf(i)));
    }

    return slaveIdList;
  }

  /**
   * Creates list of dummy populations for testing.
   * @return List of dummy populations.
   */
  @SuppressWarnings("unchecked")
  private List<Population<DummyIndividual>> createPopulationList() {
    return Arrays.asList(
        new Population<DummyIndividual>(Arrays.asList(
            new DummyIndividual()
        )),
        new Population<DummyIndividual>(Arrays.asList(
            new DummyIndividual(),
            new DummyIndividual()
        )),
        new Population<DummyIndividual>(Arrays.asList(
            new DummyIndividual(),
            new DummyIndividual(),
            new DummyIndividual()
        ))
    );
  }
}
