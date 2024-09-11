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

package org.incenp.obofoundry.kgcl.owl;

import java.io.File;

import org.incenp.obofoundry.kgcl.SimpleLabelResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.obolibrary.obo2owl.Obo2OWLConstants;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class OntologyBasedLabelResolverTest {

    private OWLOntology ontology;

    @BeforeEach
    private void loadOntology() {
        OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
        try {
            ontology = mgr.loadOntologyFromOntologyDocument(new File("src/test/resources/pizza.ofn"));
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail("Cannot load test ontology");
        }
    }

    @Test
    void testResolveLabels() {
        SimpleLabelResolver resolver = new OntologyBasedLabelResolver(ontology);

        Assertions.assertEquals("http://www.co-ode.org/ontologies/pizza/pizza.owl#SultanaTopping",
                resolver.resolve("SultanaTopping"));
        Assertions.assertEquals("http://www.co-ode.org/ontologies/pizza/pizza.owl#LaReine",
                resolver.resolve("LaReine"));

        Assertions.assertNull(resolver.resolve("Unknown label"));
    }

    @Test
    void testResolveOBOShorthands() {
        // The Pizza ontology does not use OBO shorthands, so we inject one.
        OWLOntologyManager mgr = ontology.getOWLOntologyManager();
        OWLDataFactory factory = mgr.getOWLDataFactory();
        mgr.addAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(
                        factory.getOWLAnnotationProperty(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_shorthand.getIRI()),
                        IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#hasBase"),
                        factory.getOWLLiteral("has_base")));

        SimpleLabelResolver resolver = new OntologyBasedLabelResolver(ontology);

        Assertions.assertEquals("http://www.co-ode.org/ontologies/pizza/pizza.owl#hasBase",
                resolver.resolve("has_base"));
    }

    @Test
    void testResolveSpecialShorthands() {
        // Check that "is_a" resolves into rdfs:subClassOf
        SimpleLabelResolver resolver = new OntologyBasedLabelResolver(ontology);
        Assertions.assertEquals(OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI().toString(), resolver.resolve("is_a"));

        // Unless a class with a "is_a" label exists in the ontology
        OWLOntologyManager mgr = ontology.getOWLOntologyManager();
        OWLDataFactory factory = mgr.getOWLDataFactory();
        IRI dummy = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#dummy");
        mgr.addAxiom(ontology, factory.getOWLDeclarationAxiom(factory.getOWLClass(dummy)));
        mgr.addAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(
                        factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), dummy,
                        factory.getOWLLiteral("is_a")));
        resolver = new OntologyBasedLabelResolver(ontology);
        Assertions.assertEquals(dummy.toString(), resolver.resolve("is_a"));
    }
}
