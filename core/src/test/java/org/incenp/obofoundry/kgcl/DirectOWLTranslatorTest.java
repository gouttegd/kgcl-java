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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.AddNodeToSubset;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationChange;
import org.incenp.obofoundry.kgcl.model.NodeChange;
import org.incenp.obofoundry.kgcl.model.NodeDeepening;
import org.incenp.obofoundry.kgcl.model.NodeDeletion;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.NodeShallowing;
import org.incenp.obofoundry.kgcl.model.NodeUnobsoletion;
import org.incenp.obofoundry.kgcl.model.OntologySubset;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveNodeFromSubset;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.obolibrary.obo2owl.Obo2OWLConstants.Obo2OWLVocabulary;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;

public class DirectOWLTranslatorTest implements RejectedChangeListener {

    private static final String PIZZA_BASE = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";
    private static final IRI LABEL_IRI = OWLRDFVocabulary.RDFS_LABEL.getIRI();
    private static final IRI SUBCLASSOF_IRI = OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI();
    private static final IRI PREFLABEL_IRI = SKOSVocabulary.PREFLABEL.getIRI();
    private static final IRI DEFINITION_IRI = Obo2OWLVocabulary.IRI_IAO_0000115.getIRI();
    private static final IRI EXACT_SYN_IRI = Obo2OWLVocabulary.IRI_OIO_hasExactSynonym.getIRI();
    private static final IRI NARROW_SYN_IRI = Obo2OWLVocabulary.IRI_OIO_hasNarrowSynonym.getIRI();
    private static final IRI BROAD_SYN_IRI = Obo2OWLVocabulary.IRI_OIO_hasBroadSynonym.getIRI();
    private static final IRI RELATED_SYN_IRI = Obo2OWLVocabulary.IRI_OIO_hasRelatedSynonym.getIRI();
    private static final IRI CONSIDER_IRI = Obo2OWLVocabulary.IRI_OIO_consider.getIRI();
    private static final IRI REPLACED_IRI = Obo2OWLVocabulary.IRI_IAO_0100001.getIRI();
    private static final IRI IN_SUBSET_IRI = IRI.create("http://www.geneontology.org/formats/oboInOwl#inSubset");
    private static final TestUtils util = new TestUtils(PIZZA_BASE);

    private OWLOntology ontology;
    private OWLDataFactory factory;
    private ArrayList<String> actualRejections = new ArrayList<String>();

    @BeforeEach
    private void loadOntology() {
        OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
        try {
            ontology = mgr.loadOntologyFromOntologyDocument(new File("src/test/resources/pizza.ofn"));
            factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail("Cannot load test ontology");
        }
    }

    @Override
    public void rejected(Change change, String reason) {
        actualRejections.add(reason);
    }

