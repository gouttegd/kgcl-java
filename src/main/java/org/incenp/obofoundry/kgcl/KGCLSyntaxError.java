/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023 Damien Goutte-Gattat
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

package org.incenp.obofoundry.kgcl;

/**
 * A syntax error encountered when parsing a KGCL program.
 */
public class KGCLSyntaxError {
    private int line;
    private int position;
    private String message;

    /**
     * Create a new instance.
     * 
     * @param line     The line number where the error occurred.
     * @param position The position in the line where the error occurred.
     * @param message  The error message from the ANTLR parser.
     */
    public KGCLSyntaxError(int line, int position, String message) {
        this.line = line;
        this.position = position;
        this.message = message;
    }

    /**
     * Get the line where the error occurred.
     * 
     * @return 1-based index of the offending line, starting from the beginning of
     *         the input.
     */
    public int getLine() {
        return line;
    }

    /**
     * Get the position in the line where the error occured.
     * 
     * @return 0-based index of the offending character, starting from the beginning
     *         of the line.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Get the error message.
     * 
     * @return The error message from the ANTLR parser.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("line %d, column %d: %s", line, position, message);
    }
}
