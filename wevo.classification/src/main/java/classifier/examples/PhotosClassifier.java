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
package classifier.examples;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import classifier.ClassifierObjectiveFunction;
import classifier.LearningAlgorithm;
import classifier.Rule;
import classifier.TrainedClassifier;
import classifier.TrainedClassifierProducer;
import classifier.data.ClassifiedSample;
import classifier.data.LearningSet;
import classifier.examples.operators.PhotoRulesBordersMutation;
import classifier.examples.operators.PhotoRulesIntervalAddMutation;
import classifier.examples.operators.PhotoRulesIntervalCutMutation;
import classifier.examples.operators.PhotoRulesIntervalMutation;
import classifier.examples.operators.PhotoRulesIntervalRemoveMutation;
import classifier.examples.operators.PhotoRulesMutation;
import classifier.examples.operators.PhotoRulesUniformCrossover;
import classifier.examples.operators.PopulationStatisticsLogger;
import classifier.examples.operators.ReplacementOperator;
import classifier.examples.operators.SavePopulationOperator;
import classifier.examples.operators.TournamentSelection;
import classifier.input.AllToIntegerBSQFileConverter;
import classifier.input.BMPFileReader;
import classifier.input.ByteBSQFileReader;
import classifier.output.BMPFileWriter;
import engine.Algorithm;
import engine.CachedObjectiveFunction;
import engine.PopulationEvaluator;
import engine.SingleThreadedEvaluator;
import engine.exitcriteria.MaxIterations;
import engine.utils.JavaRandom;
import engine.utils.ListUtils;

