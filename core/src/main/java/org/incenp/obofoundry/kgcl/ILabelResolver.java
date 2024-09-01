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
 * An object that can resolve labels into proper entity identifiers.
 */
public interface ILabelResolver {

    /**
     * Finds the identifier corresponding to the given label.
     * 
     * @param label The label to resolve.
     * @return The identifier of the entity with the given label, or {@code null} if
     *         the label could not be resolved.
     */
    public String resolve(String label);

    /**
     * Registers a new label-to-identifier mapping.
     * 
     * @param label      The label to register.
     * @param identifier Its corresponding identifier.
     */
    public void add(String label, String identifier);

    /**
     * Mints a new identifier for the given label.
     * <p>
     * This method is used when a KGCL “create” instruction does not include an
     * identifier for the node to be created (e.g.,
     * {@code create class 'my new class'}). It shall return a new identifier for
     * the node to be created.
     * <p>
     * Any subsequent call to {@link #resolve(String)} with the same label shall
     * return the same identifier.
     * 
     * @param label The label for which an identifier is requested.
     * @return The newly minted identifier.
     */
    public String getNewId(String label);
}
