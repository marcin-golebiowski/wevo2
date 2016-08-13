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

import java.util.LinkedHashMap;

/**
 * Cache of least recently used objects.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 * @param <T> Type of individuals we map from. 
 * @param <V> Type of individuals we map to.
 */
public class LruMap<T, V> extends LinkedHashMap<T, V> {
 
  /** Id for serialization. */
  private static final long serialVersionUID = -6259420037419874980L;

  /** Maximum size of the map. */
  private final int maxSize;

  /**
   * Creates the LruMap. 
   * @param size Maximum size of the map.
   */
  public LruMap(final int size) {
    // TODO(marcin.brodziak): make these values parameters and supply defaults.
    // MagicNumber off
    super(100, 0.5f, true);
    // MagicNumber on
    maxSize = size;
  }

  /** {@inheritDoc}. */
  @Override
  protected boolean removeEldestEntry(java.util.Map.Entry<T, V> eldest) {
    return size() > maxSize;
  }
}
