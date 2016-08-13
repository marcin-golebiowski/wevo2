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

import junit.framework.Assert;

import org.testng.annotations.Test;

/**
 * Tests for {@link SatellitePhotoCategory}.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class SatellitePhotoCategoryTest {

    /** Test toString method. */
    @Test
    public void testToString() {
      SatellitePhotoCategory cat = 
          new SatellitePhotoCategory(Color.black, "name");
      assertEquals("name (" + Color.black.toString() + ")", cat.toString());
    }

    /** Test equals method. */
    @Test
    public void testEquals() {
      SatellitePhotoCategory cat = 
          new SatellitePhotoCategory(Color.black, "name");
      SatellitePhotoCategory cat1 = 
          new SatellitePhotoCategory(Color.white, "name_a");
      SatellitePhotoCategory cat2 = 
          new SatellitePhotoCategory(Color.white, "name");
      SatellitePhotoCategory cat3 = 
          new SatellitePhotoCategory(Color.black, "name_a");
      SatellitePhotoCategory cat4 = 
          new SatellitePhotoCategory(Color.black, "name");
      SatellitePhotoCategory cat5 = 
          new SatellitePhotoCategory(null, "name");
      SatellitePhotoCategory cat6 = 
          new SatellitePhotoCategory(Color.black, null);
      SatellitePhotoCategory cat7 = 
          new SatellitePhotoCategory(null, null);

      assertEquals(false, cat.equals(null));
      assertEquals(false, cat.equals(cat1));
      assertEquals(false, cat.equals(cat2));
      assertEquals(false, cat.equals(cat3));
      assertEquals(true, cat.equals(cat4));
      assertEquals(false, cat.equals(cat5));
      assertEquals(false, cat.equals(cat6));
      assertEquals(false, cat.equals(cat7));
      assertEquals(false, cat5.equals(cat));
      assertEquals(false, cat6.equals(cat));
      assertEquals(false, cat7.equals(cat));
    }

    /** Test hashCode method. */
    @Test
    public void testHashCode() {
        SatellitePhotoCategory cat1 = 
            new SatellitePhotoCategory(Color.white, "name");
        SatellitePhotoCategory cat2 = 
            new SatellitePhotoCategory(Color.white, "name");

        Assert.assertEquals(cat1.hashCode(), cat2.hashCode());
        Assert.assertTrue(cat1.equals(cat2));
    }
}