    @Test
    void testNodeRename() {
        NodeRename change = new NodeRename();
        setAboutNode(change, "LaReine");
        setValue(change, "LaReine", "en", true);
        setValue(change, "TheQueen", "en");

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "LaReine", "LaReine", "en")));
        expected.add(new AddAxiom(ontology, getAnnotation(LABEL_IRI, "LaReine", "TheQueen", "en")));

        testChange(change, expected, null);
    }

    @Test
    void testRejectedNodeRename() {
        NodeRename change = new NodeRename();
        setAboutNode(change, "LaReine");
        setValue(change, "La Reine", "en", true);
        setValue(change, "TheQueen", "en");

        testChange(change, null, "Label \"La Reine\" not found on <" + PIZZA_BASE + "LaReine>");
    }

    @Test
    void testNewSynonym() {
        String[] qualifiers = { "exact", "narrow", "broad", "related", "unknown", null };
        IRI[] properties = { EXACT_SYN_IRI, NARROW_SYN_IRI, BROAD_SYN_IRI, RELATED_SYN_IRI, EXACT_SYN_IRI,
                EXACT_SYN_IRI };

        NewSynonym change = new NewSynonym();
        setAboutNode(change, "LaReine");
        setValue(change, "TheQueen", "en");

        for ( int i = 0; i < qualifiers.length; i++ ) {
            change.setQualifier(qualifiers[i]);
            testChange(change, new AddAxiom(ontology, getAnnotation(properties[i], "LaReine", "TheQueen", "en")));
        }
    }

    @Test
    void testNewSynonymOnInexistingClass() {
        NewSynonym change = new NewSynonym();
        setAboutNode(change, "lareine");
        setValue(change, "TheQueen", "en");

        testChange(change, null, "Node <" + PIZZA_BASE + "lareine> not found in signature");
    }

    @Test
    void testNewSynonymAlreadyExists() {
        // The Pizza ontology contains no synonym annotations, so we need to create one
        OWLAxiom ax = getAnnotation(EXACT_SYN_IRI, "LaReine", "The Queen", "en");
        ontology.getOWLOntologyManager().addAxiom(ontology, ax);

        NewSynonym change = new NewSynonym();
        setAboutNode(change, "LaReine");
        setValue(change, "The Queen", "en");

        testChange(change, null, null);
    }

    @Test
    void testRemoveSynonym() {
        // Create a synonym to remove
        OWLAxiom ax = getAnnotation(EXACT_SYN_IRI, "LaReine", "The Queen", "en");
        ontology.getOWLOntologyManager().addAxiom(ontology, ax);

        RemoveSynonym change = new RemoveSynonym();
        setAboutNode(change, "LaReine");
        setValue(change, "The Queen", "en", true);

        testChange(change, new RemoveAxiom(ontology, ax));
    }

    @Test
    void testRemoveInexistingSynonym() {
        RemoveSynonym change = new RemoveSynonym();
        setAboutNode(change, "LaReine");
        setValue(change, "The Queen", "en", true);

        testChange(change, null, "Synonym \"The Queen\" not found on <" + PIZZA_BASE + "LaReine>");
    }

    @Test
    void testReplaceSynonym() {
        // Create a synonym to replace
        OWLAxiom ax = getAnnotation(EXACT_SYN_IRI, "LaReine", "The Queen", "en");
        ontology.getOWLOntologyManager().addAxiom(ontology, ax);

        SynonymReplacement change = new SynonymReplacement();
        setAboutNode(change, "LaReine");
        setValue(change, "The Queen", "en", true);
        setValue(change, "TheQueen", "en");

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(new RemoveAxiom(ontology, ax));
        expected.add(new AddAxiom(ontology, getAnnotation(EXACT_SYN_IRI, "LaReine", "TheQueen", "en")));

        testChange(change, expected, null);
    }

    @Test
    void testReplaceInexistingSynonym() {
        SynonymReplacement change = new SynonymReplacement();
        setAboutNode(change, "LaReine");
        setValue(change, "The Queen", "en", true);
        setValue(change, "TheQueen", "en");

        testChange(change, null, "Synonym \"The Queen\" not found on <" + PIZZA_BASE + "LaReine>");
    }

    @Test
    void testNewDefinition() {
        NewTextDefinition change = new NewTextDefinition();
        setAboutNode(change, "LaReine");
        setValue(change, "The queen of pizzas.", "en");

        testChange(change,
                new AddAxiom(ontology, getAnnotation(DEFINITION_IRI, "LaReine", "The queen of pizzas.", "en")));
    }

    @Test
    void testNewDefinitionAlreadyExists() {
        // The pizza ontology does contain definition, but they use skos:definition
        // rather than IAO:0000115, so we need to create a IAO:0000115 definition.
        OWLAxiom ax = getAnnotation(DEFINITION_IRI, "LaReine", "The queen of pizzas.", "en");
        ontology.getOWLOntologyManager().addAxiom(ontology, ax);

        NewTextDefinition change = new NewTextDefinition();
        setAboutNode(change, "LaReine");
        setValue(change, "The queen of pizzas.", "en");

        testChange(change, null, null);
    }

    @Test
    void testRemoveDefinition() {
        // Create a definition to remove
        OWLAxiom ax = getAnnotation(DEFINITION_IRI, "LaReine", "The queen of pizzas.", "en");
        ontology.getOWLOntologyManager().addAxiom(ontology, ax);

        RemoveTextDefinition change = new RemoveTextDefinition();
        setAboutNode(change, "LaReine");
        setValue(change, "The queen of pizzas.", "en", true);

        testChange(change, new RemoveAxiom(ontology, ax));
    }

    @Test
    void testRemoveInexistingDefinition() {
        RemoveTextDefinition change = new RemoveTextDefinition();
        setAboutNode(change, "LaReine");
        setValue(change, "The queen of pizzas.", "en", true);

        testChange(change, null, "Definition not found on <" + PIZZA_BASE + "LaReine>");
    }

    @Test
    void testReplaceDefinition() {
        // Create a definition to replace
        OWLAxiom ax = getAnnotation(DEFINITION_IRI, "LaReine", "The queen of pizzas.", "en");
        ontology.getOWLOntologyManager().addAxiom(ontology, ax);

        TextDefinitionReplacement change = new TextDefinitionReplacement();
        setAboutNode(change, "LaReine");
        setValue(change, "The queen of pizzas.", "en", true);
        setValue(change, "The true queen of pizzas.", "en");

        testChange(change, new RemoveAxiom(ontology, ax),
                new AddAxiom(ontology, getAnnotation(DEFINITION_IRI, "LaReine", "The true queen of pizzas.", "en")));
    }

    @Test
    void testReplaceInexistingDefinition() {
        TextDefinitionReplacement change = new TextDefinitionReplacement();
        setAboutNode(change, "LaReine");
        setValue(change, "The queen of pizzas.", "en", true);
        setValue(change, "The true queen of pizzas.", "en");

        testChange(change, null, "Definition not found on <" + PIZZA_BASE + "LaReine>");
    }

    @Test
    void testSimpleObsoletion() {
        NodeObsoletion change = new NodeObsoletion();
        setAboutNode(change, "SultanaTopping");

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        OWLClass sultana = getKlass("SultanaTopping");
        OWLClass veneziana = getKlass("Veneziana");
        OWLObjectProperty hasTopping = getObjectProperty("hasTopping");

        // Added axioms (deprecation annotation + new label)
        expected.add(
                new AddAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "obsolete SultanaTopping", "en")));
        expected.add(new AddAxiom(ontology, factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana.getIRI())));

        // Removed annotations
        expected.add(new RemoveAxiom(ontology, getAnnotation(PREFLABEL_IRI, "SultanaTopping", "Sultana", "en")));
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "CoberturaSultana", "pt")));
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "SultanaTopping", "en")));

        // Removed subClassOf axioms on SultanaTopping itself
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(sultana, getKlass("FruitTopping"))));
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(sultana,
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasSpiciness"), getKlass("Medium")))));

        // Removed referencing axioms from Veneziana class:
        // 1. Veneziana SubClassOf: hasTopping some SultanaTopping
        expected.add(new RemoveAxiom(ontology,
                factory.getOWLSubClassOfAxiom(veneziana, factory.getOWLObjectSomeValuesFrom(hasTopping, sultana))));

        // 2. Veneziana SubClassOf: hasTopping only (SultanaTopping or ...)
        HashSet<OWLClassExpression> exprs = new HashSet<OWLClassExpression>();
        exprs.add(sultana);
        exprs.add(getKlass("CaperTopping"));
        exprs.add(getKlass("MozzarellaTopping"));
        exprs.add(getKlass("OliveTopping"));
        exprs.add(getKlass("OnionTopping"));
        exprs.add(getKlass("PineKernels"));
        exprs.add(getKlass("TomatoTopping"));
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(veneziana,
                factory.getOWLObjectAllValuesFrom(hasTopping, factory.getOWLObjectUnionOf(exprs)))));

        testChange(change, expected, null);
    }

    @Test
    void testObsoletionWithNoDirectReplacement() {
        NodeObsoletionWithNoDirectReplacement change = new NodeObsoletionWithNoDirectReplacement();
        setAboutNode(change, "SultanaTopping");
        change.setHasNondirectReplacement(new ArrayList<Node>());
        Node replacement = new Node();
        replacement.setId(PIZZA_BASE + "GarlicTopping");
        change.getHasNondirectReplacement().add(replacement);

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        OWLClass sultana = getKlass("SultanaTopping");
        OWLClass garlic = getKlass("GarlicTopping");

        // Added axioms (deprecation annotation, new label, consider)
        expected.add(
                new AddAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "obsolete SultanaTopping", "en")));
        expected.add(new AddAxiom(ontology, factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana.getIRI())));
        expected.add(new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(CONSIDER_IRI), sultana.getIRI(), garlic.getIRI())));

        // Removed annotations
        expected.add(new RemoveAxiom(ontology, getAnnotation(PREFLABEL_IRI, "SultanaTopping", "Sultana", "en")));
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "CoberturaSultana", "pt")));
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "SultanaTopping", "en")));

        // Removed subClassOf axioms on SultanaTopping itself
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(sultana, getKlass("FruitTopping"))));
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(sultana,
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasSpiciness"), getKlass("Medium")))));

        testChange(change, expected, null);
    }

    @Test
    void testObsoletionWithDirectReplacement() {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();
        setAboutNode(change, "SultanaTopping");
        Node replacement = new Node();
        replacement.setId(PIZZA_BASE + "GarlicTopping");
        change.setHasDirectReplacement(replacement);

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        OWLClass sultana = getKlass("SultanaTopping");
        OWLClass garlic = getKlass("GarlicTopping");
        OWLClass veneziana = getKlass("Veneziana");
        OWLObjectProperty hasSpiciness = getObjectProperty("hasSpiciness");
        OWLObjectProperty hasTopping = getObjectProperty("hasTopping");

        // Added axioms (deprecation annotation, new label, replaced_by)
        expected.add(
                new AddAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "obsolete SultanaTopping", "en")));
        expected.add(new AddAxiom(ontology, factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana.getIRI())));
        expected.add(new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(REPLACED_IRI), sultana.getIRI(), garlic.getIRI())));

        // Removed annotations
        expected.add(new RemoveAxiom(ontology, getAnnotation(PREFLABEL_IRI, "SultanaTopping", "Sultana", "en")));
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "CoberturaSultana", "pt")));
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "SultanaTopping", "en")));

        // Removed subClassOf axioms on SultanaTopping itself
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(sultana, getKlass("FruitTopping"))));
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(sultana,
                factory.getOWLObjectSomeValuesFrom(hasSpiciness, getKlass("Medium")))));

        // Removed and rewritten referencing axioms from Veneziana class:
        // 1. Veneziana SubClassOf: hasTopping some SultanaTopping
        expected.add(new RemoveAxiom(ontology,
                factory.getOWLSubClassOfAxiom(veneziana, factory.getOWLObjectSomeValuesFrom(hasTopping, sultana))));
        expected.add(new AddAxiom(ontology,
                factory.getOWLSubClassOfAxiom(veneziana, factory.getOWLObjectSomeValuesFrom(hasTopping, garlic))));

        // 2. Veneziana SubClassOf: hasTopping only (SultanaTopping or ...)
        HashSet<OWLClassExpression> exprs = new HashSet<OWLClassExpression>();
        exprs.add(sultana);
        exprs.add(getKlass("CaperTopping"));
        exprs.add(getKlass("MozzarellaTopping"));
        exprs.add(getKlass("OliveTopping"));
        exprs.add(getKlass("OnionTopping"));
        exprs.add(getKlass("PineKernels"));
        exprs.add(getKlass("TomatoTopping"));
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(veneziana,
                factory.getOWLObjectAllValuesFrom(hasTopping, factory.getOWLObjectUnionOf(exprs)))));
        exprs.remove(sultana);
        exprs.add(garlic);
        expected.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(veneziana,
                factory.getOWLObjectAllValuesFrom(hasTopping, factory.getOWLObjectUnionOf(exprs)))));

        testChange(change, expected, null);
    }

    @Test
    void testNodeUnobsoletion() {
        // Create an obsolete class that we can unobsolete
        NodeChange change = new NodeObsoletion();
        setAboutNode(change, "SultanaTopping");
        DirectOWLTranslator translator = new DirectOWLTranslator(ontology, null);
        List<OWLOntologyChange> owlChanges = change.accept(translator);
        ontology.getOWLOntologyManager().applyChanges(owlChanges);

        // Now try unobsoletion proper
        change = new NodeUnobsoletion();
        setAboutNode(change, "SultanaTopping");
        owlChanges.clear();

        OWLClass sultana = getKlass("SultanaTopping");

        // Removed deprecation axiom
        owlChanges.add(new RemoveAxiom(ontology, factory.getDeprecatedOWLAnnotationAssertionAxiom(sultana.getIRI())));

        // Removed "obsolete "-prefixed label
        owlChanges.add(
                new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "obsolete SultanaTopping", "en")));

        // Added back original label
        owlChanges.add(new AddAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "SultanaTopping", "en")));

        testChange(change, owlChanges, null);
    }

    @Test
    void testUnobsoleteNotObsoleteClass() {
        NodeUnobsoletion change = new NodeUnobsoletion();
        setAboutNode(change, "SultanaTopping");

        testChange(change, null, null);
    }

    @Test
    void testNodeDeletion() {
        NodeDeletion change = new NodeDeletion();
        setAboutNode(change, "SultanaTopping");

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        OWLClass sultana = getKlass("SultanaTopping");
        OWLClass veneziana = getKlass("Veneziana");
        OWLObjectProperty hasTopping = getObjectProperty("hasTopping");

        // Removed class declaration
        expected.add(new RemoveAxiom(ontology, factory.getOWLDeclarationAxiom(sultana)));

        // Removed annotations
        expected.add(new RemoveAxiom(ontology, getAnnotation(PREFLABEL_IRI, "SultanaTopping", "Sultana", "en")));
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "CoberturaSultana", "pt")));
        expected.add(new RemoveAxiom(ontology, getAnnotation(LABEL_IRI, "SultanaTopping", "SultanaTopping", "en")));

        // Removed subClassOf axioms on SultanaTopping itself
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(sultana, getKlass("FruitTopping"))));
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(sultana,
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasSpiciness"), getKlass("Medium")))));

        // Removed referencing axioms from Veneziana class:
        // 1. Veneziana SubClassOf: hasTopping some SultanaTopping
        expected.add(new RemoveAxiom(ontology,
                factory.getOWLSubClassOfAxiom(veneziana, factory.getOWLObjectSomeValuesFrom(hasTopping, sultana))));

        // 2. Veneziana SubClassOf: hasTopping only (SultanaTopping or ...)
        HashSet<OWLClassExpression> exprs = new HashSet<OWLClassExpression>();
        exprs.add(sultana);
        exprs.add(getKlass("CaperTopping"));
        exprs.add(getKlass("MozzarellaTopping"));
        exprs.add(getKlass("OliveTopping"));
        exprs.add(getKlass("OnionTopping"));
        exprs.add(getKlass("PineKernels"));
        exprs.add(getKlass("TomatoTopping"));
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(veneziana,
                factory.getOWLObjectAllValuesFrom(hasTopping, factory.getOWLObjectUnionOf(exprs)))));

        testChange(change, expected, null);
    }

    @Test
    void testCreateClass() {
        ClassCreation change = new ClassCreation();
        setAboutNode(change, "Fasciana");
        setValue(change, "Fasciana", "en");

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        OWLClass fasciana = getKlass("Fasciana");
        expected.add(new AddAxiom(ontology, factory.getOWLDeclarationAxiom(fasciana)));
        expected.add(new AddAxiom(ontology, getAnnotation(LABEL_IRI, "Fasciana", "Fasciana", "en")));

        testChange(change, expected, null);
    }

    @Test
    void testCreateExistingClass() {
        ClassCreation change = new ClassCreation();
        setAboutNode(change, "LaReine");
        setValue(change, "TheQueen", "en");

        testChange(change, null, "Class <" + PIZZA_BASE + "LaReine> already exists");
    }

    @Test
    void testCreateSubClassOfEdge() {
        PlaceUnder change = new PlaceUnder();
        change.setSubject(util.getNode("LaReine"));
        change.setObject(util.getNode("UnclosedPizza"));

        testChange(change,
                new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("UnclosedPizza"))));
    }

    @Test
    void testCreateExistentialRestrictionEdge() {
        EdgeCreation change = new EdgeCreation();
        change.setSubject(util.getNode("LaReine"));
        change.setPredicate(util.getNode("hasBase"));
        change.setObject(util.getNode("DeepPanBase"));

        testChange(change, new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"),
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasBase"), getKlass("DeepPanBase")))));
    }

    @Test
    void testAnnotationEdge() {
        EdgeCreation change = new EdgeCreation();
        change.setSubject(util.getNode("LaReine"));
        change.setPredicate(util.getForeignNode(OWLRDFVocabulary.RDFS_SEE_ALSO.toString()));
        change.setObject(util.getNode("Rosa"));

        testChange(change,
                new AddAxiom(ontology,
                        factory.getOWLAnnotationAssertionAxiom(
                                factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI()),
                                util.getIRI("LaReine"), util.getIRI("Rosa"))));
    }

    @Test
    void testCreateExistentialRestrictionEdgeWithMissingProperty() {
        EdgeCreation change = new EdgeCreation();
        change.setSubject(util.getNode("LaReine"));
        change.setPredicate(util.getNode("has_base"));
        change.setObject(util.getNode("DeepPanBase"));

        testChange(change, null, "Edge predicate <" + PIZZA_BASE + "has_base> not found");
    }

    @Test
    void testDeleteSubClassOfEdge() {
        RemoveUnder change = new RemoveUnder();
        change.setSubject(util.getNode("LaReine"));
        change.setObject(util.getNode("NamedPizza"));

        testChange(change,
                new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("NamedPizza"))));
    }

    @Test
    void testDeleteExistentialRestrictionEdge() {
        EdgeDeletion change = new EdgeDeletion();
        change.setSubject(util.getNode("UnclosedPizza"));
        change.setPredicate(util.getNode("hasTopping"));
        change.setObject(util.getNode("MozzarellaTopping"));

        testChange(change, new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("UnclosedPizza"),
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasTopping"), getKlass("MozzarellaTopping")))));
    }

    @Test
    void testDeleteAnnotationEdge() {
        // The pizza ontology does not contain any annotation-style edge, so we need to
        // create one
        OWLAxiom newEdge = factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI()), util.getIRI("LaReine"),
                util.getIRI("Rosa"));
        ontology.getOWLOntologyManager().addAxiom(ontology, newEdge);

        EdgeDeletion change = new EdgeDeletion();
        change.setSubject(util.getNode("LaReine"));
        change.setPredicate(util.getForeignNode(OWLRDFVocabulary.RDFS_SEE_ALSO.toString()));
        change.setObject(util.getNode("Rosa"));

        testChange(change, new RemoveAxiom(ontology, newEdge));
    }

    @Test
    void testDeleteMissingExistentialRestrictionEdge() {
        EdgeDeletion change = new EdgeDeletion();
        change.setSubject(util.getNode("LaReine"));
        change.setPredicate(util.getNode("hasTopping"));
        change.setObject(util.getNode("ParmesanTopping"));

        testChange(change, null,
                "No edge found between <" + PIZZA_BASE + "LaReine> and <" + PIZZA_BASE + "ParmesanTopping>");
    }

    @Test
    void testMoveSubClassOfEdge() {
        NodeMove change = new NodeMove();
        change.setAboutEdge(util.getEdge("LaReine", null, null));
        change.setOldValue(util.getId("NamedPizza"));
        change.setNewValue(util.getId("Pizza"));

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("NamedPizza"))));
        expected.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("Pizza"))));

        testChange(change, expected, null);
    }

    @Test
    void testMoveInexistingSubClassOfEdge() {
        NodeMove change = new NodeMove();
        change.setAboutEdge(util.getEdge("LaReine", null, null));
        change.setOldValue(util.getId("PizzaTopping"));
        change.setNewValue(util.getId("NamedPizza"));

        testChange(change, null,
                "No edge found between <" + PIZZA_BASE + "LaReine> and <" + PIZZA_BASE + "PizzaTopping>");
    }

    @Test
    void testMoveExistentialRestrictionEdge() {
        NodeMove change = new NodeMove();
        change.setAboutEdge(util.getEdge("Pizza", null, null));
        change.setOldValue(util.getId("PizzaBase"));
        change.setNewValue(util.getId("PizzaTopping"));

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("Pizza"),
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasBase"), getKlass("PizzaBase")))));
        expected.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("Pizza"),
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasBase"), getKlass("PizzaTopping")))));

        testChange(change, expected, null);
    }

    @Test
    void testMoveAnnotationEdge() {
        // The pizza ontology does not contain any annotation-style edge, so we need to
        // create one
        OWLAxiom newEdge = factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI()), util.getIRI("LaReine"),
                util.getIRI("Rosa"));
        ontology.getOWLOntologyManager().addAxiom(ontology, newEdge);

        NodeMove change = new NodeMove();
        change.setAboutEdge(util.getEdge("LaReine", null, null));
        change.setOldValue(util.getId("Rosa"));
        change.setNewValue(util.getId("Caprina"));

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(new RemoveAxiom(ontology, newEdge));
        expected.add(new AddAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(
                        factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI()),
                        util.getIRI("LaReine"), util.getIRI("Caprina"))));

        testChange(change, expected, null);
    }

    @Test
    void testDeepeningMove() {
        NodeDeepening change = new NodeDeepening();
        change.setAboutEdge(util.getEdge("LaReine", null, null));
        change.setOldValue(util.getId("NamedPizza"));
        change.setNewValue(util.getId("Fiorentina"));

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(
                new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("NamedPizza"))));
        expected.add(
                new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("Fiorentina"))));

        testChange(change, expected, null);
    }

    @Test
    void testInvalidDeepeningMove() {
        NodeDeepening change = new NodeDeepening();
        change.setAboutEdge(util.getEdge("LaReine", null, null));
        change.setOldValue(util.getId("NamedPizza"));
        change.setNewValue(util.getId("UnclosedPizza"));

        testChange(change, null,
                "<" + PIZZA_BASE + "NamedPizza> is not an ancestor of <" + PIZZA_BASE + "UnclosedPizza>");
    }

    @Test
    void testShallowingMove() {
        NodeShallowing change = new NodeShallowing();
        change.setAboutEdge(util.getEdge("LaReine", null, null));
        change.setOldValue(util.getId("NamedPizza"));
        change.setNewValue(util.getId("Pizza"));

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(
                new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("NamedPizza"))));
        expected.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("Pizza"))));

        testChange(change, expected, null);
    }

    @Test
    void testInvalidShallowingMove() {
        NodeShallowing change = new NodeShallowing();
        change.setAboutEdge(util.getEdge("LaReine", null, null));
        change.setOldValue(util.getId("NamedPizza"));
        change.setNewValue(util.getId("Fiorentina"));

        testChange(change, null, "<" + PIZZA_BASE + "Fiorentina> is not an ancestor of <" + PIZZA_BASE + "NamedPizza>");
    }

    @Test
    void testChangeSubClassOfToExistentialRestriction() {
        PredicateChange change = new PredicateChange();
        change.setAboutEdge(util.getEdge("LaReine", null, "NamedPizza"));
        change.setOldValue(SUBCLASSOF_IRI.toString());
        change.setNewValue(util.getId("hasTopping"));

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(
                new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("NamedPizza"))));
        expected.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"),
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasTopping"), getKlass("NamedPizza")))));

        testChange(change, expected, null);
    }

    @Test
    void testChangeExistentialRestrictionToSubClassOf() {
        PredicateChange change = new PredicateChange();
        change.setAboutEdge(util.getEdge("Pizza", null, "PizzaBase"));
        change.setOldValue(util.getId("hasBase"));
        change.setNewValue(SUBCLASSOF_IRI.toString());

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("Pizza"),
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasBase"), getKlass("PizzaBase")))));
        expected.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("Pizza"), getKlass("PizzaBase"))));

        testChange(change, expected, null);
    }

    @Test
    void testChangeExistentialRestriction() {
        PredicateChange change = new PredicateChange();
        change.setAboutEdge(util.getEdge("Pizza", null, "PizzaBase"));
        change.setOldValue(util.getId("hasBase"));
        change.setNewValue(util.getId("hasTopping"));

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("Pizza"),
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasBase"), getKlass("PizzaBase")))));
        expected.add(new AddAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("Pizza"),
                factory.getOWLObjectSomeValuesFrom(getObjectProperty("hasTopping"), getKlass("PizzaBase")))));

        testChange(change, expected, null);
    }

    @Test
    void testChangeSubClassToAnnotation() {
        PredicateChange change = new PredicateChange();
        change.setAboutEdge(util.getEdge("LaReine", null, "NamedPizza"));
        change.setOldValue(SUBCLASSOF_IRI.toString());
        change.setNewValue(OWLRDFVocabulary.RDFS_SEE_ALSO.toString());

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(
                new RemoveAxiom(ontology, factory.getOWLSubClassOfAxiom(getKlass("LaReine"), getKlass("NamedPizza"))));
        expected.add(new AddAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(
                        factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI()),
                        util.getIRI("LaReine"), util.getIRI("NamedPizza"))));

        testChange(change, expected, null);
    }

    @Test
    void testChangeInexistingPredicate() {
        PredicateChange change = new PredicateChange();
        change.setAboutEdge(util.getEdge("Pizza", null, "PizzaBase"));
        change.setOldValue(SUBCLASSOF_IRI.toString());
        change.setNewValue(util.getId("hasBase"));

        testChange(change, null, "No <" + SUBCLASSOF_IRI.toString() + "> edge found between <" + PIZZA_BASE
                + "Pizza> and <" + PIZZA_BASE + "PizzaBase>");
    }

    @Test
    void testChangePredicateToInexistingPredicate() {
        PredicateChange change = new PredicateChange();
        change.setAboutEdge(util.getEdge("Pizza", null, "PizzaBase"));
        change.setOldValue(util.getId("hasBase"));
        change.setNewValue(util.getId("hasAnotherBase"));

        testChange(change, null, "Edge predicate <" + util.getId("hasAnotherBase") + "> not found");
    }

    @Test
    void testChangeAnnotation() {
        NodeAnnotationChange change = new NodeAnnotationChange();
        setAboutNode(change, "LaReine");
        change.setAnnotationProperty(PREFLABEL_IRI.toString());
        setValue(change, "La Reine", "en", true);
        setValue(change, "The Queen", "en");

        ArrayList<OWLOntologyChange> expected = new ArrayList<OWLOntologyChange>();
        expected.add(new RemoveAxiom(ontology, getAnnotation(PREFLABEL_IRI, "LaReine", "La Reine", "en")));
        expected.add(new AddAxiom(ontology, getAnnotation(PREFLABEL_IRI, "LaReine", "The Queen", "en")));

        testChange(change, expected, null);
    }

    @Test
    void testChangeAnnotationMissingProperty() {
        NodeAnnotationChange change = new NodeAnnotationChange();
        setAboutNode(change, "LaReine");
        change.setAnnotationProperty("inexisting_property");
        setValue(change, "La Reine", "en", true);
        setValue(change, "The Queen", "en");

        testChange(change, null, "Property <inexisting_property> not found in signature");
    }

    @Test
    void testChangeAnnotationMissingValue() {
        NodeAnnotationChange change = new NodeAnnotationChange();
        setAboutNode(change, "LaReine");
        change.setAnnotationProperty(PREFLABEL_IRI.toString());
        setValue(change, "LaReine", "en", true);
        setValue(change, "The Queen", "en");

        testChange(change, null, "Expected annotation value not found for property <" + PREFLABEL_IRI.toString()
                + "> on node <" + PIZZA_BASE + "LaReine>");
    }

    @Test
    void testAddSubset() {
        AddNodeToSubset change = new AddNodeToSubset();
        setAboutNode(change, "LaReine");
        OntologySubset subset = new OntologySubset();
        subset.setId(util.getId("preferred_pizzas"));
        change.setInSubset(subset);

        testChange(change,
                new AddAxiom(ontology,
                        factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(IN_SUBSET_IRI),
                                util.getIRI("LaReine"), util.getIRI("preferred_pizzas"))));
    }

    @Test
    void testAddInexistingNodeToSubset() {
        AddNodeToSubset change = new AddNodeToSubset();
        setAboutNode(change, "InexistingPizza");
        OntologySubset subset = new OntologySubset();
        subset.setId(util.getId("preferred_pizzas"));
        change.setInSubset(subset);

        testChange(change, null, "Node <" + PIZZA_BASE + "InexistingPizza> not found in signature");
    }

    @Test
    void testRemoveSubset() {
        // There are no subsets in the pizza ontology, so we need to create one first
        OWLAxiom newSubset = factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(IN_SUBSET_IRI),
                util.getIRI("LaReine"), util.getIRI("preferred_pizzas"));
        ontology.getOWLOntologyManager().addAxiom(ontology, newSubset);

        RemoveNodeFromSubset change = new RemoveNodeFromSubset();
        setAboutNode(change, "LaReine");
        OntologySubset subset = new OntologySubset();
        subset.setId(util.getId("preferred_pizzas"));
        change.setInSubset(subset);

        testChange(change, new RemoveAxiom(ontology, newSubset));
    }

    @Test
    void testInexistingRemoveNodeFromSubset() {
        RemoveNodeFromSubset change = new RemoveNodeFromSubset();
        setAboutNode(change, "InexistingPizza");
        OntologySubset subset = new OntologySubset();
        subset.setId(util.getId("preferred_pizzas"));
        change.setInSubset(subset);

        testChange(change, null, "Node <" + PIZZA_BASE + "InexistingPizza> not found in signature");
    }

    @Test
    void testRemoveNodeNotPresentInSubset() {
        RemoveNodeFromSubset change = new RemoveNodeFromSubset();
        setAboutNode(change, "LaReine");
        OntologySubset subset = new OntologySubset();
        subset.setId(util.getId("preferred_pizzas"));
        change.setInSubset(subset);

        testChange(change, null,
                "Node <" + PIZZA_BASE + "LaReine> not found in subset <" + PIZZA_BASE + "preferred_pizzas>");
    }

    /*
     * Helper methods.
     */

    /*
     * Try transforming a KGCL operation and check that it yields either the
     * expected OWL changes or the expected rejection message.
     * 
     * expectedChanges may be null to indicate that no changes are expected.
     * 
     * expectedRejection may be null to indicate that no rejection is expected.
     */
    void testChange(Change change, List<OWLOntologyChange> expectedChanges, String expectedRejection) {
        OWLReasoner reasoner = null;
        if ( change instanceof NodeShallowing || change instanceof NodeDeepening ) {
            OWLReasonerFactory reasonerFactory = new ElkReasonerFactory();
            reasoner = reasonerFactory.createReasoner(ontology);
        }
        DirectOWLTranslator translator = new DirectOWLTranslator(ontology, reasoner);
        translator.addRejectListener(this);
        List<OWLOntologyChange> actualChanges = change.accept(translator);

        if ( expectedChanges != null ) {
            assertUnsortedIterableEquals(expectedChanges, actualChanges);
        } else {
            Assertions.assertEquals(0, actualChanges.size());
        }

        if ( expectedRejection != null ) {
            Assertions.assertEquals(1, actualRejections.size());
            Assertions.assertEquals(expectedRejection, actualRejections.get(0));
        } else {
            Assertions.assertEquals(0, actualRejections.size());
        }
    }

    /*
     * Try transforming a KGCL operation and check that it yields the expected OWL
     * changes.
     */
    void testChange(Change change, OWLOntologyChange... expected) {
        ArrayList<OWLOntologyChange> expectedList = new ArrayList<OWLOntologyChange>();
        for ( int i = 0; i < expected.length; i++ ) {
            expectedList.add(expected[i]);
        }
        testChange(change, expectedList, null);
    }

    /*
     * Compare the contents of iterables, without expecting that items should appear
     * in any given order.
     */
    <T> void assertUnsortedIterableEquals(Collection<T> expected, Collection<T> actual) {
        Assertions.assertEquals(expected.size(), actual.size());
        for ( T item : expected ) {
            Assertions.assertTrue(actual.contains(item),
                    String.format("Expected item not found: %s\n", item.toString()));
        }
    }

    /*
     * The following methods are intended to quickly set the parameters of a KGCL
     * change.
     */

    private void setAboutNode(NodeChange change, String id) {
        change.setAboutNode(util.getNode(id));
    }

    private void setValue(NodeChange change, String value, String language) {
        setValue(change, value, language, false);
    }

    private void setValue(NodeChange change, String value, String language, boolean old) {
        if ( old ) {
            change.setOldValue(value);
            change.setOldLanguage(language);
        } else {
            change.setNewValue(value);
            change.setNewLanguage(language);
        }
    }

    /*
     * The following methods are intended to quickly get the OWL representation of
     * an object from the Pizza ontology.
     */

    private OWLAnnotationAssertionAxiom getAnnotation(IRI property, String nodeId, String value, String language) {
        return factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(property), util.getIRI(nodeId),
                factory.getOWLLiteral(value, language));
    }

    private OWLClass getKlass(String id) {
        return factory.getOWLClass(util.getIRI(id));
    }

    private OWLObjectProperty getObjectProperty(String id) {
        return factory.getOWLObjectProperty(util.getIRI(id));
    }
}
