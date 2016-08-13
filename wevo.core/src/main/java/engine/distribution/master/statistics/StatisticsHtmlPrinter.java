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
package engine.distribution.master.statistics;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Utility responsible for printing html code of
 * all stastics sites. 
 * @author Karol Stosiek (karol.stosiek@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class StatisticsHtmlPrinter {

    // TODO(anglart.michal): try to move css from template to separate file

    /** Value of html tables length. */
    public static final int DEFAULT_TABLE_LENGTH = 20;

    /** Points to directory where templates are stored. */
    public static final String TEMPLATE_DIRECTORY = "templates";

    /** Name of sites layout template. */
    public static final String LAYOUT_SITE = "layout.ftl";

    /** Name of main frame template. */
    public static final String MAIN_SITE = "main.ftl";

    /** Name of slaves frame template. */
    public static final String SLAVES_SITE = "slaves.ftl";

    /** Name of iterations frame template. */
    public static final String ITERATIONS_SITE = "iterations.ftl";

    /** Name of iterations frame template. */
    public static final String ITERATION_SITE = "iteration.ftl";

    /** Name of settings frame template. */
    public static final String SETTINGS_SITE = "settings.ftl";

    /** Name of error site template. */
    public static final String ERROR_SITE = "error.ftl";

    /** Name of frame site key. */
    private static final String FRAME = "frame";

    /** Name of slaves key. */
    private static final String SLAVES = "slaves";

    /** Name of running time key. */
    private static final String RUNNING_TIME = "runningTime";

    /** Name of current iteration key. */
    private static final String CURRENT_ITERATION = "currentIteration";

    /** Name of start time key. */
    private static final String START_TIME = "startTime";

    /** Name of start last in table key. */
    private static final String LAST_IN_TABLE = "lastInTable";

    /** Name of start first in table key. */
    private static final String FIRST_IN_TABLE = "firstInTable";

    /** Name of start iterations key. */
    private static final String ITERATIONS = "iterations";

    /** Name of start table length key. */
    private static final String TABLE_LENGTH = "tableLength";

    /** Name of start iteration details key. */
    private static final String ITERATION_DETAIL_NUMBER =
        "iterationDetailNumber";

    /** Name of start counters key. */
    private static final String COUNTERS = "counters";

    /** Name of error message key. */
    private static final String ERROR_MESSAGE = "errorMessage";

    /** Configuration object for html generation by templates. */
    private final Configuration configuration;

    /** Logger. */
    private final Logger logger =
        Logger.getLogger(StatisticsHtmlPrinter.class.getCanonicalName());

    /**
     * Package visibility constructor.
     * @param configuration Configuration object for template using. 
     */
    StatisticsHtmlPrinter(Configuration configuration) {
      this.configuration = configuration; 
    }

    /** Constructor. */
    public StatisticsHtmlPrinter() {
      this(new Configuration());
      configuration.setClassForTemplateLoading(getClass(), TEMPLATE_DIRECTORY);
      configuration.setObjectWrapper(new DefaultObjectWrapper());
    }

    /**
     * Generates final site by embedding code generated for frame
     * into layout site.
     * @param startTime Evaluation start time (as string).
     * @param runningTime Total evolution time in seconds.
     * @param currentIteration Current iteration number.
     * @param frameSiteCode Embedded code for frame.
     * @return Html code of site.
     * @throws IOException Thrown on IO errors during template site reading.
     * @throws TemplateException Thrown on internal template errors.
     */
    private String generateSite(String startTime, double runningTime,
        long currentIteration, String frameSiteCode) throws
          IOException, TemplateException {
      StringWriter stringWriter = new StringWriter();
      Template template = configuration.getTemplate(LAYOUT_SITE);
      HashMap<String, Object> rootMap = new HashMap<String, Object>();
      rootMap.put(START_TIME, startTime);
      rootMap.put(RUNNING_TIME, runningTime);
      rootMap.put(CURRENT_ITERATION, currentIteration);
      rootMap.put(FRAME, frameSiteCode);
      template.process(rootMap, stringWriter);
      stringWriter.flush();

      return stringWriter.toString();
    }

    /**
     * Generates string representing html code of 
     * main statistics site.
     * @param startTime Evaluation start time (as string).
     * @param runningTime Total evolution time in seconds.
     * @param currentIteration Current iteration number.
     * @return Html code of main site.
     */
    public String generateMainSite(String startTime, double runningTime,
          long currentIteration) {
      StringWriter stringWriter = new StringWriter();
      try {
        Template template = configuration.getTemplate(MAIN_SITE);
        HashMap<String, Object> rootMap = new HashMap<String, Object>();
        template.process(rootMap, stringWriter);
        stringWriter.flush();

        return generateSite(startTime, runningTime, currentIteration,
            stringWriter.toString());
      } catch (IOException e) {
        String errorMsg = "Error occured: cannot find template '"
            + MAIN_SITE + "'.\n Statistics site cannot be generated.\n"
            + "Details:\n";
        logger.log(Level.WARNING, errorMsg, e);

        return generateErrorSite(errorMsg + e.getMessage());
      } catch (TemplateException e) {
        String errorMsg = "Error occurred while proccessing template '"
            + MAIN_SITE + "'.\n Statistics site cannot be generated.\n"
            + "Details:\n";
        logger.log(Level.WARNING, errorMsg, e);

        return generateErrorSite(errorMsg + e.getMessage());
      }
    }

    /**
     * Generates site with listed slaves which participates
     * in evaluation.
     * @param startTime Time when evolution started.
     * @param runningTime Total evolution time in seconds.
     * @param currentIteration Current iteration number.
     * @param slaves List of all slaves.
     * @return Html code of slaves site.
     */
    public String generateSlavesSite(String startTime, double runningTime,
            long currentIteration, List<String> slaves) {
      StringWriter stringWriter = new StringWriter();
      try {
        Template template = configuration.getTemplate(SLAVES_SITE);
        HashMap<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put(SLAVES, slaves);
        template.process(rootMap, stringWriter);
        stringWriter.flush();

        return generateSite(startTime, runningTime, currentIteration,
                stringWriter.toString());
      } catch (IOException e) {
        String errorMsg = "Error occured: cannot find template '"
            + SLAVES_SITE + "'.\nStatistics site cannot be generated.\n"
            + "Details:\n";
        logger.log(Level.WARNING, errorMsg, e);

        return generateErrorSite(errorMsg + e.getMessage());
      } catch (TemplateException e) {
        String errorMsg = "Error occurred while proccessing template '"
            + SLAVES_SITE + "'.\nStatistics site cannot be generated.\n"
            + "Details:\n";
        logger.log(Level.WARNING, errorMsg, e);

        return generateErrorSite(errorMsg + e.getMessage());
      }
    }

    /**
     * Generates site with listed iterations.
     * @param startTime Time when evolution started.
     * @param runningTime Total evolution time in seconds.
     * @param currentIteration Current iteration number.
     * @param iterations List of iterations to set in table.
     * @return Html code of iterations site.
     */
    public String generateIterationsSite(String startTime, double runningTime,
            long currentIteration, List<IterationStatistics> iterations) {
      StringWriter stringWriter = new StringWriter();
      try {
        Template template = configuration.getTemplate(ITERATIONS_SITE);
        Map<String, Object> rootMap = new HashMap<String, Object>();

        List<Map<String, Double>> iters = 
            new LinkedList<Map<String, Double>>();

        for (int i = iterations.size() - 1; i >= 0; i--) {
            iters.add(iterations.get(i).getCounters());
        }

        if (iters.size() > 0) {
          rootMap.put(LAST_IN_TABLE, iters.get(0).get(
              IterationStatistics.ITERATION_NUMBER).longValue());
          rootMap.put(FIRST_IN_TABLE, iters.get(iters.size() - 1).get(
              IterationStatistics.ITERATION_NUMBER).longValue());
        } else {
          rootMap.put(LAST_IN_TABLE, 0);
          rootMap.put(FIRST_IN_TABLE, 0);
        }
        rootMap.put(ITERATIONS, iters);
        rootMap.put(CURRENT_ITERATION, currentIteration);
        rootMap.put(TABLE_LENGTH, DEFAULT_TABLE_LENGTH);

        template.process(rootMap, stringWriter);
        stringWriter.flush();

        return generateSite(startTime, runningTime, currentIteration,
                stringWriter.toString());
      } catch (IOException e) {
          String errorMsg = "Error occured: cannot find template '"
              + ITERATIONS_SITE + "'.\nStatistics site cannot be generated.\n"
              + "Details:\n";
          logger.log(Level.WARNING, errorMsg, e);

          return generateErrorSite(errorMsg + e.getMessage());
      } catch (TemplateException e) {
        String errorMsg = "Error occurred while proccessing template '"
            + ITERATIONS_SITE + "'.\nStatistics site cannot be generated.\n"
            + "Details:\n";
        logger.log(Level.WARNING, errorMsg, e);

        return generateErrorSite(errorMsg + e.getMessage());
      }
    }

    /**
     * Generates single iteration detail site.
     * @param startTime Time when evolution started.
     * @param runningTime Total evolution time in seconds.
     * @param currentIteration Current iteration number.
     * @param choosenIteration Number of detailed iteration.
     * @param stats Iteration statistics.
     * @return Html code of iteration detail site.
     */
    public String generateIterationSite(String startTime, double runningTime,
        long currentIteration, long choosenIteration,
        IterationStatistics stats) {
      StringWriter stringWriter = new StringWriter();
      try {
        Template template = configuration.getTemplate(ITERATION_SITE);
        HashMap<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put(ITERATION_DETAIL_NUMBER, choosenIteration);
        rootMap.put(COUNTERS, stats.getCounters());

        template.process(rootMap, stringWriter);
        stringWriter.flush();

        return generateSite(startTime, runningTime, currentIteration,
            stringWriter.toString());
      } catch (IOException e) {
          String errorMsg = "Error occured: cannot find template '"
              + ITERATION_SITE + "'.\nStatistics site cannot be generated.\n"
              + "Details:\n";
          logger.log(Level.WARNING, errorMsg, e);

          return generateErrorSite(errorMsg + e.getMessage());
      } catch (TemplateException e) {
        String errorMsg = "Error occurred while proccessing template '"
            + ITERATION_SITE + "'.\nStatistics site cannot be generated.\n"
            + "Details:\n";
        logger.log(Level.WARNING, errorMsg, e);

        return generateErrorSite(errorMsg + e.getMessage());
      }
    }

    /**
     * Generates settings site.
     * @param startTime Time when evolution started.
     * @param runningTime Total evolution time in seconds.
     * @param currentIteration Current iteration number.
     * @return Html code of settings site.
     */
    public String generateSettingsSite(String startTime, double runningTime,
            long currentIteration) {
      StringWriter stringWriter = new StringWriter();
      try {
        Template template = configuration.getTemplate(SETTINGS_SITE);
        HashMap<String, Object> rootMap = new HashMap<String, Object>();
        template.process(rootMap, stringWriter);
        stringWriter.flush();

        return generateSite(startTime, runningTime, currentIteration,
                stringWriter.toString());
      } catch (IOException e) {
          String errorMsg = "Error occured: cannot find template '"
              + SETTINGS_SITE + "'.\nStatistics site cannot be generated.\n"
              + "Details:\n";
          logger.log(Level.WARNING, errorMsg, e);

          return generateErrorSite(errorMsg + e.getMessage());
      } catch (TemplateException e) {
        String errorMsg = "Error occurred while proccessing template '"
            + SETTINGS_SITE + "'.\nStatistics site cannot be generated.\n"
            + "Details:\n";
        logger.log(Level.WARNING, errorMsg, e);

        return generateErrorSite(errorMsg + e.getMessage());
      }
    }

    /**
     * Generates error site.
     * @param errorMsg Error message to display.
     * @return Html code of error site.
     */
    public String generateErrorSite(String errorMsg) {
      StringWriter stringWriter = new StringWriter();
      try {
        Template template = configuration.getTemplate(ERROR_SITE);
        HashMap<String, Object> rootMap = new HashMap<String, Object>();
        rootMap.put(ERROR_MESSAGE, errorMsg.replaceAll("\n", "<br>"));
        template.process(rootMap, stringWriter);
        stringWriter.flush();

        return stringWriter.toString();
      } catch (IOException e) {
        logger.log(Level.WARNING, "Error occurred: cannot find template '"
              + ERROR_SITE + "'. Statistics site cannot be generated.", e);
      } catch (TemplateException e) {
        logger.log(Level.WARNING, "Error occurred while proccessing template '"
              + ERROR_SITE + "'. Statistics site cannot be generated.", e);
      }
      return "";
    }
}
