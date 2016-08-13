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

/**
 * Class representing single point sample with it's category.
 * @param <D> Data sample type.
 * @param <C> Category type.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class ClassifiedSample<D, C> {

    /** Single data sample. */
    private D dataSample;

    /** Category associated with data point. */
    private C category;

    /**
     * Standard constructor.
     * @param dataSample Point o data.
     * @param category Associated category.
     */
    public ClassifiedSample(D dataSample, C category) {
        this.dataSample = dataSample;
        this.category = category;
    }

    /**
     * Getter for dataSample.
     * @return Data sample.
     */
    public D getDataSample() {
        return dataSample;
    }

    /**
     * Getter for category.
     * @return category
     */
    public C getCategory() {
        return category;
    }
}
