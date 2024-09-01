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

import java.util.HashMap;
import java.util.UUID;

/**
 * A basic implementation of the {@link ILabelResolver} interface.
 * <p>
 * This implementation is backed up by a simple dictionary mapping labels to
 * their corresponding identifiers. When a new ID is requested (with
 * {@link #getNewId(String)}), it mints a temporary ID suitable for use with the
 * {@link org.incenp.obofoundry.kgcl.AutoIDAllocator} class.
 */
public class SimpleLabelResolver implements ILabelResolver {

    private HashMap<String, String> idMap = new HashMap<String, String>();

    @Override
    public String resolve(String label) {
        return idMap.get(label);
    }

    @Override
    public String getNewId(String label) {
        String newId = AutoIDAllocator.AUTOID_BASE_IRI + UUID.randomUUID().toString();
        idMap.put(label, newId);
        return newId;
    }

    @Override
    public void add(String label, String identifier) {
        idMap.put(label, identifier);
    }

}
