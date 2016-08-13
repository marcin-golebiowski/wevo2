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

import classifier.examples.PhotoRuleCondition;
import classifier.examples.PhotoRulesGenerator;
import engine.utils.WevoRandom;

/**
 * This class is responsible for mutating single
 * set in individual by adding random interval
 * of given length.
 * 
 * Note that this is not an operator as it doesn't
 * work on population.
 *
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRulesIntervalAddMutation {

    /** Random number generator. */
    private final WevoRandom random;

    /** Length of interval to add. */ 
    private final int lengthOfNewInterval;

    /**
     * Standard constructor.
     * @param random Random number generator.
     * @param lengthOfNewInterval Length of interval to add.
     */
    public PhotoRulesIntervalAddMutation(WevoRandom random,
        int lengthOfNewInterval) {
      this.random = random;
      this.lengthOfNewInterval = lengthOfNewInterval;
    }

    /**
     * This method takes set defined as sum of intervals
     * and mutates it by adding random interval. New interval
     * is centered in randomly chosen position and have length
     * specified by lengthOfNewInterval.
     * 
     * For example given set [0, 10] (represented as list [0, 10])
     * the result might be [0, 10] + [30, 50].
     * 
     * @param singleSet Set of intervals.
     * @return Mutated set of intervals.
     */
    List<Integer> apply(List<Integer> singleSet) {
      if (singleSet.size() >= 2 * PhotoRuleCondition.MAX_INTERVALS) {
        return singleSet;
      }

      List<Integer> newSingleSet =  new ArrayList<Integer>(singleSet);
      Collections.copy(newSingleSet, singleSet);
      int bandCenter = random.nextInt(PhotoRulesGenerator.LOWER_BOUND_ON_BAND,
              PhotoRulesGenerator.UPPER_BOUND_ON_BAND);
      int newIntervalLower = bandCenter - lengthOfNewInterval / 2;
      int newIntervalUpper = newIntervalLower + lengthOfNewInterval;

      newSingleSet.add(newIntervalLower);
      newSingleSet.add(newIntervalUpper);

      return newSingleSet;
    }
}
