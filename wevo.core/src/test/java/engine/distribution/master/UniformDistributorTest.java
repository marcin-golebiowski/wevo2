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

import java.util.Arrays;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import engine.Population;

/**
 * Tests for {@link UniformDistributor}.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class UniformDistributorTest {

  /** Tested instance. */
  private UniformDistributor<String> uniformDistributor;

  /** Model population. */
  private final Population<String> dummyPopulation =
      new Population<String>(Arrays.asList(
          "individual1",
          "individual2",
          "individual3",
          "individual4",
          "individual5",
          "individual6",
          "individual7",
          "individual8"));

  /** Model list of slaves. */
  private final List<String> dummySlaveList = Arrays.asList(
      new String("slave1"), 
      new String("slave2"), 
      new String("slave3"));

  /**
   * Tests whether population is distributed uniformly
   * over all slaves.
   */
  @Test
  public void testDistributionIsUniform() {
    // MagicNumber off
    uniformDistributor = new UniformDistributor<String>();
    DistributedPopulation<String> distributedPopulation = 
        uniformDistributor.distribute(dummyPopulation, dummySlaveList);

    final int averagePopulationSize = 3;

    Assert.assertTrue(
        Math.abs(distributedPopulation.getPopulation(dummySlaveList.get(0))
            .size() - averagePopulationSize) <= 1);
    Assert.assertTrue(
        Math.abs(distributedPopulation.getPopulation(dummySlaveList.get(1))
            .size() - averagePopulationSize) <= 1);
    Assert.assertTrue(
        Math.abs(distributedPopulation.getPopulation(dummySlaveList.get(2))
            .size() - averagePopulationSize) <= 1);

    Assert.assertEquals(
        distributedPopulation.getPopulation(dummySlaveList.get(0)).size()
        + distributedPopulation.getPopulation(dummySlaveList.get(1)).size()
        + distributedPopulation.getPopulation(dummySlaveList.get(2)).size(),
        dummyPopulation.size());
    // MagicNumber on
  }
}
