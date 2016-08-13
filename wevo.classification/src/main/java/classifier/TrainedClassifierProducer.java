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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This object should be use to produce trained classifier
 * or classifiers in case of many input sources.
 * 
 * @param <D> Type of single data point.
 * @param <C> Type of category.
 * 
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class TrainedClassifierProducer<D, C> {

    /**
     * Produces trained classifier for given input iterator
     * and rules.
     * @param rules Set of trained rules with their values.
     * @param dataToClassify Data set to classify.
     * @return Created classifier. 
     */
    public TrainedClassifier<D, C> getClassifierForSingleInput(
        LinkedHashMap<Rule<D, C>, Double> rules,
        Iterator<D> dataToClassify) {
      return new TrainedClassifier<D, C>(rules, dataToClassify);
    }

    /**
     * Produces list of trained classifier one per every single 
     * input data sources.
     * @param rules Set of trained rules with their values.
     * @param listOfDataToClassify List of data sets to classify.
     * @return List of created classifiers. 
     */
    public List<TrainedClassifier<D, C>> getClassifierForInputList(
        LinkedHashMap<Rule<D, C>, Double> rules,
        List<Iterator<D>> listOfDataToClassify) {
      List<TrainedClassifier<D, C>> classifiers = 
          new LinkedList<TrainedClassifier<D, C>>();

      for (Iterator<D> iterator : listOfDataToClassify) {
        classifiers.add(new TrainedClassifier<D, C>(rules, iterator));
      }
      return classifiers;
    }
}
