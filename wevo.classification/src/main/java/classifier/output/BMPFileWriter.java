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
package classifier.output;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
 
/**
 * Class for writing BMP files.
 * 
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public final class BMPFileWriter {

    /** Private constructor to prevent object construction. */
    private BMPFileWriter() { }

    /**
     * Writes bitmap file.
     * @param filename Name of file for writing.
     * @param width Image width.
     * @param height Image height.
     * @param pixels List of pixels for writing.
     * @throws IOException Thrown on file writing problems.
     */
    public static void writeBitmapFile(String filename, int width,
            int height, List<Color> pixels) throws IOException {
      BufferedImage image = 
          new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
 
      int total = 0;
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          image.setRGB(j, i, pixels.get(total).getRGB());
          total++;
        }
      }
      ImageIO.write(image, "BMP", new File(filename));
    }
}
