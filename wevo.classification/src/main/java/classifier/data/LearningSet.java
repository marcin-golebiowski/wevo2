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

package classifier.data;

import java.util.List;

/**
 * Class representing learning set.
 * 
 * @param <D> Data sample type.
 * @param <C> Category type.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class LearningSet<C, D> {

    /** Classified data samples. */
    private List<ClassifiedSample<C, D>> samples;

    /** Width of the expert data image. */
    private int width;

    /** Height of the expert data image. */
    private int height;

    /**
     * Standard constructor.
     * 
     * @param samples Classified data samples.
     * @param width Width of the expert data image.
     * @param height Height of the expert data image.
     */
    public LearningSet(List<ClassifiedSample<C, D>> samples, int width,
            int height) {
        this.samples = samples;
        this.width = width;
        this.height = height;
    }

    /**
     * Getter for samples.
     * 
     * @return Classified data samples.
     */
    public List<ClassifiedSample<C, D>> getSamples() {
        return samples;
    }

    /**
     * Getter for width.
     * 
     * @return Width of the expert data image.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter for height.
     * 
     * @return Height of the expert data image.
     */
    public int getHeight() {
        return height;
    }
}
