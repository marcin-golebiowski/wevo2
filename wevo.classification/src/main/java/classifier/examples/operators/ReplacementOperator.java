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
import java.util.Comparator;
import java.util.List;

import engine.ObjectiveFunction;
import engine.Operator;
import engine.Population;
import engine.utils.JavaRandom;

/**
 * Replacement operator.
 * 
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * 
 * @param <T> Type of the individual in the population.
 */
public class ReplacementOperator<T> implements Operator<T> {

    /** Objective function for individuals computing. */
    private final ObjectiveFunction<T> objectiveFunction;

    /** Operator from which saved population is retrieved. */
    private final SavePopulationOperator<T> savePopulationOperator;

    /**
     * Standard constructor.
     * @param objectiveFunction Objective function for individuals computing.
     * @param savePopulationOperator Operator for saving and retrieving 
     *   population.
     */
    public ReplacementOperator(ObjectiveFunction<T> objectiveFunction,
        SavePopulationOperator<T> savePopulationOperator) {
      this.objectiveFunction = objectiveFunction;
      this.savePopulationOperator = savePopulationOperator;
    }

    /**
     * {@inheritDoc}
     * 
     * Applies operator to two populations. First is retrieved from
     * operator which saved it earlier and second is actual population.
     * It uses mi + lambda approach where best individuals (number limited
     * by original population size) is taken from union of saved and current
     * population.
     *
     * @param population Actual population.
     * @return Resulted population.
     */
    public Population<T> apply(Population<T> population) {
      Population<T> savedPopulation = 
          savePopulationOperator.getSavedPopulation();
      if (savedPopulation == null) {
          throw new IllegalStateException("There is no saved population");
      }
      List<T> oldIndividuals = savedPopulation.getIndividuals();
      List<T> newIndividuals = population.getIndividuals();
      List<T> all = new ArrayList<T>(oldIndividuals.size()
          + newIndividuals.size());
      all.addAll(oldIndividuals);
      all.addAll(newIndividuals);

      Comparator<T> comparator = new Comparator<T>() {
        public int compare(T o1, T o2) {
          Double v1 = objectiveFunction.compute(o1);
          Double v2 = objectiveFunction.compute(o2);
          return -v1.compareTo(v2);
        }
      };
      // Sort rules in descending order
      Collections.sort(all, comparator);

      List<T> individuals = new ArrayList<T>();
      int i = 0;
      while (i < savedPopulation.size()) {
        individuals.add(all.get(i));
        i++;
      }
      Population<T> resultPopulation = new Population<T>(individuals); 
      return Population.shuffle(new JavaRandom(), resultPopulation);
    }

}
