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
package engine.operators.permutation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import engine.Operator;
import engine.Population;
import engine.individuals.Permutation;
import engine.utils.WevoRandom;

/**
 * PMX crossover. A well-known crossover operator. It creates children by
 * exchanging one segment between parents and repairs the genes in children, so
 * that there are no duplicate genes. If it finds a duplicate, it select a gene,
 * which is on the same position, from the second child.
 * 
 * Example: Beginning of the exchange segment is 3 (indexing from 0). 
 * Length of the exchange segment is 3.
 * First parent's chromosome is {1,2,3,4,5,6,7,8,9}.
 * Second parent's chromosome is {4,1,2,8,7,6,9,3,5}.
 * Then children have chromosomes: 
 * First child's chromosome is {1,2,3,8,7,6,5,4,9}
 * Second child's chromosome is {8,1,2,4,5,6,9,3,7}
 *
 * @see <a href=" http://books.google.com/books?id=wonrLjj2GagC">
   Introduction to Genetic Algorithms: with 193 figures and 13 tables" </a><br>
 * S. N. Sivanandam, S. N. Deepa <br>
 * Springer, 2007 <br>
 * ISBN  354073189X, 9783540731894<br>
 *
 * @author Szymek Fogiel (szymek.fogiel@gmail.com)
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 */
public class PMXCrossover implements Operator<Permutation> {

  /** Beginning of the segment. */
  private int segmentBeginning;

  /** Length of the segment. */
  private int segmentLength;

  /** Random number generator. */
  private WevoRandom randomGenerator;

  /** Indicates whether operator should generate segment bounds randomly. */
  private boolean generateSegmentBounds;

  /**
   * Constructor.
   * 
   * @param generator Random number generator.
   * @param beginning the beginning of the exchanged segment.
   * @param length the length of the exchanged segment.
   */
  public PMXCrossover(
      final WevoRandom generator,
      final int beginning,
      final int length) {
    this.randomGenerator = generator;
    this.segmentBeginning = beginning;
    this.segmentLength = length;
    this.generateSegmentBounds = false;
  }

  /**
   * Constructor, that causes the operator to generate segment beginning
   * and length randomly.
   * 
   * @param generator Random number generator.
   */
  public PMXCrossover(
      final WevoRandom generator) {
    this.randomGenerator = generator;
    this.generateSegmentBounds = true;
  }


  /** {@inheritDoc} */
  public Population<Permutation> apply(Population<Permutation> population) {
    if (generateSegmentBounds) {
      final int chromosomeLength =
          population.getIndividuals().get(0).getSize();
      segmentBeginning = randomGenerator.nextInt(
          0, chromosomeLength);
      segmentLength = randomGenerator.nextInt(
          0, chromosomeLength - segmentBeginning);
    }

    Population<Permutation> workingCopy =
        new Population<Permutation>(population);

    Collections.shuffle(workingCopy.getIndividuals(),
        randomGenerator.getInnerGenerator());

    Population<Permutation> result = new Population<Permutation>();

    for (int i = 0; i < workingCopy.getIndividuals().size() / 2; i++) {
      List<Permutation> children = combine(
          workingCopy.getIndividuals().get(2 * i),
          workingCopy.getIndividuals().get(2 * i + 1));
      result.addIndividual(children.get(0));
      result.addIndividual(children.get(1));
    }

    return result;
  }

  /**
   * Combines two parents in a PMX way. Package-visibility for testing.
   * @param parent1 First parent.
   * @param parent2 Second parent.
   * @return List of two children obtained from parents combination.
   */
  List<Permutation> combine(
      final Permutation parent1,
      final Permutation parent2) {

    List<Permutation> result =
        new ArrayList<Permutation>(2);

    final int chromosomeLength = parent1.getValues().length;

    int[] child1Chromosome = new int[chromosomeLength];
    int[] child2Chromosome = new int[chromosomeLength];

    // Copying the exchanged segments.
    copySegment(child1Chromosome, parent2);
    copySegment(child2Chromosome, parent1);

    // Defining the rest of the genes.
    exchangeGenes(child1Chromosome, child2Chromosome, parent1);
    exchangeGenes(child2Chromosome, child1Chromosome, parent2);

    Permutation child1 = new Permutation(child1Chromosome);
    Permutation child2 = new Permutation(child2Chromosome);

    result.add(child1);
    result.add(child2);

    return result;
  }

  /**
   * Copying a segment to a child's chromosome from a parent's chromosome. The
   * copied segment is [from,to).
   * 
   * @param childChromosome The child's chromosome to which segment is copied.
   * @param parent The individual from which the segment is copied.
   */
  private void copySegment(
      final int[] childChromosome,
      final Permutation parent) {

    for (int i = segmentBeginning; i < segmentBeginning + segmentLength; i++) {
      childChromosome[i] = parent.getValue(i);
    }
  }

  /**
   * Checks if gene already exists in chromosome in segment defined by
   * segment_beginning and segment_length. If in finds a conflict it returns
   * it's position, else it return -1.
   * 
   * @param gene the gene which we check if already exists in the segment
   * @param chromosome the chromosome in which we search for a conflict
   * @return Index conflicting or -1, if none exists.
   */
  private int isConflict(
      final int gene,
      final int[] chromosome) {

    for (int j = segmentBeginning; j < segmentBeginning + segmentLength; j++) {
      if (gene == chromosome[j]) {
        return j;
      }
    }

    return -1;
  }

  /**
   * Copies genes from the parent's to child's chromosome without affecting
   * genes in segment defined by segment beginning and segment length.
   * 
   * @param affectedChildChromosome the chromosome to which genes are copied
   * @param unaffectedChildChromosome the chromosome from which genes are
   *        copied if affected_child_chromosome already contains a gene which is
   *        attempted to be copied
   * @param parent the individual from whose chromosome genes are copied
   */
  private void exchangeGenes(
      final int[] affectedChildChromosome,
      final int[] unaffectedChildChromosome,
      final Permutation parent) {

    int chromosomeLength = affectedChildChromosome.length;
    int currentGene; // currently selected gene

    // position on which conflict occurs or -1 if there is no conflict.
    int conflictPosition;
    for (int i = 0; i < chromosomeLength; i++) {
      currentGene = parent.getValue(i);
      // Checking if conflict occurs
      conflictPosition = 
          isConflict(currentGene, affectedChildChromosome);

      // If conflict occurs, then select the gene corresponding 
      // to the current one; otherwise, select current one.
      while (conflictPosition != -1) {
        currentGene = unaffectedChildChromosome[conflictPosition];
        conflictPosition =
            isConflict(currentGene, affectedChildChromosome);
      }
      affectedChildChromosome[i] = currentGene;

      // When we get to the end of the chromosome, jump to the beginning.
      if (i == segmentBeginning - 1) {
        i += segmentLength;
      }
    }
  }
}
