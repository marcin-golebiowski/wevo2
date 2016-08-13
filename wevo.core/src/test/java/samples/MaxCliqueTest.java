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
package samples;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import samples.objectivefunctions.MaxClique;

import engine.individuals.BinaryVector;
import junit.framework.Assert;

/**
 * Tests for {@link MaxClique}.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 *
 */
public class MaxCliqueTest {
  // MagicNumber off

  /** Graph instance for testing. */
  static final List<List<Integer>> GRAPH = createExemplaryGraph();

  /** Tests whether {@link MaxClique} correctly recognizes cliques. */
  @Test
  public void testCliqueExistance() {
   MaxClique function = new MaxClique(GRAPH);

   Assert.assertEquals(4.0, function.compute(new BinaryVector(new boolean[]{
       true, true, true, true, false
   })));

   Assert.assertEquals(3.0, function.compute(new BinaryVector(new boolean[]{
       true, true, false, true, false
   })));

    Assert.assertEquals(2.0, function.compute(new BinaryVector(new boolean[]{
        true, true, false, false, false
    })));
  }

  /** Tests whether {@link MaxClique} correctly recognizes non-cliques. */
  @Test
  public void testCliqueNonExistance() {
    MaxClique function = new MaxClique(GRAPH);

    Assert.assertEquals(0.0, function.compute(new BinaryVector(new boolean[]{
        true, true, false, true, true
    })));

    Assert.assertEquals(0.0, function.compute(new BinaryVector(new boolean[]{
        false, true, true, true, true
    })));

    Assert.assertEquals(0.0, function.compute(new BinaryVector(new boolean[]{
        true, false, true, false, true
    })));
  }

  /**
  * Creates exemplary graph for testing purposes.
   * @return Exemplary graph.
   */
  @SuppressWarnings("unchecked")
  private static List<List<Integer>> createExemplaryGraph() {
    return Arrays.asList(
        Arrays.asList(1, 2, 3),
        Arrays.asList(0, 2, 3, 4),
        Arrays.asList(0, 1, 3),
        Arrays.asList(0, 1, 2, 4),
        Arrays.asList(1, 3)
    );
  }

  // MagicNumber on
}
