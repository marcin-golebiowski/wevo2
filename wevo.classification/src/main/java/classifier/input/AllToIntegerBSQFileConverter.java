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

package classifier.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is an iterator which is used to convert.
 * It reads values from bsq file where every pixel
 * is a vector of bytes and converts it to the
 * vector of integers.
 *
 * @param <D> Type of data in BSQ file.
 *
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class AllToIntegerBSQFileConverter<D extends Number>
    implements Iterator<List<Integer>> {

    /** Envi file to read from. */
    private final BSQFileReader<D> reader;

    /**
     * Standard constructor.
     * @param reader BSQ file reader.
     */
    public AllToIntegerBSQFileConverter(BSQFileReader<D> reader) {
      this.reader = reader;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
      return this.reader.hasNext();
    }

    /**
     * {@inheritDoc}
     */
    public List<Integer> next() {
      List<D> bs = this.reader.next();
      List<Integer> values = new ArrayList<Integer>();
      for (D b : bs) {
        values.add(b.intValue());
      }
      return values;
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
      this.reader.remove();
    }


}
