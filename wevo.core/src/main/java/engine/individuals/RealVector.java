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
import java.util.LinkedList;
import java.util.List;

import engine.Population;
import engine.utils.WevoRandom;


/**
 * Vector of real numbers.
 * 
 * @author Marcin Brodziak (marcin@nierobcietegowdomu.pl)
 */
public class RealVector implements Serializable {

  /** Generated serial version UID. */
  private static final long serialVersionUID = 4194451430252795568L;

  /** Binary values in the vector. */
  private double[] values;

  /**
   * Constructs {@link RealVector}.
   * @param size Size of the vector.
   */
  public RealVector(int size) {
    this(new double[size]);
  }
 
  /**
   * Constructs RealVector from a list of real values.
   * @param list Real values that form the basis for this individual.
   */
  public RealVector(double[] list) {
    values = new double[list.length];
    for (int i = 0; i < list.length; i++) {
      values[i] = list[i];
    }
  }

  /**
   * Sets the ith value in the vector.
   * @param i Which value to set.
   * @param value What it set to.
   */
  public void setValue(int i, double value) {
    values[i] = value;
  }

  /**
   * Returns ith value in the vector.
   * @param i Which value to return.
   * @return Value of the bit.
   */
  public double getValue(int i) {
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
    if (object == null || !(object instanceof RealVector)) {
      return false;
    }

    RealVector that = (RealVector) object;
    return Arrays.equals(this.values, that.values);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<");
    for (double b : values) {
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
   * @return Array of doubles in the individual.
   */
  public double[] getValues() {
    return values;
  }

  /**
   * Generates random individual of given length using given generator.
   * Values in each chromosome is taken randomly with uniform distribution from
   * given range.
   * @param generator Random number generator.
   * @param individualLength Length of individual.
   * @param lowerLimit Lower range limit.
   * @param upperLimit Upper range limit.
   * @return Real individual of given length.
   */
  public static RealVector generate(
      final WevoRandom generator, 
      final int individualLength,
      final int lowerLimit,
      final int upperLimit) {

    double[] chromosome = new double[individualLength];
    for (int j = 0; j < chromosome.length; j++) {
      chromosome[j] = generator.nextDouble(lowerLimit, upperLimit);
    }
    return new RealVector(chromosome);
  }

  /**
   * Generates initial population with random individuals of given length
   * and given size. Values in each chromosome is limited by given range
   * limits.
   * @param generator Random numbers generator.
   * @param individualLength Length of each individual.
   * @param individuals Number of individuals in population.
   * @param lowerLimit Lower range limit.
   * @param upperLimit Upper range limit.
   * @return Initial population.
   */
  public static Population<RealVector> 
      generatePopulationOfRandomRealIndividuals(
      final WevoRandom generator,
      final int individualLength,
      final int individuals,
      final int lowerLimit,
      final int upperLimit) {
    List<RealVector> result = new LinkedList<RealVector>();
    for (int i = 0; i < individuals; i++) {
      result.add(RealVector.generate(generator, individualLength,
          lowerLimit, upperLimit));
    }
    return new Population<RealVector>(result);
  }
}
