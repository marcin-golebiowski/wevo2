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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Request;

import engine.distribution.master.SlaveManager;
import engine.distribution.master.StatisticsManager;
import engine.distribution.master.TaskManager;
import engine.distribution.serialization.EvaluationResult;
import engine.distribution.serialization.EvaluationTask;
import engine.distribution.serialization.ResultSerializer;
import engine.distribution.serialization.TaskSerializer;

/**
 * Servlet responsible for exchanging populations between master and slaves.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * @param <T> Type of individuals being evaluated.
 */
@SuppressWarnings("serial")
public class DistributionServlet<T> extends HttpServlet {

  /** Request property name for exchaning slave ids. */
  public static final String SLAVE_ID_PROPERTY = "slaveIdProperty";

  /** Logger. */
  private final Logger logger =
      Logger.getLogger(RegistrationServlet.class.getCanonicalName());

  /** Serializer object for reading/writing tasks. */
  private final TaskSerializer<T> taskSerializer;

  /** Serializer object for reading/writing evaluation results. */
  private ResultSerializer<T> resultSerializer;

  /** Task manager object for distributing population. */
  private TaskManager<T> taskManager;

  /** SlaveManager object for slave's information. */
  private SlaveManager slaveManager;

  /** StatisticsManager object for slave's statistics data. */
  private StatisticsManager statisticsManager;

  /**
   * Constructor.
   * @param taskManager Tool for managing population.
   * @param resultSerializer Tool for managing slave's.
   * @param slaveManager Tool for managing slave's statistics.
   * @param statisticsManager Serializer tool.
   * @param taskSerializer Serializer tool.
   */
  public DistributionServlet(
      final TaskManager<T> taskManager,
      final SlaveManager slaveManager,
      final StatisticsManager statisticsManager,
      final TaskSerializer<T> taskSerializer,
      final ResultSerializer<T> resultSerializer) {
    this.taskManager = taskManager;
    this.slaveManager = slaveManager;
    this.statisticsManager = statisticsManager;
    this.taskSerializer = taskSerializer;
    this.resultSerializer = resultSerializer;
  }

  /**
   * Retrieves slave ID from the request.
   * @param request Request to retrieve from.
   * @return Slave ID or null if not present.
   */
  private String retrieveSlaveId(final HttpServletRequest request) {
    String slaveId = request.getHeader(SLAVE_ID_PROPERTY);

    logger.log(Level.FINE, "Handling request " + request.getMethod() + " "
        + "from slave " + slaveId + " (" + request.getRemoteHost() + ")");

    return slaveId;
  }

  /** {@inheritDoc} */
  @Override
  public void doGet(
      final HttpServletRequest request,
      final HttpServletResponse response) {

    String slaveId = retrieveSlaveId(request);

    try {
      if (taskManager.isDistributionEnabled()
          && taskManager.isTaskAvailableForSlave(slaveId)) {

        EvaluationTask<T> task = taskManager.getTaskForSlave(slaveId);
        writeTask(slaveId, request, response, task);

        finalizeGetResponse(slaveId, request, response,
            HttpServletResponse.SC_OK);
        statisticsManager.afterSendingTask(slaveId, task);
      } else {
        finalizeGetResponse(slaveId, request, response,
            HttpServletResponse.SC_NO_CONTENT);
      }

    } catch (IOException e) {
      logger.log(Level.WARNING, "Error occurred while handling "
          + "GET request from slave " + slaveId
          + " (" + request.getRemoteHost() + ")", e);
    }
  }

  /**
   * Serializes the given population and writes it to the response.
   * @param slaveId ID of the slave receiving the response.
   * @param request Request related to the response.
   * @param response Response to write to.
   * @param evaluationTask Task to be written to the response.
   * @throws IOException Thrown on IO errors.
   */
  private void writeTask(
      final String slaveId,
      final HttpServletRequest request,
      final HttpServletResponse response,
      EvaluationTask<T> evaluationTask) throws IOException {

    logger.log(Level.FINEST, "Writing evaluation task ("
        + evaluationTask.size() +  ") "
        + evaluationTask.toString() + "  to the response to "
        + slaveId + " (" + request.getRemoteHost() + ")");

    taskSerializer.serialize(response.getOutputStream(), evaluationTask);
    slaveManager.updateLastContactTimestamp(slaveId);
  }

