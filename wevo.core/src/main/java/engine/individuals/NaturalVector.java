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

package engine.individuals;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Vector of natural numbers.
 * 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 */
public class NaturalVector implements Serializable {

  /** Generated serial version UID. */
  private static final long serialVersionUID = 7605441327646741580L;

  /** Long values in the vector. */
  private long[] values;

  /**
   * Constructs {@link NaturalVector}.
   * @param size Size of the vector.
   */
  public NaturalVector(int size) {
    this(new long[size]);
  }
 
  /**
   * Constructs NaturalVector from a list of natural values.
   * @param list Natural values that form the basis for this individual.
   */
  public NaturalVector(long[] list) {
    values = new long[list.length];
    for (int i = 0; i < list.length; i++) {
      values[i] = list[i];
    }
  }

  /**
   * Copying constructor.
   * @param individual Individual to be copied.
   */
  public NaturalVector(NaturalVector individual) {
    this.values = individual.values.clone();
  }

  /**
   * Sets the ith value in the vector.
   * @param i Which value to set.
   * @param value What it set to.
   */
  public void setValue(int i, long value) {
    values[i] = value;
  }

  /**
   * Returns ith value in the vector.
   * @param i Which value to return.
   * @return Value of the bit.
   */
  public long getValue(int i) {
    return values[i];
  }

  /**
   * Returns size of the individual.
   * @return Size of the individual.
   */
  public int getSize() {
    return values.length;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Arrays.hashCode(values);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object object) {
    if (object == null || !(object instanceof NaturalVector)) {
      return false;
    }

    NaturalVector that = (NaturalVector) object;
    return Arrays.equals(this.values, that.values);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<");
    for (long b : values) {
      if (sb.length() > 1) {
        sb.append(", ");
      }
      sb.append(b);
    }
    sb.append(">");
    return sb.toString();
  }

  /**
   * Array of values in the individual.
   * @return Array of longs in the individual.
   */
  public long[] getValues() {
    return values;
  }
}
