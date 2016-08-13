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

import engine.Population;

/**
 * This interface is used to generate random rule
 * or rules population.
 *
 * @param <D> Data sample type.
 * @param <C> Category type.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public interface RulesGenerator<D, C> {

    /**
     * Generates population of random rules bounded with given category.
     * @param category Category in which rules should check for membership.
     * @param populationSize Size of generated population.
     * @return Population of rules for given category.
     */
    Population<? extends Rule<D, C>> generateRulesPopulation(
        C category, int populationSize);

    /**
     * Generates population of random rules bounded with given category
     * using knowledge obtained from learning set.
     * @param category Category in which rules should check for membership.
     * @param populationSize Size of generated population.
     * @param learningSet Set of classified data samples.
     * @return Population of rules for given category.
     */
    Population<? extends Rule<D, C>> generateRulesPopulationUsingLearningSet(
        C category, int populationSize, 
        List<ClassifiedSample<D, C>> learningSet);
}
