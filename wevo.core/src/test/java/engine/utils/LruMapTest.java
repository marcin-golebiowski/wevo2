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
package engine.utils;

import static org.testng.Assert.*;

import org.testng.annotations.Test;

/**
 * Tests for {@link LruMap}.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class LruMapTest {

  /** Testing adding object to map. */
  @Test
  public void testAddingObjects() {
    // Magic Number off
    LruMap<String, Integer> map = new LruMap<String, Integer>(3);
    map.put("a", 1);
    map.put("B", 2);
    map.put("c", 3);
    map.put("d", 4);
    // Magic Number on
    assertTrue(map.containsKey("c"));
    assertFalse(map.containsKey("a"));
  }
}
