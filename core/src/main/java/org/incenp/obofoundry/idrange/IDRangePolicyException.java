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
 * This exception is thrown when attempting to parse an ID range policy file.
 */
public class IDRangePolicyException extends Exception {

    private static final long serialVersionUID = 6707665494789192644L;

    /**
     * Creates a new instance with the specified error message.
     * 
     * @param msg A message describing the error.
     */
    public IDRangePolicyException(String msg) {
        super(msg);
    }

    /**
     * Creates a new instance with the specified error message and cause.
     * 
     * @param msg   A message describing the error.
     * @param inner The underlying cause of the error.
     */
    public IDRangePolicyException(String msg, Throwable inner) {
        super(msg, inner);
    }

}
