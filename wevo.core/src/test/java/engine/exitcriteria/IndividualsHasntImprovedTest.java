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

package engine.exitcriteria;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import engine.ObjectiveFunction;
import engine.Population;

/**
 * Tests for {@link IndividualHasntImproved}.
 * 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 */
public class IndividualsHasntImprovedTest {

  /** Tests if this exit criteria works.*/
  @Test
  public void individualHasntChanged() {
    IndividualHasntImproved<String> maxIt =
        new IndividualHasntImproved<String>(2, 
          new ObjectiveFunction<String>() {
            public double compute(String individual) {
              return individual.length();
            }
          });
    List<String> list = new ArrayList<String>();
    list.add("foo");
    Population<String> population = new Population<String>(list);
    // Two false assertions
    Assert.assertFalse(maxIt.isSatisfied(population));
    Assert.assertFalse(maxIt.isSatisfied(population));
    // New good individual popped up
    population.addIndividual("fooo");
    // Two more hopeful run
    Assert.assertFalse(maxIt.isSatisfied(population));
    Assert.assertFalse(maxIt.isSatisfied(population));
    // Finaly exiting
    Assert.assertTrue(maxIt.isSatisfied(population));
  }
}
