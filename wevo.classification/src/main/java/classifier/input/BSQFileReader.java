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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;

/**
 * Abstract class representing ENVI files with BSQ interleaving.
 *
 * @param <D> internal data type
 *
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public abstract class BSQFileReader<D> extends EnviFileReader<D> {

    // Visibility Modifier off

    /** Default buffer size. */
    protected static final int BUFSIZ = 4096;

    /** Internal buffer size. */
    protected int bufferSize;

    /** Internal buffer. */
    protected List<ByteBuffer> buffers;

    // Visibility Modifier on

    /**
     * Standard constructor.
     *
     * @param headerFile Name of the header file.
     * @param dataFile Name of the data file.
     * @param bufferSize Buffer size in bytes.
     * @throws IOException when an I/O error occurs while reading header file.
     */
    protected BSQFileReader(String headerFile, String dataFile, int bufferSize)
            throws IOException {
        super(headerFile, dataFile);
        this.bufferSize = bufferSize;
    }

    // Parameter Number off

    /**
     * Another constructor.
     *
     * @param filename Name of the data file.
     * @param rows Number of rows.
     * @param columns Number of columns (samples).
     * @param bands Number of bands.
     * @param headeroffset Self-explanatory.
     * @param bufferSize Buffer size in bytes.
     * @throws FileNotFoundException when there's no data file.
     */
    protected BSQFileReader(String filename, int rows, int columns, int bands,
            int headeroffset, int bufferSize) throws FileNotFoundException {
        super(filename, rows, columns, bands, headeroffset);
        this.bufferSize = bufferSize;
    }

    // Parameter Number on

    /**
     * Sets internal buffer size.
     *
     * @param bands Number of bands in image.
     * @param size Buffer size.
     * @param byteOrder Byte order in buffer.
     * @param typesize Single buffered value size.
     * @return Buffer
     */
    protected static List<ByteBuffer> createBuffers(int bands, int size,
            ByteOrder byteOrder, int typesize) {
        if (size <= 0) {
            throw new IllegalArgumentException("Buffer size must be "
                + "a positive number");
        } else if (size < typesize) {
            throw new IllegalArgumentException("Buffer size must be greater "
                    + " than or equal to " + typesize + ".");
            }
        List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();
        for (int i = 0; i < bands; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(size);
            buffer.order(byteOrder);
            /* mark as empty */
            buffer.position(buffer.limit());
            buffers.add(buffer);
        }
        return buffers;
    }

    // Empty Statement off

    /**
     * Fills the internal buffer with new data.
     *
     * @throws IOException when file format is invalid.
     */
    protected void fillBuffer() throws IOException {
        /* no more data to read */
        if (!this.hasNext()) {
            return;
        }

        ByteBuffer buffer = this.buffers.get(0);
        byte[] inputarray = new byte[bytesToRead() - buffer.remaining()];
        for (int i = 0; i < this.bandsNumber; i++) {
            buffer = this.buffers.get(i);
            this.file.seek(this.getNextPixelOffset(i));
            this.file.readFully(inputarray);
            buffer.compact();
            buffer.put(inputarray);
            buffer.rewind();
        }
    }

    // Empty Statement on

    /** {@inheritDoc} */
    @Override
    public List<D> next() {
        if (!this.hasNext()) {
            this.logger.log(Level.WARNING, "No more pixels to read.");
            throw new NoSuchElementException();
        }
        List<D> pixel;
        try {
            pixel = this.readPixel();
        /* we have to catch this IOException here, as it is incompatible
         * with Iterator#next method. */
        } catch (IOException e) {
            this.logger.log(Level.SEVERE, "Cannot read from data file.");
            throw new NoSuchElementException(e.getMessage());
        }
        this.position++;
        return pixel;
    }

    /** {@inheritDoc} */
    @Override
    public void setByteOrder(ByteOrder byteOrder) {
        super.setByteOrder(byteOrder);
        for (int i = 0; i < bandsNumber; i++) {
            buffers.get(i).order(byteOrder);
        }
    }

    /**
     * Gets pixel offset in data file.
     * Basically it's header offset + band offset + pixel offset in band.
     *
     * @param band Band number.
     * @return Pixel offset.
     */
    protected abstract long getNextPixelOffset(int band);

    /**
     * Reads single value of type D from data file.
     *
     * @throws IOException when value cannot be read from data file.
     * @return Single value from data file.
     */
    protected abstract List<D> readPixel() throws IOException;

    /**
     * Gets number of bytes to read in the fillBuffer() method call.
     * This is needed for detecting ill-formed (too short) data files.
     * 
     * @return Number of bytes left in data file.
     */
    protected abstract int bytesToRead();

}
