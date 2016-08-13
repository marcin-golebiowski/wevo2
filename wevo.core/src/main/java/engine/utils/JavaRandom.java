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
package engine.utils;

import java.util.Random;

/**
 * Standard random number generator based on Java random number generator.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class JavaRandom implements WevoRandom {

  /** Random number generator. */
  private Random generator;

  /** Default constructor using current time as seed. */
  public JavaRandom() {
    this(System.currentTimeMillis());
  }

  /**
   * Constructor using current time as seed.
   * @param clock Time measurement utility.
   */
  public JavaRandom(WevoClock clock) {
    this(clock.getCurrentTimeMillis());
  }

  /**
   * Constructor with seed.
   * @param seed Seed to the random number generator.
   */
  public JavaRandom(long seed) {
    this.generator = new Random(seed);
  }

  /** {@inheritDoc} */
  public Random getInnerGenerator() {
    return generator;
  }

  /** {@inheritDoc} */
  public boolean nextBoolean() {
    return generator.nextBoolean();
  }

  /** {@inheritDoc} */
  public double nextDouble(double lowerLimit, double upperLimit) {
    return generator.nextDouble() * (upperLimit - lowerLimit) + lowerLimit;
  }

  /** {@inheritDoc} */
  public int nextInt(int lowerLimit, int upperLimit) {
    return generator.nextInt(upperLimit - lowerLimit) + lowerLimit;
  }

  /** {@inheritDoc} */
  public long nextLong(long lowerLimit, long upperLimit) {
    long pickedValue = Math.abs(generator.nextLong());
    pickedValue %= upperLimit - lowerLimit;

    return pickedValue + lowerLimit;
  }

  /** {@inheritDoc} */
  public double nextGaussian() { 
    return generator.nextGaussian();
  }

}
