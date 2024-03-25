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
 * Generates numerical IDs within a given a range. This class is similar to
 * {@link SequentialIDGenerator} except that numerical IDs are chosen randomly
 * within the target range, instead of being chosen sequentially.
 */
public class RandomizedIDGenerator implements IAutoIDGenerator {

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
     * @param format   The format of newly generated IDs. It must contain a C-style
     *                 format specified indicating where the numerical portion of
     *                 the ID should appear and in which format.
     * @param min      The lower bound (inclusive) for newly generated IDs.
     * @param max      The upper bound (exclusive) for newly generated IDs.
     */
    public RandomizedIDGenerator(OWLOntology ontology, String format, int min, int max) {
        this.ontology = ontology;
        this.format = format;

        boolean found = false;
        do {
            String test = String.format(format, min);
            if ( !exists(test, false) ) {
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
            id = String.format(format,i);
            found = !exists(id, true);
        } while ( i < upperBound && !found );

        return found ? id : null;
    }

    private boolean exists(String id, boolean add) {
        if (testedIDs.contains(id)) {
            return true;
        } else if ( ontology.containsEntityInSignature(IRI.create(id)) ) {
            testedIDs.add(id);
            return true;
        } else if (add) {
            testedIDs.add(id);
        }
        return false;
    }
}
