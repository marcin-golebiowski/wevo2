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
import java.util.List;

import classifier.Rule;
import classifier.examples.PhotoRuleCondition;
import classifier.examples.SatellitePhotoCategory;
import classifier.examples.PhotoRuleCondition.ConditionRepairer;
import engine.Operator;
import engine.Population;
import engine.utils.WevoRandom;

/**
 * Mutation operator for photo rules. It works by mutating intervals.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRulesIntervalMutation implements
    Operator<Rule<List<Integer>, SatellitePhotoCategory>> {

    /** Random number generator. */
    private final WevoRandom random;

    /** Probability that mutation occurs. */
    private final double mutationProbability;

    /** Adding interval mutation object. */
    private final PhotoRulesIntervalAddMutation addMutation;

    /** Cutting interval mutation object. */
    private final PhotoRulesIntervalCutMutation cutMutation;

    /** Removing interval mutation object. */
    private final PhotoRulesIntervalRemoveMutation removeMutation;

    /**
     * Standard constructor.
     * @param random Random number generator.
     * @param mutationProbability Mutation probability.
     * @param addMutation Add interval mutation.
     * @param cutMutation Cut interval mutation. 
     * @param removeMutation Remove interval mutation.
     */
    public PhotoRulesIntervalMutation(WevoRandom random,
            double mutationProbability,
            PhotoRulesIntervalAddMutation addMutation,
            PhotoRulesIntervalCutMutation cutMutation,
            PhotoRulesIntervalRemoveMutation removeMutation) {
        this.random = random;
        this.mutationProbability = mutationProbability;
        this.addMutation = addMutation;
        this.cutMutation = cutMutation;
        this.removeMutation = removeMutation;
    }


    /**
     * Mutates rule using randomly chosen interval mutations:
     * {@link PhotoRulesMutation#mutateByCuttingInterval(List)}
     * {@link PhotoRulesMutation#mutateByAddingInterval(List)}
     * {@link PhotoRulesMutation#mutateByRemovingInterval(List)}.
     * @param rule Rule to mutate.
     * @return Mutated rule.
     */
    private Rule<List<Integer>, SatellitePhotoCategory> intervalMutation(
        Rule<List<Integer>, SatellitePhotoCategory> rule) {
      PhotoRuleCondition cond = (PhotoRuleCondition) rule.getCondition();
      List<List<Integer>> newSets = new ArrayList<List<Integer>>();
      List<List<Integer>> oldSets = cond.getSets();

      for (List<Integer> singleSet : oldSets) {
        List<Integer> mutatedSingleSet = 
            new ArrayList<Integer>(singleSet);
        //Collections.copy(mutatedSingleSet, singleSet);

        if (random.nextDouble(0.0, 1.0) < mutationProbability) {
          // MagicNumber off
          int mutationType = random.nextInt(0, 3);
          // MagicNumber off

          if (mutationType == 0) {
            mutatedSingleSet = cutMutation.apply(singleSet);
          } 
          if (mutationType == 1) {
              mutatedSingleSet = removeMutation.apply(singleSet);
          }
          if (mutationType == 2) {
              mutatedSingleSet = addMutation.apply(singleSet);
          }
        }
        newSets.add(mutatedSingleSet);
      }
      PhotoRuleCondition condition = new PhotoRuleCondition(newSets);
      ConditionRepairer.repairCondition(condition);

      return new Rule<List<Integer>, SatellitePhotoCategory>(
              condition, rule.getCategory());
    }


    /** {@inheritDoc}. */
    public Population<Rule<List<Integer>, SatellitePhotoCategory>> apply(
        Population<Rule<List<Integer>, SatellitePhotoCategory>> population) {
      Population<Rule<List<Integer>, SatellitePhotoCategory>> newPopulation =
          new Population<Rule<List<Integer>, SatellitePhotoCategory>>();

      for (Rule<List<Integer>, SatellitePhotoCategory> rule
          : population.getIndividuals()) {
        Rule<List<Integer>, SatellitePhotoCategory> mutatedRule =
            intervalMutation(rule);
        newPopulation.addIndividual(mutatedRule);
      }

      return newPopulation;
    }
}
