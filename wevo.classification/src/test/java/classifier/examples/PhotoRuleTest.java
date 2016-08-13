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

import static org.testng.Assert.assertEquals;

import java.awt.Color;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import classifier.Rule;

/**
 * Tests for {@link PhotoRule}.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class PhotoRuleTest {

    /** Test if classifying is done correctly. */
    @Test
    public void testClassifying() {
        // MagicNumber off
        List<List<Integer>> params = new ArrayList<List<Integer>>();
        params.add(Arrays.asList(10, 20));
        params.add(Arrays.asList(30, 40));
        params.add(Arrays.asList(50, 60));
        Rule<List<Integer>, SatellitePhotoCategory> rule = 
            new Rule<List<Integer>, SatellitePhotoCategory>(
                new PhotoRuleCondition(params),
                new SatellitePhotoCategory(Color.black));

        List<Integer> sample1 = new ArrayList<Integer>(
            Arrays.asList(30, 50, 70));  // 000
        List<Integer> sample2 = new ArrayList<Integer>(
            Arrays.asList(30, 50, 55));  // 001
        List<Integer> sample3 = new ArrayList<Integer>(
            Arrays.asList(30, 35, 70));  // 010
        List<Integer> sample4 = new ArrayList<Integer>(
            Arrays.asList(30, 35, 55));  // 011
        List<Integer> sample5 = new ArrayList<Integer>(
            Arrays.asList(15, 50, 70));  // 100
        List<Integer> sample6 = new ArrayList<Integer>(
            Arrays.asList(15, 50, 55));  // 101
        List<Integer> sample7 = new ArrayList<Integer>(
            Arrays.asList(15, 35, 70));  // 110
        List<Integer> sample8 = new ArrayList<Integer>(
            Arrays.asList(15, 35, 55));  // 111

        assertEquals(false, rule.classify(sample1));
        assertEquals(false, rule.classify(sample2));
        assertEquals(false, rule.classify(sample3));
        assertEquals(false, rule.classify(sample4));
        assertEquals(false, rule.classify(sample5));
        assertEquals(false, rule.classify(sample6));
        assertEquals(false, rule.classify(sample7));
        assertEquals(true, rule.classify(sample8));
    }

    /** Test if fuzzy classifying is done correctly. */
    @Test
    public void testFuzzyClassifying() {
        // MagicNumber off
        List<List<Integer>> params = new ArrayList<List<Integer>>();
        params.add(Arrays.asList(10, 20));
        params.add(Arrays.asList(30, 40));
        params.add(Arrays.asList(50, 60));
        Rule<List<Integer>, SatellitePhotoCategory> rule = 
            new Rule<List<Integer>, SatellitePhotoCategory>(
                new PhotoRuleCondition(params),
                new SatellitePhotoCategory(Color.black));

        List<Integer> sample1 = new ArrayList<Integer>(
            Arrays.asList(30, 50, 70));  // 000
        List<Integer> sample2 = new ArrayList<Integer>(
            Arrays.asList(30, 50, 55));  // 001
        List<Integer> sample3 = new ArrayList<Integer>(
            Arrays.asList(30, 35, 70));  // 010
        List<Integer> sample4 = new ArrayList<Integer>(
            Arrays.asList(30, 35, 55));  // 011
        List<Integer> sample5 = new ArrayList<Integer>(
            Arrays.asList(15, 50, 70));  // 100
        List<Integer> sample6 = new ArrayList<Integer>(
            Arrays.asList(15, 50, 55));  // 101
        List<Integer> sample7 = new ArrayList<Integer>(
            Arrays.asList(15, 35, 70));  // 110
        List<Integer> sample8 = new ArrayList<Integer>(
            Arrays.asList(15, 35, 55));  // 111

        assertEquals(0.0, rule.classifyFuzzyMembership(sample1), 0.01);
        assertEquals(0.33, rule.classifyFuzzyMembership(sample2), 0.01);
        assertEquals(0.33, rule.classifyFuzzyMembership(sample3), 0.01);
        assertEquals(0.66, rule.classifyFuzzyMembership(sample4), 0.01);
        assertEquals(0.33, rule.classifyFuzzyMembership(sample5), 0.01);
        assertEquals(0.66, rule.classifyFuzzyMembership(sample6), 0.01);
        assertEquals(0.66, rule.classifyFuzzyMembership(sample7), 0.01);
        assertEquals(1.0, rule.classifyFuzzyMembership(sample8), 0.01);
    }

    /** 
     * Testing whether exception is thrown on incorrect 
     * ranges/sample lengths.
     */
    @Test(expectedExceptions = IllegalStateException.class)
    public void testExceptionInClassify() {
        List<List<Integer>> ranges = new ArrayList<List<Integer>>();
        ranges.add(null);
        ranges.add(null);

        Rule<List<Integer>, SatellitePhotoCategory> rule = 
            new Rule<List<Integer>, SatellitePhotoCategory>(
                new PhotoRuleCondition(ranges),
                new SatellitePhotoCategory(Color.black));
        List<Integer> sample = new ArrayList<Integer>(
            Arrays.asList(7, 10, 11));

        rule.classify(sample);
    }
}
