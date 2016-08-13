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
 * Represents condition in single rule.
 * 
 * @param <D> Data sample type.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public abstract class RuleCondition<D> {

    /**
     * This method checks whether rule is satisfied by given data
     * sample.
     * @param dataSample Data sample for which satisfiability should be check.
     * @return True iff data sample satisfies this rule.
     */
    public abstract boolean isSatisfied(D dataSample);

    /** 
     * This method measures in what degree is the condition satisfied.
     * Return values from range [0.0, 1.0]. Value 0.0 is equivalent
     * to false in isSatisfied method, while value 1.0 is equivalent
     * to true in same method. Return value can be seen as fuzzy
     * boolean.
     * @param dataSample Data sample for which quality should be computed.
     * @return Value of satisfy quality.
     */
    public abstract double satisfyQuality(D dataSample);
}
