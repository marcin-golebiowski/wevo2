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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import classifier.Rule;
import classifier.examples.PhotoRuleCondition;
import classifier.examples.SatellitePhotoCategory;
import classifier.examples.PhotoRuleCondition.ConditionRepairer;
import engine.Operator;
import engine.Population;
import engine.utils.WevoRandom;

/**
 * Mutation operator for photo rules.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRulesBordersMutation implements
    Operator<Rule<List<Integer>, SatellitePhotoCategory>> {

    /**
     * Enumeration for mutation types. 
     * @author Lukasz Krawiec (lmkrawiec@gmail.com)
     * @author Michal Anglart (anglart.michal@gmail.com)
     */
    private enum MutationType {
      /** Mutation of lower border. */
      LOWER_BORDER_SHIFT,
      /** Mutation of upper border. */
      UPPER_BORDER_SHIFT,
      /** Mutation of both borders with same value. */
      BOTH_BORDERS_SAME_SHIFT,
      /** Mutation of both borders with different values. */
      BOTH_BORDERS_DIFF_SHIFT;

      /** Values list of this enum. */
      private static final List<MutationType> VALUES =
          Collections.unmodifiableList(Arrays.asList(values()));

      /**
       * Return random enum value.
       * @param random Random number generator.
       * @return Random enum value.
       */
      public static MutationType random(WevoRandom random)  {
        // Random must be injected here. It's not possible to create it as
        // a field in this enum since it wouldn't be possible to inject
        // generator so the only way of testing would be mocking the enum.
        // Which is not possible since enum is final class...
        return VALUES.get(random.nextInt(0, VALUES.size()));
      }
    }

    /** Random number generator. */
    private final WevoRandom random;

    /** Probability that mutation occurs. */
    private final double mutationProbability;

    /** Sigma value (std deviation) for gaussian random generation. */
    private final double sigma;


    /**
     * Creates a mutation operator object.
     * @param random Random number generator.
     * @param mutationProbability Probability that mutation occurs.
     * @param sigma Sigma value (std deviation) for gaussian random.
     */
    public PhotoRulesBordersMutation(WevoRandom random,
        double mutationProbability,
        double sigma) {
      this.random = random;
      this.mutationProbability = mutationProbability;
      this.sigma = sigma;
    }

    /**
     * This method takes a set defined as sum of intervals
     * and mutates it by shifting borders in every interval 
     * with given probability. It randomly chooses whether 
     * lower border should be shifted, upper, or both with
     * same or different value.
     * 
     * For example given set [0, 10] + [15, 20] (represented
     * as list [0, 10, 15, 20]) the result might be 
     * [1, 11] + [13, 25].
     * 
     * Package-visibilty for testing.
     * 
     * @param singleSet Set of intervals.
     * @return Mutated set of intervals.
     */
    List<Integer> shiftBorders(List<Integer> singleSet) {
      int noOfIntervals = singleSet.size() / 2;
      List<Integer> newSingleSet = new ArrayList<Integer>();

      for (int i = 0; i < noOfIntervals; i++) {
        int shift1 = 0;
        int shift2 = 0;
        if (random.nextDouble(0.0, 1.0) < mutationProbability) {
          // Please note that actually those cases are not exclusive
          // since for example case1 is a special case of case3 where
          // shift2 = 0. This may influence on distribution of random 
          // mutations, but with a very small effect.
          switch(MutationType.random(random)) {
            case LOWER_BORDER_SHIFT:
              shift1 = (int) (random.nextGaussian() * sigma);
              break;
            case UPPER_BORDER_SHIFT:
              shift2 = (int) (random.nextGaussian() * sigma);
              break;
            case BOTH_BORDERS_SAME_SHIFT:
              shift1 = (int) (random.nextGaussian() * sigma);
              shift2 = shift1;
              break;
            case BOTH_BORDERS_DIFF_SHIFT:
              shift1 = (int) (random.nextGaussian() * sigma);
              shift2 = (int) (random.nextGaussian() * sigma);
              break;
            default:
              break;
          }
        }
        newSingleSet.add(singleSet.get(2 * i) + shift1);
        newSingleSet.add(singleSet.get(2 * i + 1) + shift2);
      }
      return newSingleSet;
    }

    /** {@inheritDoc}. */
    public Population<Rule<List<Integer>, SatellitePhotoCategory>> apply(
        Population<Rule<List<Integer>, SatellitePhotoCategory>> population) {
      Population<Rule<List<Integer>, SatellitePhotoCategory>> newPopulation =
          new Population<Rule<List<Integer>, SatellitePhotoCategory>>();

      for (Rule<List<Integer>, SatellitePhotoCategory> rule
          : population.getIndividuals()) {
        List<List<Integer>> oldSets = 
            ((PhotoRuleCondition) rule.getCondition()).getSets();
        List<List<Integer>> newSets = new ArrayList<List<Integer>>();

        for (List<Integer> singleSet : oldSets) {
          newSets.add(shiftBorders(singleSet));
        }

        PhotoRuleCondition condition = new PhotoRuleCondition(newSets);
        ConditionRepairer.repairCondition(condition);

        Rule<List<Integer>, SatellitePhotoCategory> mutatedRule =
            new Rule<List<Integer>, SatellitePhotoCategory>(condition,
                rule.getCategory());
        newPopulation.addIndividual(mutatedRule);
      }

      return newPopulation;
    }
}
