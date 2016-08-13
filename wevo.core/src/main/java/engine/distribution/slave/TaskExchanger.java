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
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import engine.distribution.master.MasterSlaveEvaluator;
import engine.distribution.master.servlets.DistributionServlet;
import engine.distribution.serialization.EvaluationResult;
import engine.distribution.serialization.EvaluationTask;
import engine.distribution.serialization.ResultSerializer;
import engine.distribution.serialization.TaskSerializer;
import engine.distribution.utils.WevoURL;

/**
 * Takes care of exchanging populations with evolution master.
 *
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 * @param <T> Type of individuals evaluated on the server.
 */
public class TaskExchanger<T> {

  /** Logging utility. */
  private final Logger logger = Logger.getLogger(
      TaskExchanger.class.getCanonicalName());

  /** Utility used for deserialization of task. */
  private final TaskSerializer<T> taskSerializer;

  /** URL address of the exchange servlet. */
  private final WevoURL exchangeUrl;

  /** Utility used for serialization of the evaluation result. */
  private ResultSerializer<T> resultSerializer;

  /**
   * Main TaskExchanger constructor. Package visibility
   * for testing purposes only.
   * @param exchangeUrl URL address for exchanging individuals.
   * @param taskSerializer Task serializer.
   * @param resultSerializer Result serializer.
   */
  TaskExchanger(
      final TaskSerializer<T> taskSerializer,
      final ResultSerializer<T> resultSerializer,
      final WevoURL exchangeUrl) {
    this.taskSerializer = taskSerializer;
    this.resultSerializer = resultSerializer;
    this.exchangeUrl = exchangeUrl;
  }

  /**
   * Connector constructor for public use.
   * @param serverUrl URL address of the master. Must not be null.
   * @param taskSerializer Serializer used for task deserialization.
   * @param resultSerializer Serializer used for result serialization.
   * @throws MalformedURLException Thrown when unable to create necessary URLs.
   */
  public TaskExchanger(
      final String serverUrl,
      final TaskSerializer<T> taskSerializer,
      final ResultSerializer<T> resultSerializer) throws MalformedURLException {
    this(taskSerializer, resultSerializer,
        new WevoURL(serverUrl + MasterSlaveEvaluator.EXCHANGE_ADDRESS));
  }

  /**
   * Retrieves population to evaluate from the master.
   * @param slaveId ID of the slave to get population for.
   * @param backoffMillis Milliseconds the slave should seize
   * to ask for population (to avoid server flooding).
   * @return Population retrieved from the server. Never null.
   * @throws IOException Thrown on deserialization or connection issues.
   * @throws ClassNotFoundException Thrown when definition for object found
   * in the stream does not exist.
   */
  public EvaluationTask<T> getTask(
      final String slaveId,
      int backoffMillis)
      throws IOException, ClassNotFoundException {

    logger.log(Level.INFO, "Retrieving population to evaluate from master");

    EvaluationTask<T> taskToEvaluate = null;
    while (taskToEvaluate == null) {

      logger.log(Level.FINE, "Connecting to master ("
          + exchangeUrl.toString() + ")");

      HttpURLConnection connection = exchangeUrl.openConnection();

      connection.setDoInput(true);
      connection.setUseCaches(false);
      connection.setRequestMethod("GET");
      connection.setRequestProperty(
          DistributionServlet.SLAVE_ID_PROPERTY,
          slaveId);

      connection.connect();

      logger.log(Level.FINE, "Getting the response for population request");

      switch (connection.getResponseCode()) {
        case HttpURLConnection.HTTP_OK:
          taskToEvaluate = retrieveTask(connection);
          break;

        case HttpURLConnection.HTTP_NO_CONTENT:
          waitForTask(backoffMillis);
          break;

        default:
          stopTrying(connection);
      }

      connection.disconnect();
      logger.log(Level.FINE, "Population retrieved successfully.");
    }

    return taskToEvaluate;
  }

  /**
   * Stops trying to retrieve population anymore.
   * @param connection Connection with the server.
   * @throws IOException Always thrown.
   */
  private void stopTrying(HttpURLConnection connection) throws IOException {

    logger.log(Level.WARNING, "Request refused. Server returned error code "
        + connection.getResponseCode());

    throw new ConnectException("Connection was not established. "
        + "Server responded with error code "
        + connection.getResponseCode()
        + " with following message: "
        + connection.getResponseMessage());
  }

  /**
   * Stops trying to retrieve population from master for a while.
   * @param backoffMillis Number of milliseconds to wait.
   */
  private void waitForTask(int backoffMillis) {
    logger.log(Level.INFO, "Request accepted, no population available. "
        + "Sleeping...");

    try {
      // Regexp off
      Thread.sleep(backoffMillis);
      // Regexp on
    } catch (InterruptedException exception) {
      // We can swallow this exception as interruption does no harm.
      logger.log(Level.WARNING, "Population exchanger was interrupted",
          exception);
    }

    logger.log(Level.INFO, "Awaken. Retrying...");
  }

  /**
   * Retrieves population from the master.
   * @param connection Connection to the server.
   * @return Population to evaluate.
   * @throws IOException Thrown on connection errors.
   * @throws ClassNotFoundException Thrown when no appropriate population
   * definition is available.
   */
  private EvaluationTask<T> retrieveTask(HttpURLConnection connection)
      throws IOException, ClassNotFoundException {
    EvaluationTask<T> evaluationTask;

    logger.log(Level.FINE, "Request accepted, deserializing population");

    evaluationTask = taskSerializer.deserialize(
        connection.getInputStream());

    logger.log(Level.FINER, "Population deserialized.");

    return evaluationTask;
  }

  /**
   * Sends population to the server.
   * @param result Result to be sent to the server.
   * @param slaveId ID of the slave sending population.
   * @throws IOException Thrown on connection issues.
   */
  public void sendResult(
      final EvaluationResult<T> result,
      final String slaveId) throws IOException {

    logger.log(Level.INFO, "Sending evaluation result to the master");

    HttpURLConnection connection =
        (HttpURLConnection) exchangeUrl.openConnection();

    connection.setDoOutput(true);
    connection.setUseCaches(false);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type",
        "application/octet-stream");
    connection.setRequestProperty(
        DistributionServlet.SLAVE_ID_PROPERTY,
        slaveId);

    connection.connect();

    resultSerializer.serialize(connection.getOutputStream(), result);

    connection.getResponseMessage();
    connection.disconnect();

    logger.log(Level.FINEST, "Response (" 
        + connection.getResponseCode() + "): " 
        + connection.getResponseMessage());
    logger.log(Level.INFO, "Evaluation result sent to the master");
  }
}
