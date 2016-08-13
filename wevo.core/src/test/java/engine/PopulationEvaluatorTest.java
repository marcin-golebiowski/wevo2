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
package engine;

import java.util.LinkedList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * An abstract test for {@link PopulationEvaluator}. Each of the
 * tests for evaluators should extend this one and make sure it passes.
 * 
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public abstract class PopulationEvaluatorTest {
 
  /**
   * Dummy individual only for testing if evaluators work fine.
   * @author Marcin Brodziak (marcin.brodziak@gmail.com)
   */
  public static class DummyIndividual {

    /** Indicates whether first objective function was called. */
    private boolean firstFunctionCalled;

    /** Indicates whether second objective function was called. */
    private boolean secondFunctionCalled;

    /**
     * Mocks a call to the first objective function.
     * @return Dummy function value. 
     */
    public double callFirstFunction() {
      this.firstFunctionCalled = true;
      return 0;
    }

    /**
     * Mocks a call to the second objective function.
     * @return Dummy function value.
     */
    public double callSecondFunction() {
      this.secondFunctionCalled = true;
      return 0;
    }

    /** Checks whether both objective functions were called. */
    public void assertBothFunctionsEvaluated() {
      Assert.assertTrue(firstFunctionCalled && secondFunctionCalled);
    }
  }
 
  /**
   * Tests whether both objective functions were called during evaluation.
   */
  @Test
  public void testObjectiveFunctions() {
    PopulationEvaluator<DummyIndividual> evaluator = getEvaluator();
    Population<DummyIndividual> population = createPopulation();
    evaluator.apply(population);
    assertObjectiveFunctionsEvaluatedOnEachIndividual(population);
  }
 
  /**
   * Self-explanatory.
   * @param population Population to evaluate.
   */
  private void assertObjectiveFunctionsEvaluatedOnEachIndividual(
      Population<DummyIndividual> population) {
    for (DummyIndividual individual :  population.getIndividuals()) {
      individual.assertBothFunctionsEvaluated();
    }
  }

  /**
   * Creates a population of dummy individuals for tests.
   * @return Population created.
   */
  private Population<DummyIndividual> createPopulation() {
    List<DummyIndividual> list = new LinkedList<DummyIndividual>();
    list.add(new DummyIndividual());
    return new Population<DummyIndividual>(list);
  }

  /**
   * Creates a list of objective functions for tests.
   * @return List of objective functions to be called in population
   * evaluation.
   */
  public List<CachedObjectiveFunction<DummyIndividual>> 
      createObjectiveFunctions() {
    LinkedList<CachedObjectiveFunction<DummyIndividual>> objectiveFunctions = 
          new LinkedList<CachedObjectiveFunction<DummyIndividual>>();
    objectiveFunctions.add(new CachedObjectiveFunction<DummyIndividual>(
        new ObjectiveFunction<DummyIndividual>() {
      public double compute(DummyIndividual individual) {
        return individual.callFirstFunction();
      }
    }, Integer.MAX_VALUE));
    objectiveFunctions.add(new CachedObjectiveFunction<DummyIndividual>(
        new ObjectiveFunction<DummyIndividual>() {
      public double compute(DummyIndividual individual) {
        return individual.callSecondFunction();
      }
    }, Integer.MAX_VALUE));
    return objectiveFunctions;
  }

  /**
   * Returns evaluator instance to be tested. Each test has to return
   * its own evaluator, obviously.
   * @return Evaluator to be tested.
   */
  public abstract PopulationEvaluator<DummyIndividual> getEvaluator(); 
}

