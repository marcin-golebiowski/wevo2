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
package classifier.examples.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import engine.utils.WevoRandom;

/**
 * This class is responsible for mutating single
 * set in individual by removing random interval.
 * 
 * Note that this is not an operator as it doesn't
 * work on population.
 *
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRulesIntervalRemoveMutation {

    /** Random number generator. */
    private final WevoRandom random;

    /**
     * Standard constructor.
     * @param random Random number generator.
     */
    public PhotoRulesIntervalRemoveMutation(WevoRandom random) {
        this.random = random;
    }

    /**
     * This method takes set defined as sum of intervals
     * and mutates it by removing random interval. For example
     * given set [0, 10] + [15, 20] (represented as list
     * [0, 10, 15, 20]) the result might be [0, 10].
     * 
     * @param singleSet Set of intervals.
     * @return Mutated set of intervals.
     */
    List<Integer> apply(List<Integer> singleSet) {
      if (singleSet.size() <= 2) {
        return singleSet;
      }
      List<Integer> newSingleSet =  new ArrayList<Integer>(singleSet);
      Collections.copy(newSingleSet, singleSet);
      int chosenInterval = random.nextInt(0, newSingleSet.size() / 2);
      newSingleSet.remove(2 * chosenInterval + 1);
      newSingleSet.remove(2 * chosenInterval);

      return newSingleSet;
    }
}
