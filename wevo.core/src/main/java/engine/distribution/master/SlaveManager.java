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
package engine.distribution.master;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import engine.utils.SystemClock;
import engine.utils.WevoClock;

/**
 * Utility responsible for handling registration of slaves
 * during evolution and keeping count of slaves that
 * are available in any moment in time.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class SlaveManager {

  /** Logging utility. */
  private final Logger logger = Logger.getLogger(
      SlaveManager.class.getCanonicalName());

  /** List of all registered slaves. */
  private final LinkedHashMap<String, SlaveInformation> slaves;

  /** Signal for collecting slaves. */
  private CountDownLatch slavesToCollect;

  /** Timeout for slaves. */
  private long slaveTimeout;

  /** Time measurement utility. */
  private WevoClock clock;

  /**
   * Constructor.
   * Package visibility for testing purposes.
   * @param slaves Mapping from slave's id to slave's information.
   * @param slavesToCollect Number of slaves to be collected guarded by a latch.
   * @param newSlaveTimeout Timeout for slaves.
   * @param newClock Time measurement utility. Must not be null.
   */
  SlaveManager(
      final Map<String, SlaveInformation> slaves,
      final CountDownLatch slavesToCollect,
      final long newSlaveTimeout,
      final WevoClock newClock) {
    this.slaves = new LinkedHashMap<String, SlaveInformation>(slaves);
    this.slavesToCollect = slavesToCollect;
    this.slaveTimeout = newSlaveTimeout;
    this.clock = newClock;
  }

  /**
   * Constructor.
   * @param minimumSlaves Minimum number of slaves to run the distribution.
   * @param newSlaveTimeout Timeout for slaves.
   */
  public SlaveManager(final int minimumSlaves, final long newSlaveTimeout) {
    this(new LinkedHashMap<String, SlaveInformation>(),
        new CountDownLatch(minimumSlaves),
        newSlaveTimeout,
        new SystemClock());
  }

  /**
   * Constructor.
   * @param minimumSlaves Minimum number of slaves to run the distribution.
   * @param newSlaveTimeout Timeout for slaves.
   * @param newClock Clock utility for time measurements. Must not be null.
   */
  public SlaveManager(
      final int minimumSlaves, 
      final long newSlaveTimeout,
      final WevoClock newClock) {
    this(new LinkedHashMap<String, SlaveInformation>(),
        new CountDownLatch(minimumSlaves),
        newSlaveTimeout,
        newClock);
  }

  /** Constructor initially disabling slave timeout. */
  public SlaveManager() {
    this(1, -1);
  }

  /**
   * Adds new slave to register.
   * @param slaveId Slave identifier
   * @param slaveInfo Slave information's 
   */
  public synchronized void addSlave(String slaveId, 
      SlaveInformation slaveInfo) {
    logger.log(Level.INFO, "Adding slave " + slaveId);
    slaves.put(slaveId, slaveInfo);
    slavesToCollect.countDown();
  }

  /**
   * Removes slave from register.
   * @param slaveId Slave identifier.
   */
  public synchronized void removeSlave(String slaveId) {
    logger.log(Level.INFO, "Removing slave " + slaveId);
    slaves.remove(slaveId);
  }

  /**
   * Returns list of slaves currently available. This is a blocking method.
   * @param minimum Minimum number of slaves to be collected.
   * @return Slave register object.
   */
  public List<String> getAvailableSlaves(final int minimum) {
    do {
        // Collect missing slaves.
        final int currentSlaves = slaves.size();
        final int missingSlaves = minimum - currentSlaves < 0 ? 0
            : minimum - currentSlaves;

        logger.log(Level.INFO, "Collecting " + minimum + " slaves. "
            + "(" + missingSlaves + " missing)");

        if (missingSlaves > 0) {
          collectRemainingSlaves(minimum - slaves.size());
        }

        logger.log(Level.FINE, "Minimum number (" + minimum 
            + ") of slaves collected");

        // Remove timed out slaves.
        final Set<String> keys = slaves.keySet();
        final long currentTime = clock.getCurrentTimeMillis();
        for (final String slaveId : keys) {
          if (isTimedOut(slaveId, currentTime)) {
            final long lastContact = slaves.get(slaveId)
                .getLastContactTimestamp();

            logger.log(Level.INFO, "Slave " + slaveId + " timed out.");
            logger.log(Level.INFO, "Last contact: "
                + lastContact + "\n" + "Current time: " + currentTime + "\n"
                + "Time difference: " + (currentTime - lastContact));
            removeSlave(slaveId);
          } 
        }
    } while (slaves.size() < minimum);

    logger.log(Level.INFO, "Slaves collected: " + slaves.keySet());
    return new ArrayList<String>(slaves.keySet());
  }

  /**
   * Indicates whether slave is timed out or not. Package-visibility for testing
   * purposes.
   * @param slaveId ID of the slave checked.
   * @param currentTime Time stamp to calculate time difference.
   * @return True if slave is timed out, false otherwise.
   */
  boolean isTimedOut(final String slaveId, final long currentTime) {
    SlaveInformation slaveInfo = slaves.get(slaveId);
    return slaveTimeout > 0
        ? currentTime - slaveInfo.getLastContactTimestamp() > slaveTimeout
        : false;
  }

  /**
   * Collects the given number of slaves (that is, waits for registration
   * of given number of slaves)..
   * @param minimum Minimum number of slaves to collect.
   */
  private void collectRemainingSlaves(final int minimum) {
    slavesToCollect = new CountDownLatch(minimum);

    try {
      logger.log(Level.INFO, "Waiting for " + minimum
          + " slaves to be collected");

      slavesToCollect.await();

      logger.log(Level.FINE, minimum + " slaves collected.");
    } catch (InterruptedException e) {
      logger.log(Level.WARNING, "Interrupted while collecting slaves", e);
      if (slavesToCollect.getCount() > 0) {
        throw new IllegalStateException("Unable to collect " + minimum
            + " number of slaves (collected: "
            + (minimum - slavesToCollect.getCount()) + ")");
      }
    }
  }

  /**
   * Updates last contact timestamp of given slave.
   * @param slaveId Slave identifier.
   */
  public synchronized void updateLastContactTimestamp(String slaveId) {
    SlaveInformation slaveInfo = slaves.get(slaveId);
    slaveInfo.updateLastContactTimestamp();
    logger.log(Level.FINE, "Last contact time stamp updated to "
        + slaveInfo.getLastContactTimestamp());
  }

  /**
   * Returns slave timeout.
   * @return slave timeout.
   */
  public long getSlaveTimeout() {
    return slaveTimeout;
  }

  /**
   * Sets new slave timeout.
   * @param newSlaveTimeout Timeout to set (in milliseconds).
   */
  public void setSlaveTimeout(long newSlaveTimeout) {
    this.slaveTimeout = newSlaveTimeout;
  }
}
