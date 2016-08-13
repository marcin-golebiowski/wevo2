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

import org.testng.Assert;
import org.testng.annotations.Test;

import engine.ObjectiveFunction;
import engine.Population;

/**
 * Tests for {@link PopulationStatistics} operator.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
@Test
public class PopulationStatisticsTest {

  /** Tests if given operator actually does what it is supposed to do. */
  @SuppressWarnings("serial")
  public void testOperator() {
    Population<String> population = new Population<String>() { {
      addIndividual("aaa");
      addIndividual("aa");
      addIndividual("aaaaa");
      addIndividual("aaa");
      addIndividual("aa");
      addIndividual("aaa");
      addIndividual("aaaa");
      addIndividual("aa");
    } };

    PopulationStatistics<String> stats = new PopulationStatistics<String>(
        new ObjectiveFunction<String>() {
           /** {@inheritDoc} */
          public double compute(String individual) {
            return individual.length();
          }
        }
    );
    stats.apply(population);
    // Magic Number off
    Assert.assertEquals("aaaaa", stats.getBestIndividual());
    Assert.assertEquals("aa", stats.getWorstIndividual());
    Assert.assertEquals(3.0, stats.getAverageIndividualValue());


    population.addIndividual("aaaaaaaaaa");
    population.addIndividual("a");

    stats.apply(population);
    Assert.assertEquals("aaaaaaaaaa", stats.getBestIndividual());
    Assert.assertEquals("a", stats.getWorstIndividual());
    Assert.assertEquals(3.5, stats.getAverageIndividualValue());
    // Magic Number on
    }
}
