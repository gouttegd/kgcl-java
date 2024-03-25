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

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Generates numerical IDs sequentially within a given range.
 */
public class SequentialIDGenerator implements IAutoIDGenerator {

    protected OWLOntology ontology;
    protected String format;
    protected int lowerBound;
    protected int upperBound;

    /**
     * Creates a new instance.
     * 
     * @param ontology The ontology to generate IDs for. Its contents will be
     *                 checked to ensure the generated IDs do not clash with
     *                 existing entities.
     * @param format   The format of newly generated IDs. It must contain a C-style
     *                 format specifier indicating where the numerical portion of
     *                 the ID should appear and in which format.
     * @param min      The lower bound (inclusive) for newly generated IDs.
     * @param max      The upper bound (exclusive) for newly generated IDs.
     */
    public SequentialIDGenerator(OWLOntology ontology, String format, int min, int max) {
        this.ontology = ontology;
        this.format = format;
        lowerBound = min;
        upperBound = max;
    }

    @Override
    public String nextID() {
        boolean found = false;
        String id = null;
        while ( lowerBound < upperBound && !found ) {
            id = String.format(format, lowerBound++);
            found = !ontology.containsEntityInSignature(IRI.create(id));
        }

        return found ? id : null;
    }
}
