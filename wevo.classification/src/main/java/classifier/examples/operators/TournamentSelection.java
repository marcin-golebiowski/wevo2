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
import engine.utils.WevoRandom;

/**
 * Returns part of the population using tournament method.
 * 
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * 
 * @param <T> Type of the individual in the population.
 */
public class TournamentSelection<T> implements Operator<T> {

    /** Fraction of population to take. */
    private final double ratio;

    /** Fraction of population to take during tournament. */
    private final double tournamentRatio;

    /** Objective function for individual evaluating. */
    private final ObjectiveFunction<T> objectiveFunction;

    /** Random number generator. */
    private final WevoRandom random;

    /**
     * Standard constructor.
     * @param objectiveFunction Objective function for individual evaluating.
     * @param ratio Fraction of population to take.
     * @param tournamentRatio Fraction of population to take during tournament.
     * @param random Random number generator.
     */
    public TournamentSelection(ObjectiveFunction<T> objectiveFunction,
            double ratio, double tournamentRatio, WevoRandom random) {
      this.objectiveFunction = objectiveFunction;
      this.ratio = ratio;
      this.tournamentRatio = tournamentRatio;
      this.random = random;
    }

    /**
     * {@inheritDoc} 
     */
    public Population<T> apply(Population<T> population) {
      int border = (int) Math.round(ratio * population.size());

      List<T> result = new ArrayList<T>();
      for (int i = 0; i < border; i++) {
        result.add(getWinningIndividual(population));
      }

      Population<T> resultPopulation = new Population<T>(result);
      // TODO Auto-generated method stub
      return Population.shuffle(random, resultPopulation);
    }

    /**
     * Gets individual which wins single tournament. First it
     * gets random subpopulation, and then chooses best individual
     * as a winner.
     * @param population Original population.
     * @return Tournament winner.
     */
    private T getWinningIndividual(Population<T> population) {
      Comparator<T> comparator = new Comparator<T>() {
        public int compare(T o1, T o2) {
          Double v1 = objectiveFunction.compute(o1);
          Double v2 = objectiveFunction.compute(o2);
          return v1.compareTo(v2);
        }
      };
      Population<T> tournamentPopulation =
          prepareRandomPopulationFraction(population);
      List<T> individuals = tournamentPopulation.getIndividuals();

      return Collections.max(individuals, comparator);
    }

    /**
     * Prepares population for tournament. It simply gets
     * random individuals from population basing on ratio
     * for tournaments.
     * @param population Original population. 
     * @return Population prepared for tournament.
     */
    private Population<T> prepareRandomPopulationFraction(
        Population<T> population) {
      int border = (int) Math.round(tournamentRatio * population.size());

      Population.shuffle(random, population);
      List<T> result = new ArrayList<T>();
      for (int i = 0; i < border; i++) {
        result.add(population.getIndividuals().get(i));
      }

      return new Population<T>(result);
    }
}
