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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.easymock.IMocksControl;
import org.easymock.classextension.EasyMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import engine.ObjectiveFunction;
import engine.Population;
import engine.utils.WevoRandom;

/**
 * Tests for {@link CombineBestWithOtherOperator}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class CombineBestWithOtherOperatorTest {
  // MagicNumber off

  /** Mock control. */
  private final IMocksControl mockControl = EasyMock.createControl();

  /** Tests if operator conforms to its contract. */
  @Test
  public void testApply() {
    Population<String> population = new Population<String>(
        Arrays.asList("a", "bb", "ccc", "dddd"));

    List<ObjectiveFunction<String>> objectiveFunctions = 
        new LinkedList<ObjectiveFunction<String>>();
    objectiveFunctions.add(new ObjectiveFunction<String>() {
      public double compute(String individual) {
        return individual.length();
      }
    });
    objectiveFunctions.add(new ObjectiveFunction<String>() {
      public double compute(String individual) {
        return individual.charAt(0);
      }
    });

    WevoRandom randomMock = mockControl.createMock(WevoRandom.class);
    EasyMock.expect(randomMock.nextInt(0, 4)).andReturn(2).anyTimes();

    CombineBestWithOtherOperator<String> operator = 
        new CombineBestWithOtherOperator<String>(
            objectiveFunctions,
            new BestFractionSelection<String>(
                objectiveFunctions.get(0), 0.5),
            randomMock);

    mockControl.replay();
    Population<String> result = operator.apply(population);
    mockControl.verify();

    Assert.assertFalse(result.getIndividuals().contains("c"));
    Assert.assertEquals(result.size(), 4);
  }

  // MagicNumber on
}
