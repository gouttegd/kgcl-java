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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class SequentialIDGeneratorTest {

    private OWLOntology ontology;

    @BeforeEach
    private void prepareOntology() {
        OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();

        try {
            ontology = mgr.createOntology();
        } catch ( OWLOntologyCreationException e ) {
            e.printStackTrace();
        }
    }

    @Test
    void testGenerateIDWithinRange() {
        IAutoIDGenerator gen = new SequentialIDGenerator(ontology, "https://example.org/%07d", 1000, 2000);

        for ( int i = 0; i < 10; i++ ) {
            String id = gen.nextID();
            Assertions.assertEquals(String.format("https://example.org/%07d", 1000 + i), id);
        }
    }

    @Test
    void testAvoidUsedLowerIDs() {
        OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        for ( int i = 1000; i < 1100; i++ ) {
            String id = String.format("https://example.org/%07d", i);
            ontology.getOWLOntologyManager().addAxiom(ontology,
                    factory.getOWLDeclarationAxiom(factory.getOWLClass(IRI.create(id))));
        }

        IAutoIDGenerator gen = new SequentialIDGenerator(ontology, "https://example.org/%07d", 1000, 2000);

        for ( int i = 0; i < 10; i++ ) {
            String id = gen.nextID();
            Assertions.assertEquals(String.format("https://example.org/%07d", 1100 + i), id);
        }
    }

    @Test
    void testAvoidUsingMidRangeIDs() {
        OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        ontology.getOWLOntologyManager().addAxiom(ontology,
                factory.getOWLDeclarationAxiom(factory.getOWLClass(IRI.create("https://example.org/0001005"))));

        IAutoIDGenerator gen = new SequentialIDGenerator(ontology, "https://example.org/%07d", 1000, 2000);

        for ( int i = 0; i < 10; i++ ) {
            String id = gen.nextID();
            int expected = i < 5 ? i : i + 1;
            Assertions.assertEquals(String.format("https://example.org/%07d", 1000 + expected), id);
        }
    }

    @Test
    void testFailUponHittingUpperBound() {
        IAutoIDGenerator gen = new SequentialIDGenerator(ontology, "https://example.org/%07d", 1000, 1005);

        for ( int i = 0; i < 10; i++ ) {
            String id = gen.nextID();
            if ( i < 5 ) {
                Assertions.assertEquals(String.format("https://example.org/%07d", 1000 + i), id);
            } else {
                Assertions.assertNull(id);
            }
        }
    }

}
