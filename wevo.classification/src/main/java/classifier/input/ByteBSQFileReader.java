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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading ENVI file containing signed bytes
 * with BSQ interleaving.
 *
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public final class ByteBSQFileReader extends BSQFileReader<Byte> {

    /** Dummy field representing byte size in bytes. */
    private static final int TYPESIZE = 1;

    /**
     * Standard constructor.
     *
     * @param headerFile Name of the header file.
     * @param dataFile Name of the data file.
     * @throws IOException when an I/O error occurs while reading header file.
     */
    public ByteBSQFileReader(String headerFile, String dataFile)
            throws IOException {
        this(headerFile, dataFile, BUFSIZ);
    }

    /**
     * Standard constructor.
     *
     * @param headerFile Name of the header file.
     * @param dataFile Name of the data file.
     * @param bufferSize Buffer size in bytes.
     * @throws IOException when an I/O error occurs while reading header file.
     */
    public ByteBSQFileReader(String headerFile, String dataFile,
            int bufferSize) throws IOException {
        super(headerFile, dataFile, bufferSize);
        /* we do it here because we need TYPESIZE in
         * order to correctly allocate buffers. */
        buffers = createBuffers(bandsNumber, bufferSize, byteOrder, TYPESIZE);
    }

    /**
     * Constructor to use when there's no header file.
     *
     * @param filename Name of the data file.
     * @param rows Number of rows.
     * @param columns Number of columns (samples).
     * @param bands Number of bands.
     * @param headeroffset Self-explanatory.
     * @throws FileNotFoundException when there's no data file.
     */
    public ByteBSQFileReader(String filename, int rows, int columns, int bands,
            int headeroffset) throws FileNotFoundException {
        this(filename, rows, columns, bands, headeroffset, BUFSIZ);
    }

    // Parameter Number off

    /**
     * Constructor to use when there's no header file.
     *
     * @param filename Name of the data file.
     * @param rows Number of rows.
     * @param columns Number of columns (samples).
     * @param bands Number of bands.
     * @param headeroffset Self-explanatory.
     * @param bufferSize Buffer size in bytes.
     * @throws FileNotFoundException when there's no data file.
     */
    public ByteBSQFileReader(String filename, int rows, int columns, int bands,
            int headeroffset, int bufferSize) throws FileNotFoundException {
        super(filename, rows, columns, bands, headeroffset, bufferSize);
        buffers = createBuffers(bandsNumber, bufferSize, byteOrder, TYPESIZE);
    }

    // Parameter Number on

    /** {@inheritDoc} */
    @Override
    protected long getNextPixelOffset(int band) {
        return this.headerOffset + band * this.rowsNumber * this.columnsNumber
            + this.position;
    }

    /** {@inheritDoc} */
    @Override
    protected List<Byte> readPixel() throws IOException {
        List<Byte> pixel = new ArrayList<Byte>();
        if (!this.buffers.get(0).hasRemaining()) {
            this.fillBuffer();
        }
        for (int i = 0; i < this.bandsNumber; i++) {
            pixel.add(this.buffers.get(i).get());
        }
        return pixel;
    }

    /** {@inheritDoc} */
    @Override
    protected int bytesToRead() {
        return (int) Math.min(bufferSize,
            rowsNumber * columnsNumber - position);

    }
}
