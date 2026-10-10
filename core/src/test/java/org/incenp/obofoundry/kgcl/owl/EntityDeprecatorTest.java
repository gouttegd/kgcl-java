/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.incenp.linkml.core.LinkMLRuntimeException;
import org.incenp.linkml.ext.ObjectLoader;
import org.incenp.obofoundry.kgcl.TestUtils;
import org.incenp.obofoundry.kgcl.model.DeprecationProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.obolibrary.obo2owl.Obo2OWLConstants.Obo2OWLVocabulary;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

public class EntityDeprecatorTest {

    private static final String PIZZA_BASE = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
    private static final IRI LABEL_IRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();
    private static final IRI SEE_ALSO_IRI = OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI();
    private static final IRI PREFLABEL_IRI = SKOSVocabulary.PREFLABEL.getIRI();
    private static final IRI REPLACED_IRI = Obo2OWLVocabulary.IRI_IAO_0100001.getIRI();
    private static final IRI CONSIDER_IRI = Obo2OWLVocabulary.IRI_OIO_consider.getIRI();

    private OWLOntology ontology;
    private OWLDataFactory factory;
    private TestUtils util;

    @BeforeEach
    void loadOntology() {
        OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
        try {
            ontology = mgr.loadOntologyFromOntologyDocument(new File("src/test/resources/pizza.ofn"));
            factory = mgr.getOWLDataFactory();
            util = new TestUtils(PIZZA_BASE, factory);
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail("Cannot load test ontology");
        }
    }

    @Test
    void testDefaultProfile() {
        EntityDeprecator deprecator = new EntityDeprecator(ontology);
        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");

        deprecator.deprecate(sultana, null, null, removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added axioms (deprecation annotation + new label)
        expectedAdded.add(getAnnotation(LABEL_IRI, sultana, "obsolete SultanaTopping", "en"));
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));

        // Removed annotations
        expectedRemoved.add(getAnnotation(PREFLABEL_IRI, sultana, "Sultana", "en"));
        expectedRemoved.add(getAnnotation(LABEL_IRI, sultana, "CoberturaSultana", "pt"));
        expectedRemoved.add(getAnnotation(LABEL_IRI, sultana, "SultanaTopping", "en"));
        // Removed logical axioms
        expectedRemoved.addAll(getLogicalAxioms());
        // Removed referencing axioms
        expectedRemoved.addAll(getReferencingAxioms(sultana));

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    @Test
    void testBasicProfile() {
        ObjectLoader loader = new ObjectLoader();
        loader.getContext().addPrefix("rdfs", Namespaces.RDFS.getPrefixIRI());
        loader.getContext().addPrefix("skos", Namespaces.SKOS.getPrefixIRI());
        EntityDeprecator deprecator = null;
        try {
            DeprecationProfile basic = loader.loadObject(new File("src/main/resources/deprecation/basic.yaml"),
                    DeprecationProfile.class);
            deprecator = new EntityDeprecator(ontology, basic);
        } catch ( IOException | LinkMLRuntimeException e ) {
            Assertions.fail("Cannot load deprecation profile", e);
        }

        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");
        IRI garlic = util.getIRI("GarlicTopping");

        deprecator.deprecate(sultana, garlic, null, removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added axioms (deprecated annotation + new labels + replacedBy)
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));
        expectedAdded.add(getAnnotation(LABEL_IRI, sultana, "DEPRECATED SultanaTopping", "en"));
        expectedAdded.add(getAnnotation(LABEL_IRI, sultana, "DEPRECATED CoberturaSultana", "pt"));
        expectedAdded.add(getAnnotation(SEE_ALSO_IRI, sultana, garlic));

        // Removed annotations
        expectedRemoved.add(getAnnotation(LABEL_IRI, sultana, "SultanaTopping", "en"));
        expectedRemoved.add(getAnnotation(LABEL_IRI, sultana, "CoberturaSultana", "pt"));
        expectedRemoved.add(getAnnotation(PREFLABEL_IRI, sultana, "Sultana", "en"));
        // Removed logical axioms
        expectedRemoved.addAll(getLogicalAxioms());

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    @Test
    void testDirectRewiring() {
        DeprecationProfile profile = new DeprecationProfile();
        profile.setRewireAxioms(true);
        profile.setReplacedByAnnotationPropertyIri(REPLACED_IRI.toString());
        EntityDeprecator deprecator = new EntityDeprecator(ontology, profile);
        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");
        IRI garlic = util.getIRI("GarlicTopping");

        deprecator.deprecate(sultana, garlic, null, removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added deprecation + replaced by annotations
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));
        expectedAdded.add(getAnnotation(REPLACED_IRI, sultana, garlic));
        // Added rewired axioms
        expectedAdded.addAll(getReferencingAxioms(garlic));
        // Removed referencing axioms
        expectedRemoved.addAll(getReferencingAxioms(sultana));

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    @Test
    void testIndirectRewiring() {
        DeprecationProfile profile = new DeprecationProfile();
        profile.setRewireAxioms(true);
        profile.setAlternateEntityAnnotationPropertyIri(CONSIDER_IRI.toString());
        EntityDeprecator deprecator = new EntityDeprecator(ontology, profile);
        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");
        IRI garlic = util.getIRI("GarlicTopping");

        deprecator.deprecate(sultana, null, Set.of(garlic), removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added deprecation + replaced by annotations
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));
        expectedAdded.add(getAnnotation(CONSIDER_IRI, sultana, garlic));

