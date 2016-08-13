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

import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test for {@link CachedObjectiveFunction}.
 * @author marcin.brodziak@gmail.com (Marcin Brodziak)
 */
public class CachedObjectiveFunctionTest {
  /**
   * Sample objective function counting how many times it was called.
   * @author marcin.brodziak@gmail.com (Marcin Brodziak)
   */
  private class SampleObjectiveFunction 
      implements ObjectiveFunction<String> {

    /** Number of times the function was called. */
    private int howManyTimesCalled = 0;

    /** {@inheritDoc} */
    public double compute(String individual) {
      return ++howManyTimesCalled;
    }
  }
 
  /** Tests if caching is done properly. */
  @Test
  public void testCaching() {
    // Magic Number off
    CachedObjectiveFunction<String> function = new
        CachedObjectiveFunction<String>(new SampleObjectiveFunction(), 100);
    // Magic Number on
    function.computeInternal("a");
    assertEquals(function.compute("a"), 1.0);
    function.computeInternal("a");
    assertEquals(function.compute("a"), 1.0);
    function.computeInternal("b");
    assertEquals(function.compute("b"), 2.0);
  }
 
  /** Tests if exception is thrown on empty cache. */
  @Test(expectedExceptions = { IllegalStateException.class })
  public void testThrowsWithEmptyCache() {
    // Magic Number off
    CachedObjectiveFunction<String> function = new
        CachedObjectiveFunction<String>(new SampleObjectiveFunction(), 100);
    // Magic Number on
    assertEquals(1, function.compute("a"));
  }

  /** Test of the cache expiration. */
  @Test(expectedExceptions = { IllegalStateException.class })
  public void testCacheExpiration() {
    CachedObjectiveFunction<String> function = new
        CachedObjectiveFunction<String>(new SampleObjectiveFunction(), 1);
    function.computeInternal("a");
    function.computeInternal("b");
    assertEquals(function.compute("b"), 2.0);
    function.compute("a");
  }
}
