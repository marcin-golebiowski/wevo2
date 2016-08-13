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
package samples.objectivefunctions;

import engine.ObjectiveFunction;
import engine.individuals.BinaryVector;

/**
 * One Max objective function. Computes the number of non-zero
 * bits in the individual.
 *
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 */
public class OneMax implements ObjectiveFunction<BinaryVector> {
  /** {@inheritDoc} */
  public double compute(BinaryVector o) {
    int result = 0;
    for (int i = 0; i < o.getSize(); i++) {
      if (o.getBit(i)) {
        result++;
      }
    }
    return result;
  }
}