        // No removed axioms: no rewiring when there is only indirect replacement

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    @Test
    void testRewiringNoReplacement() {
        DeprecationProfile profile = new DeprecationProfile();
        profile.setRewireAxioms(true);
        EntityDeprecator deprecator = new EntityDeprecator(ontology, profile);
        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");

        deprecator.deprecate(sultana, null, null, removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added deprecation axiom
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));

        // Removed referencing axioms
        expectedRemoved.addAll(getReferencingAxioms(sultana));

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    @Test
    void testRemoveLogicalDefinitions() {
        DeprecationProfile profile = new DeprecationProfile();
        profile.setRemoveLogicalDefinition(true);
        EntityDeprecator deprecator = new EntityDeprecator(ontology, profile);
        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");

        deprecator.deprecate(sultana, null, null, removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added deprecation axiom
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));

        // Removed logical axioms
        expectedRemoved.addAll(getLogicalAxioms());

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    @Test
    void testRemoveAnnotations() {
        DeprecationProfile profile = new DeprecationProfile();
        profile.setRemoveAnnotationAssertions(true);
        EntityDeprecator deprecator = new EntityDeprecator(ontology, profile);
        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");

        deprecator.deprecate(sultana, null, null, removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added deprecation axiom
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));

        // Removed skos:prefLabel annotation (rdfs:label should be preserved)
        expectedRemoved.add(getAnnotation(PREFLABEL_IRI, sultana, "Sultana", "en"));

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    @Test
    void testPrefixAnnotations() {
        DeprecationProfile profile = new DeprecationProfile();
        profile.setRemoveAnnotationAssertions(true);
        profile.setAnnotationValuePrefix("obsolete ");
        profile.getPreservedAnnotationAssertionPropertyIris(true).add(PREFLABEL_IRI.toString());
        EntityDeprecator deprecator = new EntityDeprecator(ontology, profile);
        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");

        deprecator.deprecate(sultana, null, null, removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added deprecation axiom
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));

        // Removed skos:prefLabel annotation
        expectedRemoved.add(getAnnotation(PREFLABEL_IRI, sultana, "Sultana", "en"));
        // Added prefixed skos:prefLabel annotation
        expectedAdded.add(getAnnotation(PREFLABEL_IRI, sultana, "obsolete Sultana", "en"));

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    @Test
    void testPreservedAnnotations() {
        DeprecationProfile profile = new DeprecationProfile();
        profile.setRemoveAnnotationAssertions(true);
        profile.getPreservedAnnotationAssertionPropertyIris(true).add(PREFLABEL_IRI.toString());
        EntityDeprecator deprecator = new EntityDeprecator(ontology, profile);
        Set<OWLAxiom> removed = new HashSet<>();
        Set<OWLAxiom> added = new HashSet<>();
        IRI sultana = util.getIRI("SultanaTopping");

        deprecator.deprecate(sultana, null, null, removed, added);

        Set<OWLAxiom> expectedRemoved = new HashSet<>();
        Set<OWLAxiom> expectedAdded = new HashSet<>();

        // Added deprecation axiom
        expectedAdded.add(factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana));

        // Both rdfs:label and skos:prefLabel should be preserved

        util.assertUnsortedIterableEquals(expectedAdded, added);
        util.assertUnsortedIterableEquals(expectedRemoved, removed);
    }

    private OWLAnnotationAssertionAxiom getAnnotation(IRI property, IRI entity, String value, String language) {
        return factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(property), entity,
                factory.getOWLLiteral(value, language));
    }

    private OWLAnnotationAssertionAxiom getAnnotation(IRI property, IRI entity, IRI value) {
        return factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(property), entity, value);
    }

    // Get the logical axioms for the SultanaTopping class
    private Set<OWLAxiom> getLogicalAxioms() {
        Set<OWLAxiom> axioms = new HashSet<>();
        OWLClass klass = util.getKlass("SultanaTopping");
        axioms.add(factory.getOWLSubClassOfAxiom(klass, util.getKlass("FruitTopping")));
        axioms.add(factory.getOWLSubClassOfAxiom(klass,
                factory.getOWLObjectSomeValuesFrom(util.getObjectProperty("hasSpiciness"), util.getKlass("Medium"))));
        return axioms;
    }

    // Get the referencing axioms for the SultanaTopping class, optionally rewritten
    // with the specified IRI
    private Set<OWLAxiom> getReferencingAxioms(IRI subject) {
        Set<OWLAxiom> axioms = new HashSet<>();

        // Veneziana SubClassOf: hasTopping some <subject>
        axioms.add(factory.getOWLSubClassOfAxiom(util.getKlass("Veneziana"),
                factory.getOWLObjectSomeValuesFrom(util.getObjectProperty("hasTopping"),
                        factory.getOWLClass(subject))));

        // Veneziana SubClassOf: hasTopping only (<subject> or ...)
        Set<OWLClassExpression> exprs = new HashSet<>();
        exprs.add(factory.getOWLClass(subject));
        exprs.add(util.getKlass("CaperTopping"));
        exprs.add(util.getKlass("MozzarellaTopping"));
        exprs.add(util.getKlass("OliveTopping"));
        exprs.add(util.getKlass("OnionTopping"));
        exprs.add(util.getKlass("PineKernels"));
        exprs.add(util.getKlass("TomatoTopping"));
        axioms.add(factory.getOWLSubClassOfAxiom(util.getKlass("Veneziana"),
                factory.getOWLObjectAllValuesFrom(util.getObjectProperty("hasTopping"),
                        factory.getOWLObjectUnionOf(exprs))));

        return axioms;
    }
}
