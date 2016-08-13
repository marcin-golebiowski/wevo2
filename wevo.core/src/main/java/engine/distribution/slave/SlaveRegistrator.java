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
package engine.distribution.slave;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import engine.distribution.master.MasterSlaveEvaluator;
import engine.distribution.master.servlets.DistributionServlet;
import engine.distribution.utils.WevoURL;

/**
 * Takes care of registering slave in the distribution.
 * 
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class SlaveRegistrator {

  /** URL address of the registration servlet. */
  private final WevoURL registrationUrl;

  /**
   * Main TaskExchanger constructor. Package visibility
   * for testing purposes only.
   * @param registrationUrl Registration URL.
   */
  SlaveRegistrator(final WevoURL registrationUrl) {
    this.registrationUrl = registrationUrl;
  }

  /**
   * Connector constructor for public use.
   * @param serverUrl URL address of the master. Must not be null.
   * @throws MalformedURLException Thrown when unable to create necessary URLs.
   */
  public SlaveRegistrator(final String serverUrl)
      throws MalformedURLException {
    this(new WevoURL(serverUrl + MasterSlaveEvaluator.REGISTER_ADDRESS));
  }

  /**
   * Registers this slave in master's slave manager.
   * As a result of request a newly allocated slave id
   * is obtained.
   * @param slaveName Name of slave under which it would be registered.
   * @return This slave id allocated by master. 
   * @throws IOException Thrown on connection issues.
   * @throws ClassNotFoundException Thrown on reading response issues.
   */
  public String register(final String slaveName) 
      throws IOException, ClassNotFoundException {

    HttpURLConnection connection = registrationUrl.openConnection();

    connection.setDoOutput(true);
    connection.setUseCaches(false);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type",
        "application/octet-stream");

    connection.setRequestProperty(
        DistributionServlet.SLAVE_ID_PROPERTY,
        slaveName);

    connection.connect();
    connection.getResponseMessage();

    String slaveId = connection.getHeaderField(
        DistributionServlet.SLAVE_ID_PROPERTY);

    connection.disconnect();

    return slaveId;
  }
}
