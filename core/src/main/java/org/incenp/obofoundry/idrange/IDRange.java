/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2024 Damien Goutte-Gattat
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the Gnu General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.incenp.obofoundry.idrange;

/**
 * Represents an ID range for automatically assigned IDs.
 */
public class IDRange {

    private int lowerBound;

    private int upperBound;

    /**
     * Creates a new instance.
     * 
     * @param lower The lower bound (inclusive) of the range.
     * @param upper The upper bound (exclusive) of the range.
     */
    public IDRange(int lower, int upper) {
        if ( lower < 0 || upper <= lower ) {
            throw new IllegalArgumentException("Invalid ID range");
        }

        lowerBound = lower;
        upperBound = upper;
    }

    /**
     * Gets the lower bound (inclusive) of the range.
     * 
     * @return The range's lower bound.
     */
    public int getLowerBound() {
        return lowerBound;
    }

    /**
     * Gets the upper bound (exclusive) of the range,
     * 
     * @return The range's upper bound.
     */
    public int getUpperBound() {
        return upperBound;
    }
}
