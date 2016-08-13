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
package classifier;

import java.util.List;

import classifier.data.ClassifiedSample;
import engine.ObjectiveFunction;

/**
 * Objective function for learning classifier system.
 * @param <D> Data sample type.
 * @param <C> Category type.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class ClassifierObjectiveFunction<D, C> 
      implements ObjectiveFunction<Rule<D, C>> {

    // TODO(anglart.michal|lmkrawiec): write tests!

    /** Set of learning data points. */
    private List<ClassifiedSample<D, C>> learningSet;

    /**
     * Standard constructor.
     * @param learningSet Set of learning data points.
     */
    public ClassifierObjectiveFunction(
        List<ClassifiedSample<D, C>> learningSet) {
      this.learningSet = learningSet;
    }

    /** {@inheritDoc} */
    public double compute(Rule<D, C> individual) {
      int value = 0;

      for (ClassifiedSample<D, C> dataPair : learningSet) {
        C category = individual.getCategory();
        boolean inCategoryByRule =
            individual.classify(dataPair.getDataSample());
        boolean inCategoryByExpert =
            category.equals(dataPair.getCategory());

        if (inCategoryByExpert == inCategoryByRule) {
          value++;
        }
      }
      return value;
    }
}
