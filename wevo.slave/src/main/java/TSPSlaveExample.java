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
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import samples.objectivefunctions.EuclideanTSP;
import engine.CachedObjectiveFunction;
import engine.MultiThreadedEvaluator;
import engine.distribution.slave.Slave;
import engine.individuals.Permutation;
import engine.utils.ListUtils;

/**
 * Example of a slave in the master-slave distribution model.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class TSPSlaveExample {

  /** Logger. */
  private final Logger logger =
      Logger.getLogger(TSPSlaveExample.class.getCanonicalName());

  /** Size of the cache. */
  @Option(name = "-cs", aliases = { "cacheSize" }, usage = "Size of the cache "
      + "for evaluation results. Should be bigger than population size.")
  // MagicNumber off
  private int cacheSize = 2000;
  // MagicNumber on

  /** Cycles for the slave to register. */
  @Option(name = "-rc", aliases = { "--registerCycles" }, usage =
      "Before each slave can start working, it has to "
      + "register. This number indicates how many times slave should try "
      + "to register, in case any registration failure occurs. Negative value "
      + "indicates infinite number of cycles, which is the default. "
      + "Can be combined with timeout mechanism, in which case slave stops when"
      + " either of the criteria (timeout expired or exceeded number of trials)"
      + " was met.")
  private int registerCycles = -1;

  /** Number of milliseconds to backoff on failures. */
  @Option(name = "-b", aliases = { "--backoff" }, usage =
      "Number of milliseconds for slave to seize "
      + "before asking master for population after rejection to avoid flooding."
      + " Defaults to 500ms.")
  // MagicNumber off
  private int backoff = 500;
  // MagicNumber on

  /** Connection timeout. */
  @Option(name = "-t", aliases = { "--timeout" }, usage =
      "When there is a connection failure, a timeout "
      + "mechanism is triggered. When the time (in seconds) specified here "
      + "passes, slave seizes trying to connect and stops. Defaults to 300 "
      + "seconds. Passing negative value disables timing-out.")
  // MagicNumber off
  private int timeout = 300;
  // MagicNumber on

  /** Name of this slave unit. */
  @Option(name = "-n", aliases = { "--name" }, usage =
      "Name of this slave unit. Defaults to \"slave\".")
  private String slaveName = "slave";

  /** URL of the evolution server. */
  @Option(name = "-u", aliases = { "--url" }, usage =
      "URL address of the evolution server. "
      + "Defaults to http://127.0.0.1:8000")
  private String url = "http://127.0.0.1:8000";


  /**
   * Main program entry.
   * @param args List of command line arguments:
   * <ul>
   * <li>URL of the server.</li>
   * <li>Number of runningCycles slave should execute. Negative value
   * means infinite number of runningCycles.
   * </ul>
   */
  public static void main(final String[] args) {
    for (Handler handler : Logger.getLogger("").getHandlers()) {
      handler.setLevel(Level.ALL);
    }
    Logger.getLogger("").setLevel(Level.ALL);

    new TSPSlaveExample().doMain(args);
  }

  /**
   * Main slave routine.
   * @param args List of command line arguments:
   * <ul>
   * <li>URL of the server.</li>
   * <li>Number of runningCycles slave should execute. Negative value
   * means infinite number of runningCycles.
   * </ul>
   */
  public void doMain(final String[] args)  {
    CmdLineParser parser = new CmdLineParser(this);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      parser.printUsage(System.err);
      System.exit(1);
    }

    try {
      // MagicNumber off
      CachedObjectiveFunction<Permutation> objectiveFunctionWrapper =
          new CachedObjectiveFunction<Permutation>(
              new EuclideanTSP(),
              cacheSize);
      // MagicNumber on

      Slave<Permutation> slave = Slave.<Permutation>createSlave(
          new MultiThreadedEvaluator<Permutation>(
              buildObjectiveFunctions(objectiveFunctionWrapper)),
          slaveName,
          url);

      slave.register(registerCycles, backoff, timeout);
      slave.run(backoff, timeout);
    } catch (MalformedURLException exception) {
      logger.log(Level.SEVERE, "Given URL address was malformed.", exception);
    } catch (ClassNotFoundException exception) {
      logger.log(Level.SEVERE, "Class definition missing.", exception);
    }
  }

  /**
   * Creates a list of objective functions (one in this case) to be optimized.
   * @param objectiveFunctionWrapper A function that the list is based on.
   * @return List of objective functions.
   */
  @SuppressWarnings("unchecked")
  private static List<CachedObjectiveFunction<Permutation>>
      buildObjectiveFunctions(
          CachedObjectiveFunction<Permutation> objectiveFunctionWrapper) {
    return ListUtils.buildList(objectiveFunctionWrapper);
  }
}
