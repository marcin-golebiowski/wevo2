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

import java.util.ArrayList;
import java.util.List;

/**
 * Common utilities for managing lists.
 * @author Marcin Brodziak (marcin.brodziak@gmail.com)
 */
public class ListUtils {
 
  /**
   * Builds a list out of given set of elements.
   * @param <T> Type of the elements in a list.
   * @param elements Array of elements.
   * @return Converted list of elements.
   */
  public static <T> List<T> buildList(T... elements) {
    List<T> list = new ArrayList<T>();
    for (T element : elements) {
      list.add(element);
    }
    return list;
  }
 
  /**
   * Compares two arrays of doubles with given precision.
   * @param a1 First array to compare.
   * @param a2 Second array to compare.
   * @param precision Precision for each element.
   * @return Whether arrays are equal with given precision.
   */
  public static boolean compareArraysOfDoubles(double[] a1, 
      double[] a2, double precision) {
    if (a1.length != a2.length) {
      return false;
    }
    for (int i = 0; i < a1.length; i++) {
      if (Math.abs(a1[i] - a2[i]) > precision) {
        return false;
      }
    }
    return true;
  }
}
