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

import java.util.HashSet;
import java.util.Random;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Generates numerical IDs within a given range.
 */
public class SimpleIDGenerator implements IAutoIDGenerator {

    private OWLOntology ontology;
    private String format;
    private int lowerBound;
    private int upperBound;
    private Random rand = new Random();
    private HashSet<String> testedIDs = new HashSet<String>();

    /**
     * Creates a new instance.
     * 
     * @param ontology The ontology to generate IDs for. Its contents will be
     *                 checked to ensure the generated IDs do not clash with
     *                 existing entities.
     * @param format   The String used to format the newly generated IDs. It must
     *                 contain a C-style format specifier indicating where the
     *                 numerical portion of the ID should appear and in which
     *                 format.
     * @param min      The lower bound for newly generated IDs.
     * @param max      The upper bound for newly generated IDs.
     */
    public SimpleIDGenerator(OWLOntology ontology, String format, int min, int max) {
        this.ontology = ontology;
        this.format = format;

        boolean found = false;
        do {
            String test = String.format(format, min);
            if ( !exists(test) ) {
                found = true;
            } else {
                min += 1;
            }
        } while ( !found && min < max );

        lowerBound = min;
        upperBound = max;
    }

    @Override
    public String nextID() {
        boolean found = false;
        int i = lowerBound;
        String id = null;
        do {
            i += rand.nextInt(100);
            id = String.format(format, i);
            found = !exists(id);
        } while ( i < upperBound && !found );

        if ( found ) {
            testedIDs.add(id);
        }

        return id;
    }

    private boolean exists(String id) {
        if ( testedIDs.contains(id) ) {
            return true;
        } else if ( ontology.containsEntityInSignature(IRI.create(id)) ) {
            testedIDs.add(id);
            return true;
        } else {
            return false;
        }
    }

}
