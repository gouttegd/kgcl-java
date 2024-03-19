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

package org.incenp.obofoundry.kgcl.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the type of a node, when the knowledge graph is backed by an OWL
 * ontology.
 */
public enum OwlType {
    CLASS("class"),
    OBJECT_PROPERTY("relation"),
    NAMED_INVIDIDUAL("instance"),
    ANNOTATION_PROPERTY("annotation property");

    private final static Map<String, OwlType> MAP;

    static {
        Map<String, OwlType> map = new HashMap<String, OwlType>();
        for ( OwlType value : OwlType.values() ) {
            map.put(value.toString(), value);
        }

        MAP = Collections.unmodifiableMap(map);
    }

    private final String repr;

    OwlType(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }

    /**
     * Parses a string into a OwlType enum value.
     * 
     * @param v The string to parse.
     * @return The corresponding enumeration value, or {@code null} if the string
     *         does not match any allowed value.
     */
    public static OwlType fromString(String v) {
        return MAP.get(v);
    }
}
