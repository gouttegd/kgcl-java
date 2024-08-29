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
public interface IEntityLabelResolver {

    /**
     * Finds the identifier corresponding to the given label.
     * 
     * @param label The label to resolve.
     * @return The identifier of the entity with the given label, or {@code null} if
     *         the label could not be resolved.
     */
    public String resolve(String label);

}
