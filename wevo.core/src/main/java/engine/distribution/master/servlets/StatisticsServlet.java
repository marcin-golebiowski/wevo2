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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import engine.distribution.master.StatisticsManager;
import engine.distribution.master.statistics.IterationStatistics;
import engine.distribution.master.statistics.StatisticsHtmlPrinter;

/**
 * Servlet responsible for sending html site which presents statistics
 * of computation.
 * TODO(anglart.michal): decorate with loggers.
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class StatisticsServlet extends HttpServlet {

    /** Url parameter: iterations. */
    public static final String ITERATIONS_PROPERTY = "iterations";

    /** Url parameter: slaves. */
    public static final String SLAVES_PROPERTY = "slaves";

    /** Url parameter: settings. */
    public static final String SETTINGS_PROPERTY = "settings";

    /** Url parameter: iteration. */
    public static final String ITERATION_PROPERTY = "iteration";

    /** Url parameter: length. */
    public static final String LENGTH_PROPERTY = "length";

    /** Url parameter: last. */
    public static final String LAST_PROPERTY = "last";

    /** Generated serial version UID. */
    private static final long serialVersionUID = -4153799436989888385L;

    /** Tool used to track history of evaluation. */
    private StatisticsManager statisticsManager;

    /** Tool used to generate statistics pages. */
    private final StatisticsHtmlPrinter htmlPrinter;

    /** Logger. */
    private final Logger logger =
        Logger.getLogger(StatisticsServlet.class.getCanonicalName());


    /**
     * Constructor. Package-visible.
     * @param statisticsManager Statistics manager.
     * @param htmlPrinter Html sites printer.
     */
    StatisticsServlet(final StatisticsManager statisticsManager,
          final StatisticsHtmlPrinter htmlPrinter) {
      this.statisticsManager = statisticsManager;
      this.htmlPrinter = htmlPrinter;
    }

    /**
     * Constructor.
     * @param statisticsManager Statistics manager.
     */
    public StatisticsServlet(final StatisticsManager statisticsManager) {
      this(statisticsManager, new StatisticsHtmlPrinter());
    }

    /** {@inheritDoc} */
    @Override
    public void doGet(
        final HttpServletRequest request,
        final HttpServletResponse response) {

      response.setHeader("pragma", "no-cache");
      response.setContentType("text/html");
      response.setStatus(HttpServletResponse.SC_OK);

      try {
        ServletOutputStream output = response.getOutputStream();
        // TODO(anglart.michal): validate parameters in order
        // to avoid setting multiple properties like: 
        // ?settings=show&slaves=show etc.
        String iterations = request.getParameter(ITERATIONS_PROPERTY);
        String slaves = request.getParameter(SLAVES_PROPERTY);
        String settings = request.getParameter(SETTINGS_PROPERTY);
        String iteration = request.getParameter(ITERATION_PROPERTY);

        if (iterations != null && iterations.equals("show")) {
            String last = request.getParameter(LAST_PROPERTY);
            writeIterationsPage(output, last);
        } else if (iteration != null) {
            writeIterationDetailPage(output, Long.parseLong(iteration));
        } else if (slaves != null && slaves.equals("show")) {
            writeSlavesPage(output);
        } else if (settings != null && settings.equals("show")) {
            writeSettingsPage(output);
        } else {
            writeMainPage(output);
        }
      } catch (IOException e) {
        logger.log(Level.WARNING, "Error occurred while handling "
            + "GET request from (" + request.getRemoteHost() 
            + ") in statistics servlet.", e);
      }
    }

    /**
     * Writes iterations page to given servlet output stream.
     * @param output Stream for writing page.
     * @param choosenIteration Iteration specified in parameter. 
     * @throws IOException Thrown on output errors.
     */
    public void writeIterationDetailPage(ServletOutputStream output,
        long choosenIteration) throws IOException {

        String startTime = statisticsManager.getStartTimeString();
        double totalTime = statisticsManager.getRunningTime();
        long iteration = statisticsManager.getIterationCounter();

        IterationStatistics stats = 
            statisticsManager.getSingleIteration(choosenIteration);

        String iterationPage =
            htmlPrinter.generateIterationSite(startTime, totalTime,
                iteration, choosenIteration, stats);

        output.println(iterationPage);
    }

    /**
     * Writes iterations page to given servlet output stream.
     * @param output Stream for writing page.
     * @param lastIteration Number of newest iteration to show in table. 
     * @throws IOException Thrown on output errors.
     */
    public void writeIterationsPage(ServletOutputStream output,
          String lastIteration) throws IOException {
      String startTime = statisticsManager.getStartTimeString();
      double totalTime = statisticsManager.getRunningTime();
      long iteration = statisticsManager.getIterationCounter();

      long lastIterVal =
          lastIteration == null ? iteration - 1 : Long.parseLong(lastIteration);
      lastIterVal = Math.max(lastIterVal, 0);
      lastIterVal = Math.min(iteration - 1, lastIterVal);

      List<IterationStatistics> iterations = 
          statisticsManager.getIterationsStatistics(lastIterVal);
      String iterationsPage = 
          htmlPrinter.generateIterationsSite(startTime, totalTime, 
              iteration, iterations);

      output.println(iterationsPage);
    }

    /**
     * Writes slaves page to given servlet output stream.
     * @param output Stream for writing page.
     * @throws IOException Thrown on output errors.
     */
    public void writeSlavesPage(ServletOutputStream output) throws IOException {
      String startTime = statisticsManager.getStartTimeString();
      double totalTime = statisticsManager.getRunningTime();
      long iteration = statisticsManager.getIterationCounter();
      List<String> slaves = statisticsManager.getSlaves();
      String slavesPage = 
          htmlPrinter.generateSlavesSite(startTime, 
              totalTime, iteration, slaves);
      output.println(slavesPage);
    }

    /**
     * Writes settings page to given servlet output stream.
     * @param output Stream for writing page.
     * @throws IOException Thrown on output errors.
     */
    public void writeSettingsPage(ServletOutputStream output) 
        throws IOException {
      String startTime = statisticsManager.getStartTimeString();
      double totalTime = statisticsManager.getRunningTime();
      long iteration = statisticsManager.getIterationCounter();
      String settingsPage = 
          htmlPrinter.generateSettingsSite(startTime, totalTime, iteration);
      output.println(settingsPage);
    }

    /**
     * Writes main page to given servlet output stream.
     * @param output Stream for writing page.
     * @throws IOException Thrown on output errors.
     */
    private void writeMainPage(ServletOutputStream output) throws IOException {
      String startTime = statisticsManager.getStartTimeString();
      double totalTime = statisticsManager.getRunningTime();
      long iterationNumber = statisticsManager.getIterationCounter();
      String mainPage = 
          htmlPrinter.generateMainSite(startTime, totalTime, iterationNumber);
      output.println(mainPage);
    }
}
