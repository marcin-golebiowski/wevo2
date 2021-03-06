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

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests for {@link FloatBSQFileReader}.
 *
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public final class FloatBSQFileReaderTest {

    // Magic Number off

    /**
     * Tests whether parsing header file and then reading from data file works.
     *
     * @throws IOException when there's an I/O error.
     */
    @Test
    public void testReadingWithHeader() throws IOException {
        final FloatBSQFileReader reader =
            new FloatBSQFileReader("testData/Float.hdr", "testData/Float.bsq",
                8);
        List<Float> pixel;
        for (int i = 11; i <= 13; i++) {
            Assert.assertEquals(reader.hasNext(), true);
            pixel = reader.next();
            for (int j = 0; j < 3; j++) {
                Assert.assertEquals((float) pixel.get(j), (float) i + j * 10);
            }
        }
        Assert.assertEquals(reader.hasNext(), false);
    }

    /**
     * Tests whether reading from data file works, when there's no
     * header file.
     *
     * @throws IOException when there's an I/O error.
     */
    @Test
    public void testReadingWithoutHeader() throws IOException {
        final FloatBSQFileReader reader =
            new FloatBSQFileReader("testData/Float.bsq", 1, 3, 3, 7, 8);
        reader.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        List<Float> pixel;
        for (int i = 11; i <= 13; i++) {
            Assert.assertEquals(reader.hasNext(), true);
            pixel = reader.next();
            for (int j = 0; j < 3; j++) {
                Assert.assertEquals((float) pixel.get(j), (float) i + j * 10);
            }
        }
        Assert.assertEquals(reader.hasNext(), false);
    }

    // Magic Number on

}
