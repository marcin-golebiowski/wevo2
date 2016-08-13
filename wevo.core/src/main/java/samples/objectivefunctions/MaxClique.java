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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import engine.ObjectiveFunction;
import engine.individuals.BinaryVector;

/**
 * Objective function checking whether given individual represents
 * a clique or not. In case it is not a clique, 0 is returned; otherwise,
 * number of nodes in the individual are returned.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
@SuppressWarnings("unchecked")
public class MaxClique implements ObjectiveFunction<BinaryVector> {

  /** Graph that we're looking for clique in. */
  private final List<List<Integer>> graph;

  /**
   * Public constructor, accepting graph that serves as a basis for
   * clique check.
   * @param graph Graph examined. Must not be null.
   */
  public MaxClique(final List<List<Integer>> graph) {
    this.graph = graph;
  }

  /** {@inheritDoc} */
  public double compute(final BinaryVector individual) {
    double result = 0.0;
    // Collect nodes in the potential clique.
    HashSet<Integer> nodes = new HashSet<Integer>();
    for (int i = 0; i < individual.getSize(); i++) {
      if (individual.getBit(i)) {
        nodes.add(i);
      }
    }

    // Check whether nodes form a clique.
    for (Integer node : nodes) {
      if (!otherNodesAreConnected(nodes, node)) {
        return 0;
    }

      result += 1.0;
    }
 
    return result;
  }

  /**
   * Check, for each node in the potential clique,
   * whether other nodes from the potential clique
   * are neighbours to given node (that is, check
   * whether given node is connected with other nodes
   * from given set of nodes).
   * @param nodes Set of nodes to check if they're connected to given node.
   * @param node Node that neighbours are checked.
   * @return True iff all of the nodes in <tt>nodes</tt> are connected
   * to <tt>node</tt>, false otherwise.
   */
  private boolean otherNodesAreConnected(
      final HashSet<Integer> nodes,
      final Integer node) {
    Set<Integer> otherNodes = (Set<Integer>) nodes.clone();
    otherNodes.remove(node);
    if (!graph.get(node).containsAll(otherNodes)) {
      return false;
    }

    return true;
  }
}

