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

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import classifier.data.ClassifiedSample;
import engine.Algorithm;
import engine.Population;
import engine.operators.PopulationStatistics;

/**
 * Algorithm for learning process. This class is responsible for
 * using evolutionary algorithm to train rules and choose
 * best set of rules one for each category.
 * @param <D> Data sample type.
 * @param <C> Category type.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class LearningAlgorithm<D, C> {

    // TODO(anglart.michal|lmkrawiec): write tests!

    /** Logger. */
    private final Logger logger =
        Logger.getLogger(LearningAlgorithm.class.getCanonicalName());

    /** Learning multiset consists of classified samples. */
    private final List<ClassifiedSample<D, C>> learningMultiset;

    /** Set of different categories in learning multiset. */
    private final Set<C> categories;

    /** Random rules generator. */
    private final RulesGenerator<D, C> rulesGenerator;

    /** List of operators that the evolutionary algorithm is based on. */
    private final Algorithm<Rule<D, C>> evolutionaryAlgorithm;

    /** Size of population used in evolution. */
    private final int populationSize;

    /** Objective function used to obtain best rule at the end of evolution. */
    private final ClassifierObjectiveFunction<D, C> helperObjectiveFunction;

    /**
     * Standard constructor. Package visibility for testing.
     * @param learningMultiset Multiset of classified samples.
     * @param categories Set of all categories in expert data. 
     * @param rulesGenerator Generator of rules.
     * @param evolutionaryAlgorithm Evolutionary algorithm to use during learn.
     * @param populationSize Size of population used in evolution.
     * @param helperObjectiveFunction Objective function used to obtain 
     *     best rule.
     */
    // ParameterNumber off
    LearningAlgorithm(
        final List<ClassifiedSample<D, C>> learningMultiset,
        final Set<C> categories,
        final RulesGenerator<D, C> rulesGenerator,
        final Algorithm<Rule<D, C>> evolutionaryAlgorithm,
        final int populationSize,
        final ClassifierObjectiveFunction<D, C> helperObjectiveFunction) {
      this.learningMultiset = learningMultiset;
      this.categories = categories;
      this.rulesGenerator = rulesGenerator;
      this.evolutionaryAlgorithm = evolutionaryAlgorithm;
      this.populationSize = populationSize;
      this.helperObjectiveFunction = helperObjectiveFunction;
    }
    // ParameterNumber on

    /**
     * Standard constructor.
     * @param learningMultiset Set of classified samples.
     * @param rulesGenerator Generator of rules.
     * @param evolutionaryAlgorithm Evolutionary algorithm to use during learn.
     * @param populationSize Size of population used in evolution.
     */
    public LearningAlgorithm(
        final List<ClassifiedSample<D, C>> learningMultiset,
        final RulesGenerator<D, C> rulesGenerator,
        final Algorithm<Rule<D, C>> evolutionaryAlgorithm,
        final int populationSize) {
      this(learningMultiset, new HashSet<C>(), rulesGenerator,
        evolutionaryAlgorithm, populationSize, 
        new ClassifierObjectiveFunction<D, C>(learningMultiset));
    }

    /** Reads all categories in learning multiset. */
    private void prepare() { 
      for (ClassifiedSample<D, C> sample : learningMultiset) {
        categories.add(sample.getCategory());
      }
    }

    /**
     * Learning method for classification process. This method simply returns
     * set of best rules, which was trained with a use of evolutionary 
     * algorithm, bounded with their objective function values.
     * @return Set of rules with values.
     */
    public LinkedHashMap<Rule<D, C>, Double> learn() {
      prepare();

      LinkedHashMap<Rule<D, C>, Double> rules = 
          new LinkedHashMap<Rule<D, C>, Double>();

      // Find best rule for each category 
      logger.log(Level.INFO, "Learning proccess have started.");
      for (C category : categories) {
        Rule<D, C> bestRule = findBestRule(category);

        // Add best rule for current category and set it's OF value.
        rules.put(bestRule, helperObjectiveFunction.compute(bestRule));

        evolutionaryAlgorithm.reset();
      }
      logger.log(Level.INFO, "Learning proccess have ended.");

      return rules;
    }

    /**
     * Perform evolution and get best rule for given category. 
     * @param category Category for which rule should be establish.
     * @return Best rule from final population.
     */
    private Rule<D, C> findBestRule(C category) {
        // Generate random population of rules and add them to algorithm.
        evolutionaryAlgorithm.setPopulation(castToPopulation(
            rulesGenerator.generateRulesPopulationUsingLearningSet(
                category, populationSize, learningMultiset)));

        // Run the evolution.
        logger.log(Level.INFO, "Running evolutionary algorithm to obtain best"
            + " possible rules for category " + category.toString() + ".");
        evolutionaryAlgorithm.run();

        // Take the final population and get best individual.
        Population<Rule<D, C>> population =
            evolutionaryAlgorithm.getPopulation();
        PopulationStatistics<Rule<D, C>> statsOperator = 
            new PopulationStatistics<Rule<D, C>>(
                helperObjectiveFunction);
        statsOperator.apply(population);
        Rule<D, C> bestRule =
            statsOperator.getBestIndividual();

        logger.log(Level.INFO, "Evolution has ended. Best rule found for "
            + "category " + category.toString() + ".");

        return bestRule;
    }

    /**
     * Extracted method to avoid unchecked cast in 
     * {@link LearningAlgorithm#findBestRule(Object)}
     * method.
     * @param population Population to cast
     * @return Casted population.
     */
    @SuppressWarnings("unchecked")
    private Population<Rule<D, C>> castToPopulation(
        Population<? extends Rule<D, C>> population) {
      return (Population<Rule<D, C>>) population;
    }
}
