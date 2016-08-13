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
package classifier.examples;

import java.util.List;

import classifier.RuleCondition;

/**
 * Classification condition for multispectral photos. In case of
 * such photos data point is simply a pixel made of n integer
 * values. Condition is made of n sets. Sets are defined as union
 * of ranges on integer numbers. Representation of sets is simple:
 * we define union: [a1, a2] + [a3, a4] + ... + [a_(k-1), a_k] as a
 * simple list of integers: [a1, a2, a3, a4, ..., a_(k-1), a_k].
 * 
 * Pixel satisfies condition if every i-th value of this vector
 * belongs to i-th set defined by sets list.
 * 
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRuleCondition extends RuleCondition<List<Integer>> {

    /**
     * Class for repairing broken photo rule conditions.
     * @author Lukasz Krawiec (lmkrawiec@gmail.com)
     * @author Michal Anglart (anglart.michal@gmail.com)
     */
    public static class ConditionRepairer {

      /** 
       * This method repairs two things in condition. Firstly it
       * reorders list which represent intervals. For example given list:
       * [2,1,4,3] which represents intervals union: [1,2]+[3,4] it
       * swaps interval borders resulting in:
       * [1,2,3,4].
       * Secondly it cuts down the intervals which ranges outside bound
       * of single pixel band. For example given list:
       * [-10,5,100,300] it produces:
       * [0,5,100,255] when band bounds is 0 and 256.
       * 
       * @param cond Condition to repair.
       */
      public static void repairCondition(PhotoRuleCondition cond) {
        for (List<Integer> set : cond.bandSets) {
          for (int i = 0; i < set.size() / 2; i++) {
            final int idx1 = 2 * i;
            final int idx2 = 2 * i + 1;

            if (set.get(idx1) > set.get(idx2)) {
              int tmp = set.get(idx1);
              set.set(idx1, set.get(idx2));
              set.set(idx2, tmp);
            }
            set.set(idx1, Math.max(
                PhotoRulesGenerator.LOWER_BOUND_ON_BAND, set.get(idx1)));
            set.set(idx2, Math.min(
                PhotoRulesGenerator.UPPER_BOUND_ON_BAND - 1, set.get(idx2)));
          }
        }
      }
    }

    /** Limits number of intervals which create single set. */
    public static final int MAX_INTERVALS = 3;

    /** List of sets for checking pixel satisfiability. */
    private List<List<Integer>> bandSets;

    /**
     * Standard constructor.
     * @param bandSets List of sets for pixel checking.
     */
    public PhotoRuleCondition(List<List<Integer>> bandSets) {
      // TODO(anglart.michal): each internal list should be non-empty, and
      // it's size should be divisible by 2 and what's more there should 
      // be no more intervals than limit
      this.bandSets = bandSets;
    }

    /**
     * Getter for sets. For testing purposes.
     * @return Sets.
     */
    public List<List<Integer>> getSets() {
      return bandSets;
    }

    /**
     * Checks whether pixel satisfies this condition.
     * Condition is satisfied iff all values from sample
     * vector belongs to set defined by ranges.
     * @param dataSample Photo pixel.
     * @return True iff condition is satisfied by pixel.
     */
    @Override
    public boolean isSatisfied(List<Integer> dataSample) {
      if (bandSets.size() != dataSample.size()) {
        throw new IllegalStateException("Sets list should be"
            + " as long as pixel vector.");
      }

      for (int i = 0; i < dataSample.size(); i++) {
        List<Integer> ranges = bandSets.get(i);

        // pixel doesn't belong to ranges union
        if (!isPixelValueInSet(dataSample.get(i), ranges)) {
          return false;
        }
      }

      return true;
    }

    /**
     * Measures quality of condition satisfiability. 
     * In this case quality is number from [0.0, 1.0] is
     * obtained as a number of pixel values which
     * belongs to sets divided by number of all pixel
     * values.
     * @param dataSample Photo pixel.
     * @return Measure of satisfiability quality.
     */
    @Override
    public double satisfyQuality(List<Integer> dataSample) {
      if (bandSets.size() != dataSample.size()) {
        throw new IllegalStateException("Sets list should be twice"
            + " as long as pixel vector.");
      }
      int satisfiedConditions = 0;

      for (int i = 0; i < dataSample.size(); i++) {
        List<Integer> ranges = bandSets.get(i);

        // pixel belong to ranges union
        if (isPixelValueInSet(dataSample.get(i), ranges)) {
          satisfiedConditions++;
        }
      }
      return ((double) satisfiedConditions) / ((double) dataSample.size());
    }

    /**
     * Checks if value of pixel belongs to union of ranges. 
     * @param pixelValue Value in pixel vector.
     * @param union List which define union of ranges.
     * @return True iff pixel value belongs to union of ranges.
     */
    private boolean isPixelValueInSet(int pixelValue, List<Integer> union) {
      boolean inSet = false;

      for (int j = 0; j < union.size(); j += 2) {
        int nextIndex = j + 1;
        // swap parameter values order
        // TODO(anglart.michal): swaping shouldn't be needed: check
        if (union.get(j) > union.get(nextIndex)) {
          int tmp = union.get(j);
          union.set(j, union.get(nextIndex));
          union.set(nextIndex, tmp);
        }

        // set inSet variable if pixel value is in at least one range
        if (union.get(j) <= pixelValue && pixelValue <= union.get(nextIndex)) {
          inSet = true;
          break;
        }
      }
      return inSet;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
      return bandSets.toString();
    }
}
