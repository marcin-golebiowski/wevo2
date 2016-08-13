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
 * Individual representing permutation.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (michal.anglart@gmail.com)
 */
public class Permutation implements Serializable {

  /** Generated serial version UID. */
  private static final long serialVersionUID = -1188762353837936477L;

  /** Chromosome of the individual. */
  private final int[] values;

  /**
   * Constructor accepting permutation size.
   * @param size Size of the individual.
   */
  public Permutation(int size) {
    this.values = new int[size];
  }

  /**
   * Constructor accepting genes. Genes have to form a valid permutation.
   * @param genes Genes to be set.
   */

  public Permutation(final int[] genes) {
    this.values = genes;
  }

  /**
   * Copying constructor.
   * @param individual Individual to be copied.
   */
  public Permutation(Permutation individual) {
    this(individual.getValues());
  }

  /**
   * Returns chromosome of the individual.
   * @return Chromosome of the individual.
   */
  public int[] getValues() {
    return this.values.clone();
  }

  /**
   * Returns gene value at given position.
   * @param position Position.
   * @return Gene value at given position.
   */
  public int getValue(final int position) {
    return values[position];
  }

  /**
   * Transposes genes on given positions.
   * @param i First gene position. Must be within individual size limit.
   * @param j Second gene position. Must be within individual size limit.
   */
  public void transpose(final int i, final int j) {
    final int geneValueI = values[i];
    final int geneValueJ = values[j];
    values[j] = geneValueI;
    values[i] = geneValueJ;
  }

  /**
   * Returns size of the individual.
   * @return Size of the individual. 
   */
  public int getSize() {
    return values.length;
  }

  /**
   * Returns string representation of this permutation.
   * @return String representation of this permutation.
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("<");
    for (long value : values) {
      if (builder.length() > 1) {
        builder.append(", ");
      }
      builder.append(value);
    }
    builder.append(">");
    return builder.toString();
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return Arrays.hashCode(values);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object object) {
    if (object == null || !(object instanceof Permutation)) {
      return false;
    }

    final Permutation that = (Permutation) object;
    return Arrays.equals(this.values, that.values);
  } 

  /**
   * Generates single permutation individual.
   * @param generator Random number generator.
   * @param permutationSize Size of the generated individual.
   * @return Randomly generated permutation individual.
   */
  public static Permutation generate(
      final WevoRandom generator,
      final int permutationSize) {
    List<Integer> genesPickPool = new LinkedList<Integer>();
    for (int j = 0; j < permutationSize; j++) {
      genesPickPool.add(j);
    }

    int[] chromosome = new int[permutationSize];
    for (int position = 0; position < permutationSize; position++) {
      final int pickedElement =
        generator.nextInt(0, genesPickPool.size());

      chromosome[position] = genesPickPool.get(pickedElement);
      genesPickPool.remove(pickedElement);
    }
    return new Permutation(chromosome);
  }

  /**
   * Generates initial population with random permutation individuals.
   * @param generator Random number generator.
   * @param individualSize Size of each individual in the generated population.
   * @param individuals Number of individuals in the population.
   * @return Population of permutation individuals.
   */
  public static Population<Permutation> generatePopulationOfRandomIndividuals(
      final WevoRandom generator,
      final int individualSize,
      final int individuals) {
    List<Permutation> result = new LinkedList<Permutation>();
    for (int i = 0; i < individuals; i++) {
      result.add(Permutation.generate(generator, individualSize));
    } 

    return new Population<Permutation>(result);
  }
}
