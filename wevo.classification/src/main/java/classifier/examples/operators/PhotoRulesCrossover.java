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
import java.util.Iterator;
import java.util.List;

import classifier.Rule;
import classifier.examples.PhotoRuleCondition;
import classifier.examples.SatellitePhotoCategory;
import classifier.examples.PhotoRuleCondition.ConditionRepairer;
import engine.Operator;
import engine.Population;
import engine.utils.WevoRandom;

/**
 * Crossover operator for photo rules.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public final class PhotoRulesCrossover implements
      Operator<Rule<List<Integer>, SatellitePhotoCategory>> {

    /** Random number generator. */
    private final WevoRandom random;

    /**
     * Creates a photo rules crossover operator.
     * @param random Random number generator.
     */
    public PhotoRulesCrossover(WevoRandom random) {
      this.random = random;
    }

    /**
     * This method deals with mixing chromosomes process.
     * Note that method have side effects as it takes two
     * empty lists and adds elements to them. 
     * 
     * @param parent1 First parent chromosome.
     * @param parent2 Second parent chromosome.
     * @param offspring1 First parent chromosome. Should be empty.
     * @param offspring2 First parent chromosome. Should be empty.
     */
    private void crossTwoChromosomes(List<List<Integer>> parent1,
        List<List<Integer>> parent2,
        List<List<Integer>> offspring1,
        List<List<Integer>> offspring2) {
      int cutting = random.nextInt(0, parent1.size());

      for (int i = 0; i < parent1.size(); i++) {
        List<Integer> c1 = parent1.get(i);
        List<Integer> c2 = parent2.get(i);
        if (i < cutting) {
          offspring1.add(c1);
          offspring2.add(c2);
        } else if (i == cutting) {
          int cutting2 = random.nextInt(0, Math.min(c1.size(), c2.size()));
          List<Integer> newc1 = new ArrayList<Integer>();
          List<Integer> newc2 = new ArrayList<Integer>();

          newc1.addAll(c1.subList(0, cutting2));
          newc1.addAll(c2.subList(cutting2, c2.size()));
          newc2.addAll(c2.subList(0, cutting2));
          newc2.addAll(c1.subList(cutting2, c1.size()));
          offspring1.add(newc1);
          offspring2.add(newc2);
        } else {
          offspring1.add(c2);
          offspring2.add(c1);
        }
      }
    }

    /** 
     * {@inheritDoc}
     * 
     *  Crossover of two photo rules is made in simple way:
     *  firstly a band set is chosen in which crossing point
     *  will be determined. Chromosome parts which represents
     *  sets before chosen set goes to first offspring while those
     *  which lays after chosen set goes to second offspring.
     *  Part of chromosome representing chosen set is also cut
     *  and first part goes to first offspring and second goes to
     *  second.
     *
     *  For example - given two rules:
     *   [[10,20], [30,40,50,60], [85,95]]
     *   [[15,25], [20,50], [80,90]]
     *  and assuming that we choose second band set we can get offsprings:
     *   [[10,20], [30,50], [80,90]]
     *   [[15,25], [20,40,50,60], [85,95]].
     */
    public Population<Rule<List<Integer>, SatellitePhotoCategory>> apply(
        Population<Rule<List<Integer>, SatellitePhotoCategory>> population) {

      Iterator<Rule<List<Integer>, SatellitePhotoCategory>> iterator =
          population.getIndividuals().iterator();
      List<Rule<List<Integer>, SatellitePhotoCategory>> output = 
          new ArrayList<Rule<List<Integer>, SatellitePhotoCategory>>();
      Rule<List<Integer>, SatellitePhotoCategory> lonelyParent = null;

      // Let the lonely parent live in next generetion.
      if (population.size() % 2 != 0) {
        lonelyParent = iterator.next();
      }
      while (iterator.hasNext()) {
        Rule<List<Integer>, SatellitePhotoCategory> parent1 = iterator.next();
        Rule<List<Integer>, SatellitePhotoCategory> parent2 = iterator.next();

        List<List<Integer>> oldSets1 =
            ((PhotoRuleCondition) parent1.getCondition()).getSets();
        List<List<Integer>> oldSets2 = 
            ((PhotoRuleCondition) parent2.getCondition()).getSets();
        List<List<Integer>> newSets1 = new ArrayList<List<Integer>>();
        List<List<Integer>> newSets2 = new ArrayList<List<Integer>>();

        crossTwoChromosomes(oldSets1, oldSets2, newSets1, newSets2);

        PhotoRuleCondition condition1 = new PhotoRuleCondition(newSets1);
        ConditionRepairer.repairCondition(condition1);
        PhotoRuleCondition condition2 = new PhotoRuleCondition(newSets2);
        ConditionRepairer.repairCondition(condition2);

        Rule<List<Integer>, SatellitePhotoCategory> rule1 = 
            new Rule<List<Integer>, SatellitePhotoCategory>(condition1,
                parent1.getCategory());
        Rule<List<Integer>, SatellitePhotoCategory> rule2 = 
            new Rule<List<Integer>, SatellitePhotoCategory>(condition2,
                parent2.getCategory());
        output.add(rule1);
        output.add(rule2);
      }

      if (lonelyParent != null) {
        output.add(lonelyParent);
      }

      return new Population<Rule<List<Integer>,
          SatellitePhotoCategory>>(output);
    }
}

