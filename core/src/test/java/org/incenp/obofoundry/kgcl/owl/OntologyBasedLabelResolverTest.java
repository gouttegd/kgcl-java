/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2024 Damien Goutte-Gattat
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   (1) Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   (2) Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 *   (3) Neither the name of the copyright holder nor the names its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
    void loadOntology() {
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
