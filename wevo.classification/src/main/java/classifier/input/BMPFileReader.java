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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;


/**
 * Class for reading bmp files.
 * 
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public final class BMPFileReader {

    /** Private constructor to prevent object construction. */
    private BMPFileReader() { }

    /**
     * Reads bitmap file and returns list of pixels.
     * @param filename Name of bitmap file.
     * @return List of pixels.
     * @throws IOException Thrown on file reading problems.
     * @throws InterruptedException Another thread has 
     *     interrupted grabber thread.
     */
    public static List<Color> readBitmapFile(String filename)
        throws IOException, InterruptedException {
      File myImageFile = new File(filename);
      BufferedImage image = ImageIO.read(myImageFile);

      int[] pixs = new int[image.getWidth() * image.getHeight()];
      List<Color> pixels = new ArrayList<Color>();

      PixelGrabber grabber = new PixelGrabber(image, 0, 0, image.getWidth(),
            image.getHeight(), pixs, 0, image.getWidth());
      grabber.grabPixels(0);

      for (int i = 0; i < pixs.length; i++) {
        pixels.add(handlePixel(pixs[i]));
      }

      return pixels;
    }

    /** 
     * Handle single pixel. Reads integer representations of pixel
     * in ARGB format and converts it to color object. This method discards
     * alpha channel.
     * @param pixel Pixel represented as integer.
     * @return Color of given pixel
     */
    private static Color handlePixel(int pixel) {
      // Magic Number off
      int red = (pixel >> 16) & 0xff;
      int green = (pixel >>  8) & 0xff;
      int blue = pixel & 0xff;
      // Magic Number on

      return new Color(red, green, blue);
     }
}
