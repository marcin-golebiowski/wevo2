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
package engine.distribution.serialization;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.Assert;
import org.testng.annotations.Test;

import engine.CachedObjectiveFunction;

/**
 * Tests for {@link EvaluationResult}.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class EvaluationResultBuilderTest {

  /** Tests if result builder builds evaluation result properly. */
  @SuppressWarnings({ "serial", "unchecked" })
  @Test
  public void testBuilding() {
    // MagicNumber off
    IMocksControl mockControl = EasyMock.createControl();
    mockControl.reset();

    final List<String> individuals = new LinkedList<String>() { {
        add("a");
        add("aa");
        add("aaa");
    } };

    final LinkedList<LinkedHashMap<String, Double>> partialResults =
        new LinkedList<LinkedHashMap<String, Double>>() { {
            addLast(new LinkedHashMap<String, Double>() { {
              put(individuals.get(0), 1.0);
              put(individuals.get(0), 2.0);
              put(individuals.get(0), 3.0);
            } });

            addLast(new LinkedHashMap<String, Double>() { {
              put(individuals.get(0), 1.1);
              put(individuals.get(0), 2.1);
              put(individuals.get(0), 3.1);
            } });

            addLast(new LinkedHashMap<String, Double>() { {
              put(individuals.get(0), 1.2);
              put(individuals.get(0), 2.2);
              put(individuals.get(0), 3.2);
            } });
        } };

    CachedObjectiveFunction<String> objectiveFunction1 =
        mockControl.createMock(CachedObjectiveFunction.class);
    EasyMock.expect(objectiveFunction1.getCache())
        .andReturn(partialResults.get(0));

    CachedObjectiveFunction<String> objectiveFunction2 =
        mockControl.createMock(CachedObjectiveFunction.class);
    EasyMock.expect(objectiveFunction2.getCache())
        .andReturn(partialResults.get(1));

    CachedObjectiveFunction<String> objectiveFunction3 =
        mockControl.createMock(CachedObjectiveFunction.class);
    EasyMock.expect(objectiveFunction3.getCache())
        .andReturn(partialResults.get(2));

    EvaluationResultBuilder<String> resultBuilder =
      new EvaluationResultBuilder<String>();

    mockControl.replay();
    resultBuilder.appendObjectiveFunctionResults(objectiveFunction1.getCache());
    resultBuilder.appendObjectiveFunctionResults(objectiveFunction2.getCache());
    resultBuilder.appendObjectiveFunctionResults(objectiveFunction3.getCache());

    EvaluationResult<String> evaluationResult =
        resultBuilder.toEvaluationResult();

    mockControl.verify();

    Assert.assertEquals(evaluationResult.getResult(0), partialResults.get(0));
    Assert.assertEquals(evaluationResult.getResult(1), partialResults.get(1));
    Assert.assertEquals(evaluationResult.getResult(2), partialResults.get(2));
    // MagicNumber on
  }
}
