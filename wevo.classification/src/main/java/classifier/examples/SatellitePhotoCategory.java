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

import java.awt.Color;

/**
 * This class describes single category in satellite photos
 * classification problem. Category is defined by the color
 * of pixel in expert classification data.
 * @author Lukasz Krawiec (lmkrawiec@gmail.com)
 * @author Michal Anglart (anglart.michal@gmail.com)
 */
public class SatellitePhotoCategory {

    /** Color which represents this category in learning set. */
    private final Color categoryColor;

    /** Name of category. */
    private final String categoryName;

    /**
     * Standard constructor.
     * @param categoryColor Color of category in learning set.
     * @param categoryName Category name.
     */
    public SatellitePhotoCategory(Color categoryColor, String categoryName) {
      this.categoryColor = categoryColor;
      this.categoryName = categoryName;
    }

    /**
     * Standard constructor. Initializes name as "undefined".
     * @param categoryColor Color of category in learning set.
     */
    public SatellitePhotoCategory(Color categoryColor) {
      this(categoryColor, "undefined");
    }

    /**
     * Gets category color.
     * @return Color of this category.
     */
    public Color getCategoryColor() {
      return categoryColor;
    }

    /**
     * Gets category name.
     * @return Name of this category.
     */
    public String getCategoryName() {
      return categoryName;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
      return categoryName + " (" + categoryColor.toString() + ")";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
      if (obj instanceof SatellitePhotoCategory) {
        SatellitePhotoCategory that = (SatellitePhotoCategory) obj;
        return categoryColor != null && categoryColor.equals(that.categoryColor)
            && categoryName != null && categoryName.equals(that.categoryName);
      }
      return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      // Eclipse auto-generated method.
      final int prime = 31;
      int result = 1;
      result = prime * result
          + ((categoryColor == null) ? 0 : categoryColor.hashCode());
      result = prime * result
          + ((categoryName == null) ? 0 : categoryName.hashCode());
      return result;
    }
}
