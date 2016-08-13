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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract class for reading ENVI files.
 *
 * ENVI files are commonly used for storing 3-dimensional data. There are
 * three major flavors of them:
 * Band Sequential (BSQ): each line of data followed immediately by the next
 * line in the same spectral band and so on.
 * Band Interleaved By Pixel Format (BIP): the first pixel for all bands in
 * sequential order followed by the second pixel for all bands and so on.
 * Band Interleaved By Line Format (BIL): the first line of the first band
 * followed by the first line of the second band and so on.
 *
 * Optionally, there may be an additional header file (.hdr) containing
 * "ENVI" string followed by some "key = value" pairs.
 * The most important ones among keys are:
 * samples: number of columns
 * lines: self-explanatory
 * bands: self-explanatory
 * header offset: position in file, from which the actual data starts
 * data type: integer representing internal data format:
 *          1: byte (8 bits)
 *          2: signed short integer (16 bits)
 *          3: signed integer (32 bits)
 *          4: float (32 bits)
 *          5: double (64 bits)
 *          6: complex number (float, float)
 *          9: complex number (double, double)
 *          12: unsigned short integer
 *          13: unsigned integer
 *          14: signed long integer (64 bits)
 *          15: unsigned long integer
 * interleave: defines format of the file (BSQ, BIP or BIL)
 * byte order: 0 for little-endian, 1 for big-endian
 *
 * Reading data type and interleave isn't supported in this class, as it
 * is determined by subclass type i.e. ShortBSQFileReader supports
 * data type = 2 and interleave = bsq.
 *
 * @param <D> internal data type
 *
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public abstract class EnviFileReader<D> implements Iterator<List<D>> {

    // Visibility Modifier off
    /** Number of bands in data file. */
    protected int bandsNumber;

    /** Number of rows in data file. */
    protected int rowsNumber;

    /** Number of columns (samples) in data file. */
    protected int columnsNumber;

    /** Self-explanatory. */
    protected int headerOffset = 0;

    /** Data file. */
    protected final RandomAccessFile file;

    /** Offset of the current pixel in file. */
    protected long position = 0;

    /** Byte order in file. Defaults to big-endian. */
    protected ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;

    /** Logger. */
    protected final Logger logger =
        Logger.getLogger(EnviFileReader.class.getCanonicalName());

    // Visibility Modifier on

    /**
     * Standard constructor.
     *
     * @param headerFile Name of the header file.
     * @param dataFile Name of the data file.
     * @throws IOException when an I/O error occurs while reading header file.
     */
    protected EnviFileReader(String headerFile, String dataFile)
            throws IOException {
        this.parseHeaderFile(headerFile);
        this.file = new RandomAccessFile(dataFile, "r");
    }

    /**
     * Constructor to use when there's no header file.
     *
     * @param filename Name of the data file.
     * @param bands Number of bands.
     * @param rows Number of rows.
     * @param columns Number of columns (samples).
     * @param headeroffset Self-explanatory.
     * @throws FileNotFoundException when there's no data file.
     */
    protected EnviFileReader(String filename, int rows, int columns, int bands,
            int headeroffset) throws FileNotFoundException {
        this.bandsNumber = bands;
        this.rowsNumber = rows;
        this.columnsNumber = columns;
        this.headerOffset = headeroffset;

        this.file = new RandomAccessFile(filename, "r");
    }

    /** Destructor. Closes data file. */
    @Override
    protected void finalize() {
        try {
            this.file.close();
        } catch (final IOException e) {
            this.logger.log(Level.WARNING, "Cannot close data file.");
        }
    }

    /**
     * Gets number of columns.
     * @return Number of columns.
     */
    public int getColumnsNumber() {
      return this.columnsNumber;
    }

    /**
     * Gets number of rows in image.
     * @return Number of rows.
     */
    public int getRowsNumber() {
      return this.rowsNumber;
    }

    /**
     * Reads image info from ENVI header file.
     *
     * @param headerFile Name of the header file to read
     * @throws IOException when an I/O error occurs while reading header file.
     */
    private void parseHeaderFile(String headerFile) throws IOException {
        /* parse header file */
        this.logger.log(Level.INFO, "Processing ENVI header file "
            + headerFile + ".");
        final BufferedReader header =
            new BufferedReader(new FileReader(headerFile));
        String line = header.readLine().trim();
        if (!line.startsWith("ENVI")) {
            throw new IllegalArgumentException(headerFile
                + " is not an ENVI file.");
        }
        int setValues = 0x0;
        while ((line = header.readLine()) != null) {
            final String[] pair = line.split("=");
            if (pair.length != 2) {
                throw new IllegalArgumentException(headerFile
                    + " is not valid ENVI header file.");
            }
            pair[0] = pair[0].trim();
            pair[1] = pair[1].trim();

            // Magic Number off

            if (pair[0].equals("samples")) {
                this.columnsNumber = Integer.parseInt(pair[1]);
                setValues |= 0x1;
            } else if (pair[0].equals("lines")) {
                this.rowsNumber = Integer.parseInt(pair[1]);
                setValues |= 0x2;
            } else if (pair[0].equals("bands")) {
                this.bandsNumber = Integer.parseInt(pair[1]);
                setValues |= 0x4;
            } else if (pair[0].equals("header offset")) {
                this.headerOffset = Integer.parseInt(pair[1]);
                setValues |= 0x8;
            } else if (pair[0].equals("byte order")) {
                this.byteOrder = (Integer.parseInt(pair[1]) == 0)
                    ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN;
                setValues |= 0x10;
            }
            /* else continue */
        }

        /* check if all values are set */
        if (setValues != 31) {
            throw new IllegalArgumentException(headerFile
                + " is not valid ENVI header file.");
        }
        // Magic Number on

        header.close();
        this.logger.log(Level.INFO, "ENVI header file " + headerFile
            + " processed successfully.");
    }

    /**
     * Sets byte order.
     *
     * @param byteOrder byte order to set.
     */
    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    /**
     * Iterator method. Reads next pixel from the data file.
     *
     * @return Next pixel from data file.
     */
    public abstract List<D> next();

    /**
     * Iterator method. Checks, if there's any pixel left to read.
     *
     * @return true if there's an unread pixel in the data file,
     * false otherwise.
     */
    public boolean hasNext() {
        return this.position < this.rowsNumber * this.columnsNumber;
    }

    /**
     * Iterator method. Throws an exception, as we do not support deleting
     * pixels from file.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