  /**
   * Sets headers and prepares the response to be sent.
   * @param slaveId ID of the slave receiving the response.
   * @param request Request related to the response.
   * @param response Response to be finalized.
   * @param statusCode Status code to be set in the response.
   */
  private void finalizeGetResponse(
      final String slaveId,
      final HttpServletRequest request,
      final HttpServletResponse response,
      final int statusCode) {

    logger.log(Level.FINER, "Finalizing GET response to "
        + slaveId + " (" + request.getRemoteHost() + ")");

    response.setHeader("pragma", "no-cache");
    response.setContentType("application/octet-stream");
    response.setStatus(statusCode);
    ((Request) request).setHandled(true);

    logger.log(Level.FINEST, "GET response to " + slaveId
        + " (" + request.getRemoteHost() + ") "
        + "with status code " + statusCode + " finalized: "
        + response.toString());
  }

  /** {@inheritDoc} */
  @Override
  public void doPost(
      final HttpServletRequest request,
      final HttpServletResponse response) {

    String slaveId = retrieveSlaveId(request); 

    try {
      EvaluationResult<T> evaluationResult =
          readEvaluationResult(request);

      updateTaskForSlave(slaveId, evaluationResult, request);

      finalizePostResponse(slaveId, request, response,
          HttpServletResponse.SC_ACCEPTED);

      slaveManager.updateLastContactTimestamp(slaveId);
      statisticsManager.afterReceivingResult(slaveId, evaluationResult);
    } catch (final Exception e) {
      logger.log(Level.WARNING, "Error occurred while handling "
          + "GET request from slave " + slaveId
          + " (" + request.getRemoteHost() + ")", e);
    }
  }

  /**
   * Updates central (distributed) population with a population chunk.
   * @param slaveId ID of the slave sending population.
   * @param evaluationResult Chunk that updates the central population.
   * @param request Request containing the population.
   */
  private void updateTaskForSlave(
      final String slaveId,
      EvaluationResult<T> evaluationResult,
      HttpServletRequest request) {

    logger.log(Level.FINEST, "Slave " + slaveId
        + " (" + request.getRemoteHost() + "): "
        + "updated central population with evaluation result ("
        + evaluationResult.size() + ") :"
        + evaluationResult.toString());

    taskManager.updateTask(slaveId, evaluationResult);
  }

  /**
   * Reads population from the request.
   * @param request Request to read from.
   * @return Population found in the request.
   * @throws IOException Thrown on IO errors.
   * @throws ClassNotFoundException Thrown when population definition
   * was not found.
   */
  private EvaluationResult<T> readEvaluationResult(
        final HttpServletRequest request)
      throws IOException, ClassNotFoundException {
    EvaluationResult<T> result = 
        resultSerializer.deserialize(request.getInputStream());
    return result;
  }

  /**
   * Sets appropriate headers and prepares response for sending.
   * @param slaveId ID of the slave causing the update.
   * @param request Request related with the response.
   * @param response Response to be finalized.
   * @param statusCode Status code to be set to the response.
   */
  private void finalizePostResponse(
      final String slaveId,
      final HttpServletRequest request,
      final HttpServletResponse response,
      final int statusCode) {

    logger.log(Level.FINER, "Finalizing POST response to slave "
        + slaveId + " (" + request.getRemoteHost() + ")");

    response.setHeader("pragma", "no-cache");
    response.setContentType("application/octet-stream");
    response.setStatus(statusCode);
    ((Request) request).setHandled(true);

    logger.log(Level.FINEST, "POST response to slave "
        + slaveId + " (" + request.getRemoteHost() + ") finalized: "
        + response.toString());
  }
}
