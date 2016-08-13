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

import static org.testng.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.Test;

import engine.ObjectiveFunction;
import engine.Population;

/**
 * Tests for {@link BestFractionSelection}.
 * TODO(marcin.brodziak): Add tests for dividing population of odd number
 * of individuals.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class BestRatioSelectionTest {
  // Magic Number off

  /** Tests if operator does actually select best half of population. */
  @Test
  public void testOperator() {
    List<String> strings = 
        new LinkedList<String>();
    strings.add("short");
    strings.add("looong");

    Population<String> p = new Population<String>(strings);
    BestFractionSelection<String> operator = new BestFractionSelection<String>(
        new ObjectiveFunction<String>() {
          public double compute(String individual) {
            return individual.length();
          }
        }, 0.5);
    p = operator.apply(p);
    assertEquals(p.getIndividuals().size(), 2);
    assertEquals(p.getIndividuals().get(0), "looong");
    assertEquals(p.getIndividuals().get(1), "looong");
  }

  // Magic Number on
}
