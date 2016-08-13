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
package engine.distribution.master.servlets;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;

import engine.distribution.master.SlaveIdGenerator;
import engine.distribution.master.SlaveInformation;
import engine.distribution.master.SlaveManager;
import engine.distribution.master.StatisticsManager;

/**
 * Servlet responsible for registering slaves.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class RegistrationServlet extends HttpServlet {

  /** Default slave name in case slave using empty name. */
  public static final String DEFAULT_SLAVE_NAME = "slave";

  /** Generated serial version UID. */
  private static final long serialVersionUID = 2391325022791651850L;

  /** Logger. */
  private final Logger logger =
      Logger.getLogger(DistributionServlet.class.getCanonicalName());

  /** SlaveManager object for registering new slave. */
  private SlaveManager slaveManager;

  /** StatisticsManager object for registering new slave. */
  private StatisticsManager statisticsManager;

  /**
   * Constructor.
   * @param slaveManager Slave's managing tool.
   * @param statisticsManager Slave's stats managing tool.
   */
  public RegistrationServlet(
        SlaveManager slaveManager,
        StatisticsManager statisticsManager) {
    this.slaveManager = slaveManager;
    this.statisticsManager = statisticsManager;
  }

  /** {@inheritDoc} */
  @Override
  public void doPost(
      final HttpServletRequest request,
      final HttpServletResponse response) {

    logger.log(Level.FINE, "Handling POST request from "
        + request.getRemoteHost());

    validateRequest(request);

    String slaveId = registerSlave(
        getSlaveName(request));

    finalizeResponse(request, response,
        slaveId);

    logger.log(Level.FINEST, "POST request from "
        + request.getRemoteHost() + " handled: "
        + request.toString());
  }

  /**
   * Sets return codes and other parameters to finalize
   * the response. 
   * @param request Request related to the response.
   * @param response Response being finalized.
   * @param slaveId ID assigned to the slave.
   */
  private void finalizeResponse(
      final HttpServletRequest request,
      final HttpServletResponse response,
      final String slaveId) {

    response.setHeader("pragma", "no-cache");
    response.setContentType("application/octet-stream");
    response.setStatus(HttpServletResponse.SC_ACCEPTED);
    response.setHeader(
        DistributionServlet.SLAVE_ID_PROPERTY,
        slaveId);
    ((Request) request).setHandled(true);

    logger.log(Level.FINEST, "Response to " + request.getRemoteHost()
        + " finalized");
  }

  /**
   * Registers slave in the slave registry.
   * @param slaveName Name of the slave to register.
   * @return ID assigned to the slave during registration.
   */
  private String registerSlave(String slaveName) {
    String slaveId = SlaveIdGenerator.generateId(slaveName);
    SlaveInformation slaveInfo = new SlaveInformation();

    slaveManager.addSlave(slaveId, slaveInfo);
    statisticsManager.addSlave(slaveId);

    logger.log(Level.FINE, "Slave " + slaveName
        + " assigned with slave ID: " + slaveId.toString());

    return slaveId;
  }

  /**
   * Validates the POST request.
   * @param request Request to be validated.
   */
  private void validateRequest(final HttpServletRequest request) {
    final String contentType = request.getContentType();

    if (contentType == null
        || !contentType.equals("application/octet-stream")) {
      throw new IllegalArgumentException("Request has invalid "
          + "Content-Type. Only application/octet-stream is "
          + "accepted by this servlet.");
    }
  }

  /**
   * Validates slave name retrieved from the request.
   * @param request Request that the slave name was retrieved from.
   * @return generated name for the slave in case of unsuccessful validation.
   */
  private String getSlaveName(final HttpServletRequest request) {

    final String suggestedSlaveName = request.getHeader(
        DistributionServlet.SLAVE_ID_PROPERTY);

    String assignedSlaveName = suggestedSlaveName;
    if (suggestedSlaveName == null || suggestedSlaveName.equals("")) {
      assignedSlaveName = DEFAULT_SLAVE_NAME;
      logger.log(Level.INFO, "Invalid slave name supplied. "
          + "Using default slave name: " + DEFAULT_SLAVE_NAME);
    }

    logger.log(Level.INFO, "Host " + request.getRemoteHost()
        + " (suggested name: " + suggestedSlaveName
        + ") assigned with name " + assignedSlaveName);

    return assignedSlaveName;
  }
}