/**
 * Multispectral satellites photos classifying system.
 * This program reads expert data (a pair: bsq and bmp file),
 * then trains the parameters and finally classifies given
 * bsq file. As an output it produces bmp file with classification.
 * 
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public final class PhotosClassifier {

    /** Length of single data point vector. */
    private static final int DATA_POINT_LENGTH = 3;

    /** Logger. */
    private final Logger logger =
        Logger.getLogger(PhotosClassifier.class.getCanonicalName());

    /** Number of individuals in initial population. */
    @Option(name = "-ps", aliases = { "populationSize" }, usage = "Size of "
        + "population in evolutionary algorithm.")
    // MagicNumber off
    private int populationSize = 20;

    /** Cache size for cached objective function. */
    @Option(name = "-cs", aliases = { "cacheSize" }, usage = "Size of the cache"
        + " for evaluation results. Should be bigger than population size.")
    private int cacheSize = 200;

    /** Number of evolutionary algorithm iterations. */
    @Option(name = "-i", aliases = { "iterations" }, usage = "Number of "
        + "iterations in evolutionary algorithm.")
    private int iterations = 20;

    /** Name of expert data header file (HDR). */
    @Option(name = "-edh", aliases = { "expert-data-header" }, usage =
        "Name of expert data BSQ file header.", required = true)
    private String expertDataHeaderFilename;

    /** Name of expert data file (BSQ). */
    @Option(name = "-edp", aliases = { "expert-data-photo" }, usage =
        "Name of expert data BSQ file.", required = true)
    private String expertDataPhotoFilename;

    /** Name of expert data classification file (BMP). */
    @Option(name = "-edc", aliases = { "expert-data-classification" }, usage =
        "Name of expert data classification BMP file.", required = true)
    private String expertDataClassificationFilename;

    /** Name of photo header file to classify (HDR). */
    @Option(name = "-ch", aliases = { "classify-header" }, usage = "Name of BSQ"
        + "header file on which program should use trained classifier.",
        required = true)
    private String classifyHeaderFilename;

    /** Name of photo file to classify (BSQ). */
    @Option(name = "-cp", aliases = { "classify-photo" }, usage =
        "Name of BSQ file on which program should use trained classifier.",
        required = true)
    private String classifyPhotoFilename;

    /** Name of bmp file where bsq classification should be write. */
    @Option(name = "-cof", aliases = { "classify-output-file" }, usage =
        "Name of BMP file where program should write output classification.")
    private String classifyOutputFilename = "output.bmp";


    /* Parameters for fine-tuning the algorithm. */

    /** Standard deviation for generating initial population. */
    private double sigma = 48;

    /* Tournament selection parameters. */

    /** Fraction of population to take in tournament selection. */
    private double ratio = 0.6;

    /** Fraction of population competing in a tournament. */
    private double tournamentRatio = 0.4;

    /* Borders shifting parameters. */

    /** Self-explanatory. */
    private double bordersMutationProbability = 0.4;

    /** Standard deviation for shift. */
    private double bordersSigma = 32;

    /* Interval mutation parameters. */

    /** Self-explanatory. */
    private double intervalMutationProbability = 0.4;

    /** Initial length of a newly-added interval. */
    private int intervalAddLength = 32;

    /**
     * Main program entry.
     * @param args List of command line arguments.
     */
    public static void main(final String[] args) {
      for (Handler s : Logger.getLogger("").getHandlers()) {
        s.setLevel(Level.ALL);
      }
      new PhotosClassifier().doMain(args);
    }

    /**
     * Main program entry.
     * @param args Program arguments.
     */
    public void doMain(String[] args) {
      CmdLineParser parser = new CmdLineParser(this);

      try {
        parser.parseArgument(args);
      } catch (CmdLineException e) {
        parser.printUsage(System.err);
        System.exit(1);
      }

      // Create learning set
      LearningSet<List<Integer>, SatellitePhotoCategory> learningSet
        = createLearningSet();

      // Create evolutionary algorithm
      Algorithm<Rule<List<Integer>, SatellitePhotoCategory>> evolutionaryAlg = 
          new Algorithm<Rule<List<Integer>, SatellitePhotoCategory>>(null);
      ClassifierObjectiveFunction<List<Integer>, 
          SatellitePhotoCategory> classifierFunction = 
              new ClassifierObjectiveFunction<List<Integer>,
                  SatellitePhotoCategory>(learningSet.getSamples());
      CachedObjectiveFunction<Rule<List<Integer>, 
          SatellitePhotoCategory>> objectiveFunctionWrapper = 
              new CachedObjectiveFunction<Rule<List<Integer>,
                  SatellitePhotoCategory>>(classifierFunction, cacheSize);

      PopulationEvaluator<Rule<List<Integer>, 
          SatellitePhotoCategory>> evaluator =
              buildObjectiveFunctions(objectiveFunctionWrapper);

      evolutionaryAlg.addExitPoint(new MaxIterations<Rule<List<Integer>,
          SatellitePhotoCategory>>(iterations));
      evolutionaryAlg.addEvaluationPoint(evaluator);
      evolutionaryAlg.addOperator(new PopulationStatisticsLogger<Rule<
          List<Integer>, SatellitePhotoCategory>>(objectiveFunctionWrapper));
      SavePopulationOperator<Rule<List<Integer>, SatellitePhotoCategory>> 
          saveOperator = new SavePopulationOperator<Rule<List<Integer>,
              SatellitePhotoCategory>>();
      evolutionaryAlg.addOperator(saveOperator);
      evolutionaryAlg.addOperator(new TournamentSelection<Rule<List<Integer>,
          SatellitePhotoCategory>>(objectiveFunctionWrapper, 
              ratio, tournamentRatio, new JavaRandom()));
      evolutionaryAlg.addOperator(
          new PhotoRulesUniformCrossover(new JavaRandom()));
      evolutionaryAlg.addOperator(new PhotoRulesMutation(new JavaRandom(),
          new PhotoRulesBordersMutation(new JavaRandom(),
              bordersMutationProbability, bordersSigma),
          new PhotoRulesIntervalMutation(new JavaRandom(),
              intervalMutationProbability,
              new PhotoRulesIntervalAddMutation(new JavaRandom(),
                  intervalAddLength),
              new PhotoRulesIntervalCutMutation(new JavaRandom()),
              new PhotoRulesIntervalRemoveMutation(new JavaRandom()))));
      evolutionaryAlg.addOperator(evaluator);
      evolutionaryAlg.addOperator(new ReplacementOperator<Rule<List<Integer>,
          SatellitePhotoCategory>>(objectiveFunctionWrapper, saveOperator));

      // Create learning algorithm
      LearningAlgorithm<List<Integer>, SatellitePhotoCategory> learningAlg = 
          new LearningAlgorithm<List<Integer>, SatellitePhotoCategory>(
            learningSet.getSamples(),
            new PhotoRulesGenerator(new JavaRandom(), sigma, DATA_POINT_LENGTH),
            evolutionaryAlg,
            populationSize);

      // Start learning.
      LinkedHashMap<Rule<List<Integer>, SatellitePhotoCategory>, Double>
          rules = learningAlg.learn();

      // Classify given file and output result to bmp file
      classifyImage(rules);

      /* Create diagrams for expert data image. */
      createRulesDiagrams(rules, learningSet);
    }

    /**
     * Classifies bsq file using trained classifier.
     * @param rules Trained rules.
     */
    private void classifyImage(LinkedHashMap<Rule<List<Integer>, 
        SatellitePhotoCategory>, Double> rules) {

      ByteBSQFileReader bsqReader = null;
      try {
        bsqReader = new ByteBSQFileReader(classifyHeaderFilename, 
            classifyPhotoFilename);
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Cant open bsq file "
            + classifyPhotoFilename);
        System.exit(1);
      }

      TrainedClassifierProducer<List<Integer>, SatellitePhotoCategory>
          classifierProducer = new TrainedClassifierProducer<List<Integer>,
            SatellitePhotoCategory>();

      TrainedClassifier<List<Integer>, SatellitePhotoCategory> 
          classifier = classifierProducer.getClassifierForSingleInput(rules,
              new AllToIntegerBSQFileConverter<Byte>(bsqReader));

      // Write bmp file
      int width = bsqReader.getColumnsNumber();
      int height = bsqReader.getRowsNumber();
      try {
        List<Color> pixels = new ArrayList<Color>();
        while (classifier.hasNext()) {
          pixels.add(classifier.next().getCategoryColor());
        }
        BMPFileWriter.writeBitmapFile(classifyOutputFilename, width, height,
            pixels);
      } catch (IOException e) {
        logger.log(Level.SEVERE,
            "Can't write to file " + classifyOutputFilename + ".");
        System.exit(1);
      }
    }

    /**
     * This method outputs diagram for each rule. A rule diagram is
     * a bitmap made for expert photo on which algorithm was learning
     * using learned rules. For given category and pixel there are three
     * cases:
     *   - true positive: when rule and expert says that the pixel
     *     is in category - depicted as green pixel
     *   - false positive: when rule and expert says that the pixel
     *     is not in category - depicted as a dark green pixel 
     *   - false: when rule and expert does not agree - depicted as a red pixel.
     * 
     * @param rules Rules set.
     * @param learningSet Learning set.
     */
    private void createRulesDiagrams(LinkedHashMap<Rule<List<Integer>,
            SatellitePhotoCategory>, Double> rules,
        LearningSet<List<Integer>,
            SatellitePhotoCategory> learningSet) {

      int i = 0; 
      for (Rule<List<Integer>, SatellitePhotoCategory> rule : rules.keySet()) {
        List<Color> pixels = new ArrayList<Color>();
        for (ClassifiedSample<List<Integer>, SatellitePhotoCategory> sample
            : learningSet.getSamples()) {
          Color color = null;
          if (rule.classify(sample.getDataSample())) {
            if (rule.getCategory().equals(sample.getCategory())) {
              color = Color.green;
            } else {
              color = Color.red;
            }
          } else {
            if (rule.getCategory().equals(sample.getCategory())) {
              color = Color.red;
            } else {
              color = new Color(0, 155, 0);
            }
          }
          pixels.add(color);
        }
        try {
          BMPFileWriter.writeBitmapFile(
              filterWhitespaces(rule.getCategory().toString()) + ".bmp",
              learningSet.getWidth(), learningSet.getHeight(), pixels);
        } catch (IOException e) {
          logger.log(Level.SEVERE,
              "Can't write to file " + classifyOutputFilename + ".");
          System.exit(1);
        }
        i++;
      }
    }

    /**
     * Creates learning set.
     * @return Learning set.
     */
    private LearningSet<List<Integer>, SatellitePhotoCategory> 
        createLearningSet() {

      // Read bsq file with expert data
      List<List<Integer>> dataPixels = new ArrayList<List<Integer>>();

      ByteBSQFileReader bsqReader = null;
      try {
        bsqReader = new ByteBSQFileReader(expertDataHeaderFilename, 
            expertDataPhotoFilename);
      } catch (IOException e1) {
        logger.log(Level.SEVERE, "Cant open bsq file "
            + expertDataPhotoFilename);
        System.exit(1);
      }

      AllToIntegerBSQFileConverter<Byte> bsqIntReader = 
          new AllToIntegerBSQFileConverter<Byte>(bsqReader);
      while (bsqIntReader.hasNext()) {
        dataPixels.add(bsqIntReader.next());
      }

      // Read bmp file with expert classification
      List<Color> pixels = null;
      try {
        pixels = BMPFileReader.readBitmapFile(expertDataClassificationFilename);
      } catch (IOException e) {
        logger.log(Level.SEVERE, "Cant open file "
            + expertDataClassificationFilename);
        System.exit(1);
      } catch (InterruptedException e) {
        logger.log(Level.SEVERE, "Cant read from file "
            + expertDataClassificationFilename);
        System.exit(1);
      }

      List<ClassifiedSample<List<Integer>, SatellitePhotoCategory>> samples
          = zipExpertDataFiles(dataPixels, pixels);

      return new LearningSet<List<Integer>, SatellitePhotoCategory>(samples,
          bsqReader.getColumnsNumber(), bsqReader.getRowsNumber());
    }

    /** 
     * Creates expert data from given data pixels and it's colored
     * classification.
     * @param dataPixels Pixels from data image.
     * @param pixels Pixels from classification image.
     * @return Expert data list.
     */
    private List<ClassifiedSample<List<Integer>, SatellitePhotoCategory>>
        zipExpertDataFiles(List<List<Integer>> dataPixels, List<Color> pixels) {
      if (dataPixels.size() != pixels.size()) {
        throw new IllegalStateException("Data image and classification image"
            + "have different number of pixels.");
      }

      List<ClassifiedSample<List<Integer>, SatellitePhotoCategory>> expertData =
          new ArrayList<ClassifiedSample<List<Integer>,
            SatellitePhotoCategory>>();
      for (int i = 0; i < dataPixels.size(); i++) {
        expertData.add(
          new ClassifiedSample<List<Integer>, SatellitePhotoCategory>(
            dataPixels.get(i),
            new SatellitePhotoCategory(pixels.get(i))));
      }
      return expertData;
    }

    /**
     * Creates a list of objective functions to be optimized.
     * @param objectiveFunctionWrapper Function on which list returned by 
     *  method is based.
     * @return List of objective functions.
     */
    @SuppressWarnings("unchecked")
    private static PopulationEvaluator<Rule<List<Integer>,
        SatellitePhotoCategory>> buildObjectiveFunctions(
          CachedObjectiveFunction<Rule<List<Integer>, SatellitePhotoCategory>> 
            objectiveFunctionWrapper) {
      return new SingleThreadedEvaluator<Rule<List<Integer>,
        SatellitePhotoCategory>>(ListUtils.buildList(objectiveFunctionWrapper));
    }

    /**
     * Filters whitespaces in given string. 
     * @param s String to filter.
     * @return Resulted string without whitespaces.
     */
    private String filterWhitespaces(String s) {
      String result = "";
      for (int i = 0; i < s.length(); i++) {
        if (!Character.isWhitespace(s.charAt(i))) {
          result += s.charAt(i);
        }
      }
      return result;
    }
}
