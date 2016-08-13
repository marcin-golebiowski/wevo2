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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * This class is a set of trained rules (one for each category).
 * It is used to classify big amounts of data by reading from
 * iterator of data points and producing categories for each
 * point.
 * 
 * @param <D> Type of single data point.
 * @param <C> Type of category.
 *
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class TrainedClassifier<D, C>  implements Iterator<C> {

    // TODO(anglart.michal|lmkrawiec): write tests!

    /** 
     * Set of rules - one per each category. Rule is bounded with it's
     * objective function value. LinkedHashMap should be used here.
     */
    private LinkedHashMap<Rule<D, C>, Double> rules;

    /** Iterator which produces data points to classify. */
    private Iterator<D> dataToClassify;

    /**
     * Standard constructor.
     * We assume that size of each list is equal.
     * @param rules Set of rules. Note that one should set
     *   objective function values of every rule in this list.
     * @param dataToClassify Iterator which produces data points.
     */
    public TrainedClassifier(LinkedHashMap<Rule<D, C>, Double> rules, 
          Iterator<D> dataToClassify) {
      this.rules = rules;
      this.dataToClassify = dataToClassify;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
      return dataToClassify.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public C next() {
      // Get next data point to classify.
      D dataPoint = dataToClassify.next();

      // Collect rules satisfied by data point
      LinkedHashMap<Rule<D, C>, Double> satisfiedRules = 
          new LinkedHashMap<Rule<D, C>, Double>();
      for (Entry<Rule<D, C>, Double> ruleEntry : satisfiedRules.entrySet()) {
        if (ruleEntry.getKey().classify(dataPoint)) {
          satisfiedRules.put(ruleEntry.getKey(), ruleEntry.getValue());
        }
      }

      return getCategoryBasingSatisfiedRules(dataPoint, satisfiedRules);
    }

    /**
     * Gets category basing on current data point and rules satisfied
     * by this point.
     * @param dataPoint Data point used to get fuzzy membership if needed.
     * @param satisfiedRules List of satisfied rules.
     * @return Chosen category.
     */
    private C getCategoryBasingSatisfiedRules(D dataPoint,
        LinkedHashMap<Rule<D, C>, Double> satisfiedRules) {

      // There are no satisfied rules, so choose one with best fuzzy 
      // membership value.
      if (satisfiedRules.size() == 0) {
          LinkedHashMap<Rule<D, C>, Double> rulesWithFuzzyValues = 
              new LinkedHashMap<Rule<D, C>, Double>();

          // Create new mapping from rules to fuzzy membership values.
          for (Entry<Rule<D, C>, Double> ruleEntry : rules.entrySet()) {
            rulesWithFuzzyValues.put(ruleEntry.getKey(),
                ruleEntry.getKey().classifyFuzzyMembership(dataPoint));
          }

          Comparator<Entry<Rule<D, C>, Double>> comparator =
              createMapComparator();
          Rule<D, C> bestRule = Collections.max(
              rulesWithFuzzyValues.entrySet(), comparator).getKey();
          return bestRule.getCategory();

      } 

      // there is only one rule, so return it's category
      if (satisfiedRules.size() == 1) {
          for (Entry<Rule<D, C>, Double> ruleEntry
              : satisfiedRules.entrySet()) {
              return ruleEntry.getKey().getCategory();
          }
      }

      // there are many satisfied rules, so choose one which had best
      // objective function value
      Comparator<Entry<Rule<D, C>, Double>> comparator =
          createMapComparator();
      Rule<D, C> bestRule = 
          Collections.max(satisfiedRules.entrySet(), comparator).getKey();
      return bestRule.getCategory();
    }

    /**
     * Remove method. Throws an exception since we don't want to remove any
     * data.
     */
    public void remove() {
      throw new UnsupportedOperationException();
    }

    /**
     * Creates comparator which takes map from rules to some double value.
     * Created comparator compares mapped values.
     * @return Comparator working on map.
     */
    private Comparator<Entry<Rule<D, C>, Double>> createMapComparator() {
      Comparator<Entry<Rule<D, C>, Double>> comparator =
          new Comparator<Entry<Rule<D, C>, Double>>() {
        public int compare(Entry<Rule<D, C>, Double> o1,
            Entry<Rule<D, C>, Double> o2) {
          return o1.getValue().compareTo(o2.getValue());
        }
      };
      return comparator;
    }
}
