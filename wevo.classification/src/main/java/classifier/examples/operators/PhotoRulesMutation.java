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

import java.util.List;

import classifier.Rule;
import classifier.examples.SatellitePhotoCategory;
import engine.Operator;
import engine.Population;
import engine.utils.WevoRandom;

/**
 * Mutation operator for photo rules.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRulesMutation implements
    Operator<Rule<List<Integer>, SatellitePhotoCategory>> {

    /** Random number generator. */
    private final WevoRandom random;

    /** Operator for mutating borders. */
    private final PhotoRulesBordersMutation bordersMutationOperator;

    /** Operator for mutating intervals. */ 
    private final PhotoRulesIntervalMutation intervalMutationOperator;


    /**
     * Standard constructor.
     * @param random Random number generator.
     * @param bordersMutationOperator Operator for border mutations.
     * @param intervalMutationOperator Operator for interval mutations.
     */
    public PhotoRulesMutation(WevoRandom random,
            PhotoRulesBordersMutation bordersMutationOperator,
            PhotoRulesIntervalMutation intervalMutationOperator) {
      this.random = random;
      this.bordersMutationOperator = bordersMutationOperator;
      this.intervalMutationOperator = intervalMutationOperator;
    }

    /** {@inheritDoc}. */
    public Population<Rule<List<Integer>, SatellitePhotoCategory>> apply(
        Population<Rule<List<Integer>, SatellitePhotoCategory>> population) {
      if (random.nextBoolean()) {
        return bordersMutationOperator.apply(population);
      } else {
        return intervalMutationOperator.apply(population);
      }
    }
}
