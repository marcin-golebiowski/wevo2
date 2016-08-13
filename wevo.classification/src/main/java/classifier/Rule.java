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

/**
 * Classification rule. Rule should be treated as an individual in 
 * evolutionary algorithm, but strictly speaking condition is
 * an object which evolves during evolution.
 * 
 * @param <D> Data sample type.
 * @param <C> Category type.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class Rule<D, C> {

    /** Condition under which rule classifies given data point. */
    private RuleCondition<D> condition;

    /** Category for which this rule should check pixel belonging. */
    private C category;

    /**
     * Standard constructor.
     * @param condition Condition under which rule classifies given data point.
     * @param category Category for created rule.
     */
    public Rule(RuleCondition<D> condition, C category) {
      this.condition = condition;
      this.category = category;
    }

    /**
     * Getter for category bounded with rule.
     * @return Category bounded with rule.
     */
    public C getCategory() {
      return category;
    }

    /**
     * Getter for parameters bounded with rule.
     * @return Parameters bounded with rule.
     */
    public RuleCondition<D> getCondition() {
      return condition;
    }

    /**
     * Classification method. Given point of data and basing on 
     * condition this method decides whether the point belongs to
     * category for which this rule was created.
     * @param dataSample Single data sample.
     * @return True iff data sample belongs to category bounded with rule.
     */
    public boolean classify(D dataSample) {
      return condition.isSatisfied(dataSample);
    }

    /**
     * Classification method. Given point of data and basing on 
     * condition this method returns value from range 0.0 - 1.0 which
     * describes fuzzy membership to the category. Value 0.0 means that
     * the point does not belong to category, while 1.0 means that the
     * point belongs to category.
     * @param dataSample Single data sample.
     * @return Value of fuzzy membership to the category.
     */
    public double classifyFuzzyMembership(D dataSample) {
      return condition.satisfyQuality(dataSample);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
      // Eclipse auto-generated method
      final int prime = 31;
      int result = 1;
      result = prime * result
          + ((category == null) ? 0 : category.hashCode());
      result = prime * result
          + ((condition == null) ? 0 : condition.hashCode());
      return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Rule<?, ?>) {
        Rule<D, C> that = castToRule(obj);
        return condition != null && condition.equals(that.condition)
            && category != null && category.equals(that.category);
      }
      return false;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
      return condition.toString() + " => " + category.toString();
    }

    /**
     * Perform unchecked cast to Rule.
     * @param obj Object to cast.
     * @return Casting result.
     */
    @SuppressWarnings("unchecked")
    private Rule<D, C> castToRule(Object obj) {
      return (Rule<D, C>) obj;
    }
}
