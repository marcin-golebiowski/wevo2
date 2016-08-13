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

import java.util.Arrays;

import org.easymock.classextension.EasyMock;
import org.easymock.classextension.IMocksControl;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import engine.utils.WevoRandom;

/**
 * Test for {@link SingleThreadedEvaluator}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
@Test
public class PopulationTest {

  /** Mock control. */
  private IMocksControl mockControl = EasyMock.createControl();

  /** Population acting as an argument for tested methods. */
  private final Population<String> originalPopulation =
      new Population<String>(Arrays.asList(new String[] {"a", "b", "c"})); 

  /** Clone of the original population. */
  private final Population<String> originalPopulationClone =
      new Population<String>(Arrays.asList(new String[] {"a", "b", "c"}));

  /** Tests population shuffling. */
  public void testPopulationShuffling() {
    Population<String> expectedPopulation = new Population<String>(
        Arrays.asList(new String[] {"c", "b", "a"}));

    // MagicNumber off
    WevoRandom randomMock = mockControl.createMock(WevoRandom.class);
    EasyMock.expect(randomMock.nextInt(0, 3)).andReturn(2);
    EasyMock.expect(randomMock.nextInt(0, 2)).andReturn(1);
    EasyMock.expect(randomMock.nextInt(0, 1)).andReturn(0);
    // MagicNumber on

    mockControl.replay();
    Population<String> shuffleResult = Population.shuffle(
        randomMock, originalPopulation);
    mockControl.verify();

    Assert.assertEquals(shuffleResult, expectedPopulation);
    assertThatOriginalPopulationWasIntact(originalPopulation);
  }

  /** Tests population shuffling. */
  public void testPopulationShufflingWithRepeatingIndex() {
    Population<String> expectedPopulation = new Population<String>(
        Arrays.asList(new String[] {"b", "c", "a"}));

    // MagicNumber off
    WevoRandom randomMock = mockControl.createMock(WevoRandom.class);
    EasyMock.expect(randomMock.nextInt(0, 3)).andReturn(1);
    EasyMock.expect(randomMock.nextInt(0, 2)).andReturn(1);
    EasyMock.expect(randomMock.nextInt(0, 1)).andReturn(0);
    // MagicNumber on

    mockControl.replay();
    Population<String> shuffleResult = Population.shuffle(
        randomMock, originalPopulation);
    mockControl.verify();

    Assert.assertEquals(shuffleResult, expectedPopulation);
    assertThatOriginalPopulationWasIntact(originalPopulation);
  }

  /**
   * Assert that the original population was not changed during execution
   * of the tested method.
   * @param population Original population that fed the tested method.
   */
  private void assertThatOriginalPopulationWasIntact(
      final Population<String> population) {
    Assert.assertEquals(population, originalPopulationClone);
  }
 
  /** Tests removing random individual from the population. */
  public void testRandomIndividualRemoval() {
    Population<String> expectedPopulation = new Population<String>(
        Arrays.asList(new String[] {"a", "c"}));

    // MagicNumber off
    WevoRandom randomMock = mockControl.createMock(WevoRandom.class);
    EasyMock.expect(randomMock.nextInt(0, 3)).andReturn(1);
    // MagicNumber on

    mockControl.replay();
    Population<String> removalResult = Population.removeRandomIndividual(
        randomMock, originalPopulation);
    mockControl.verify();

    Assert.assertEquals(removalResult, expectedPopulation);
    assertThatOriginalPopulationWasIntact(originalPopulation);
  }

  /** Cleans up the working environment. */
  @AfterMethod
  public void tearDown() {
    mockControl.reset();
  }
}
