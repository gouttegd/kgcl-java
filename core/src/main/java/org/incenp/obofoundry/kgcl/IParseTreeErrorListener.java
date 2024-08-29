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

package org.incenp.obofoundry.kgcl;

/**
 * An object that listens to errors that may occur when processing the parse
 * tree of a KGCL changeset. Such errors may be cause by a KGCL instruction that
 * is syntactically valid (so that the ANTLR parser could successfully turn it
 * into a parse tree) but logically incorrect.
 */
public interface IParseTreeErrorListener {

    /**
     * Called upon encountering a parse tree error.
     * 
     * @param line    The line where the error occured.
     * @param column  The position in the line where the error occured.
     * @param message The error message.
     */
    public void parseTreeError(int line, int column, String message);

}
