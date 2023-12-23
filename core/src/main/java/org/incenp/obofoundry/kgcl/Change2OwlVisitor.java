/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023 Damien Goutte-Gattat
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeChange;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.obolibrary.obo2owl.Obo2OWLConstants;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * A visitor to convert a list of KGCL {@link Change} objects into a list of OWL
 * API {@link OWLOntologyChange} objects that, when applied to an ontology,
 * would implement the requested changes.
 * <p>
 * This class is primarily intended for internal use by {@link OntologyPatcher}.
 * It may however be used directly by client code to obtain
 * {@link OWLOntologyChange} objects without having them immediately applied to
 * the ontology:
 * 
 * <pre>
 * Change change = ...;
 * OWLOntology ontology = ...;
 * Change2OwlVisitor visitor = new Change2OwlVisitor(ontology);
 * List&lt;OWLOntologyChange&gt; changeAsOwlChanges = change.accept(visitor);
 * </pre>
 * 
 * <p>
 * This class may also be derived to modify the way some changes are translated
 * into OWL changes. For example, if you want “definitions” to be represented by
 * another annotation than {@code http://purl.obolibrary.org/obo/IAO_0000115},
 * you could derive this class and override the
 * {@link #visit(NewTextDefinition)} and similar methods.
 */
public class Change2OwlVisitor extends ChangeVisitorBase<List<OWLOntologyChange>> {

    private OWLOntology ontology;
    private OWLDataFactory factory;
    private List<Change2OwlRejectListener> listeners;
    private Set<IRI> addedIRIs;

    /**
     * Creates a new instance for the specified ontology.
     * 
     * @param ontology The OWL API ontology the changes are intended for.
     */
    public Change2OwlVisitor(OWLOntology ontology) {
        this.ontology = ontology;
        factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        listeners = new ArrayList<Change2OwlRejectListener>();
        addedIRIs = new HashSet<IRI>();
    }

    /**
     * Adds a listener for change rejection events. All the {@code visit()} methods
     * will return an empty list if they cannot translate the change into OWL axioms
     * for any reason. Use a “reject listener” to get the reason why a change has
     * been rejected.
     * <p>
     * Note that the listener will not be called for changes that are not translated
     * merely because the type of change is not implemented.
     * 
     * @param listener The listener to add.
     */
    public void addRejectListener(Change2OwlRejectListener listener) {
        listeners.add(listener);
    }

    /**
     * Called internally whenever a change is rejected. This method formats the
     * provided error message then calls any listener that has been set up by
     * {@link #addRejectListener(Change2OwlRejectListener)}.
     * 
     * @param change The change that is rejected.
     * @param format The reason for rejecting the change, as a format string.
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.
     */
    protected void onReject(Change change, String format, Object... args) {
        for ( Change2OwlRejectListener listener : listeners ) {
            listener.rejected(change, String.format(format, args));
        }
    }

    @Override
    protected List<OWLOntologyChange> doDefault(Change v) {
        return new ArrayList<OWLOntologyChange>();
    }

    private boolean compareValue(OWLAnnotationValue value, String changeText, String changeLang) {
        String valueText = value.asLiteral().get().getLiteral();
        String valueLang = value.asLiteral().get().getLang();

        if ( !valueText.equals(changeText) ) {
            return false;
        }

        if ( valueLang == null ) {
            return changeLang == null ? true : false;
        }

        return valueLang.equals(changeLang);
    }

    private boolean aboutNodeExists(NodeChange v) {
        String nodeId = v.getAboutNode().getId();
        IRI nodeIRI = IRI.create(nodeId);
        if ( !ontology.containsEntityInSignature(nodeIRI) && !addedIRIs.contains(nodeIRI) ) {
            onReject(v, "Node <%s> not found in signature", nodeId);
            return false;
        }

        return true;
    }

    private IRI findClass(Change v, String id) {
        IRI classIRI = IRI.create(id);
        if ( !ontology.containsClassInSignature(classIRI) && !addedIRIs.contains(classIRI) ) {
            onReject(v, "Class %s not found in signature", classIRI.toQuotedString());
            return null;
        }
        return classIRI;
    }

    private Set<OWLAxiom> findEdges(IRI subjectIRI, IRI objectIRI, IRI predicateIRI) {
        HashSet<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        OWLClass object = factory.getOWLClass(objectIRI);
        OWLObjectProperty property = null;
        if ( predicateIRI != null && !OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI().equals(predicateIRI) ) {
            property = factory.getOWLObjectProperty(predicateIRI);
        }

        for ( OWLAxiom axiom : ontology.getAxioms(factory.getOWLClass(subjectIRI), Imports.INCLUDED) ) {
            if ( axiom instanceof OWLSubClassOfAxiom ) {
                OWLSubClassOfAxiom scoa = (OWLSubClassOfAxiom) axiom;
                OWLClassExpression objectExpression = scoa.getSuperClass();
                if ( objectExpression.containsEntityInSignature(object) ) {
                    if ( predicateIRI == null ) {
                        // No predicate specified, so any edge between subject and object is a match
                        axioms.add(scoa);
                    } else if ( property != null
                            && objectExpression.getObjectPropertiesInSignature().contains(property) ) {
                        // Predicate is a property and this expression has it, so it's a match
                        axioms.add(scoa);
                    } else if ( property == null && objectExpression.isNamed() ) {
                        // Predicate is rdfs:subClassOf and this expression is the object, it's a match
                        axioms.add(scoa);
                    }
                }
            }
        }

        return axioms;
    }

    private List<OWLOntologyChange> makeList(OWLOntologyChange... args) {
        ArrayList<OWLOntologyChange> list = new ArrayList<OWLOntologyChange>();
        for ( OWLOntologyChange c : args ) {
            list.add(c);
        }
        return list;
    }

    @Override
    public List<OWLOntologyChange> visit(NodeRename v) {
        if ( !aboutNodeExists(v) ) {
            return doDefault(v);
        }

        OWLAnnotationAssertionAxiom oldLabelAxiom = null;
        for ( OWLAnnotationAssertionAxiom ax : ontology
                .getAnnotationAssertionAxioms(IRI.create(v.getAboutNode().getId())) ) {
            if ( ax.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI()) ) {
                if ( compareValue(ax.getValue(), v.getOldValue(), v.getOldLanguage()) ) {
                    oldLabelAxiom = ax;
                }
            }
        }

        if ( oldLabelAxiom == null ) {
            onReject(v, "Label \"%s\" not found on <%s>", v.getOldValue(), v.getAboutNode().getId());
            return doDefault(v);
        }

        RemoveAxiom removeOldLabel = new RemoveAxiom(ontology, oldLabelAxiom);
        AddAxiom addNewLabel = new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
                IRI.create(v.getAboutNode().getId()), factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage())));

        return makeList(removeOldLabel, addNewLabel);
    }

    @Override
    public List<OWLOntologyChange> visit(NewSynonym v) {
        if ( !aboutNodeExists(v) ) {
            return doDefault(v);
        }

        IRI aboutNodeIri = IRI.create(v.getAboutNode().getId());

        // The KGCL spec says the qualifier is optional, but if we use oboInOwl
        // properties to represent synonyms a qualifier is mandatory (there is no
        // unqualified "hasSynonym" property AFAIK), so if no qualifier was specified we
        // default to 'exact'.
        IRI propertyIri = Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasExactSynonym.getIRI();
        String qualifier = v.getQualifier();
        if ( qualifier != null ) {
            switch ( qualifier ) {
            case "narrow":
                propertyIri = Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasNarrowSynonym.getIRI();
                break;

            case "broad":
                propertyIri = Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasBroadSynonym.getIRI();
                break;

            case "related":
                propertyIri = Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasRelatedSynonym.getIRI();
                break;
            }
        }

        return makeList(new AddAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(propertyIri), aboutNodeIri,
                        factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
    }

    @Override
    public List<OWLOntologyChange> visit(RemoveSynonym v) {
        if ( !aboutNodeExists(v) ) {
            return doDefault(v);
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for ( OWLAnnotationAssertionAxiom ax : ontology
                .getAnnotationAssertionAxioms(IRI.create(v.getAboutNode().getId())) ) {
            // The KGCL 'remove synonym' instruction is qualifier-agnostic, so we remove ANY
            // matching synonym regardless of its type
            IRI propertyIRI = ax.getProperty().getIRI();
            if ( propertyIRI.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasExactSynonym.getIRI())
                    || propertyIRI.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasBroadSynonym.getIRI())
                    || propertyIRI.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasNarrowSynonym.getIRI())
                    || propertyIRI.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasRelatedSynonym.getIRI()) ) {
                if ( compareValue(ax.getValue(), v.getOldValue(), v.getOldLanguage()) ) {
                    changes.add(new RemoveAxiom(ontology, ax));
                }
            }
        }

        if ( changes.isEmpty() ) {
            onReject(v, "Synonym \"%s\" not found on <%s>", v.getOldValue(), v.getAboutNode().getId());
        }

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(SynonymReplacement v) {
        if ( !aboutNodeExists(v) ) {
            return doDefault(v);
        }

        // I’d like to implement this as a RemoveSynonym followed by a AddSynonym to
        // avoid code duplication, but the catch is that we need to find out the type of
        // the synonym to remove (exact, narrow, broad, related?) so that we can create
        // a new synonym of the same type.
        IRI aboutNodeIri = IRI.create(v.getAboutNode().getId());
        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(aboutNodeIri) ) {
            IRI propertyIri = ax.getProperty().getIRI();
            if ( propertyIri.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasExactSynonym.getIRI())
                    || propertyIri.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasBroadSynonym.getIRI())
                    || propertyIri.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasNarrowSynonym.getIRI())
                    || propertyIri.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasRelatedSynonym.getIRI()) ) {
                if ( compareValue(ax.getValue(), v.getOldValue(), v.getOldLanguage()) ) {
                    changes.add(new RemoveAxiom(ontology, ax));
                    changes.add(new AddAxiom(ontology,
                            factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(propertyIri),
                                    aboutNodeIri, factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
                }
            }
        }

        if ( changes.isEmpty() ) {
            onReject(v, "Synonym \"%s\" not found on <%s>", v.getOldValue(), v.getAboutNode().getId());
        }

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(NewTextDefinition v) {
        if ( !aboutNodeExists(v) ) {
            return doDefault(v);
        }

        return makeList(new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(Obo2OWLConstants.Obo2OWLVocabulary.IRI_IAO_0000115.getIRI()),
                IRI.create(v.getAboutNode().getId()), factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
    }

    @Override
    public List<OWLOntologyChange> visit(RemoveTextDefinition v) {
        if ( !aboutNodeExists(v) ) {
            return doDefault(v);
        }

        for ( OWLAnnotationAssertionAxiom ax : ontology
                .getAnnotationAssertionAxioms(IRI.create(v.getAboutNode().getId())) ) {
            if ( ax.getProperty().getIRI().equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_IAO_0000115.getIRI()) ) {
                // If we have the text of the definition to remove, check that it matches the
                // definition we found. The KGCL command syntax does not allow specifying the
                // text of the definition to remove, but the KGCL model does.
                if ( v.getOldValue() == null || compareValue(ax.getValue(), v.getOldValue(), v.getOldLanguage()) ) {
                    return makeList(new RemoveAxiom(ontology, ax));
                }
            }
        }

        onReject(v, "Definition not found on <%s>", v.getAboutNode().getId());
        return doDefault(v);
    }

    @Override
    public List<OWLOntologyChange> visit(TextDefinitionReplacement v) {
        RemoveTextDefinition removeOldDefinition = new RemoveTextDefinition();
        removeOldDefinition.setAboutNode(v.getAboutNode());
        removeOldDefinition.setOldValue(v.getOldValue());
        removeOldDefinition.setOldLanguage(v.getOldLanguage());

        NewTextDefinition addNewDefinition = new NewTextDefinition();
        addNewDefinition.setAboutNode(v.getAboutNode());
        addNewDefinition.setNewValue(v.getNewValue());
        addNewDefinition.setNewLanguage(v.getNewLanguage());

        // Hack: the remove part may fail if we were provided with the text of the
        // definition to remove and it doesn't match the existing definition -- in which
        // case we should not proceed with the add part.

        List<OWLOntologyChange> changes = removeOldDefinition.accept(this);
        if ( !changes.isEmpty() ) {
            changes.addAll(addNewDefinition.accept(this));
        }
        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(NodeObsoletion v) {
        if ( !aboutNodeExists(v) ) {
            return doDefault(v);
        }

        IRI obsoleteNodeIri = IRI.create(v.getAboutNode().getId());
        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // Remove the axioms that make up the class definition
        for ( OWLAxiom ax : ontology.getAxioms(factory.getOWLClass(obsoleteNodeIri), Imports.INCLUDED) ) {
            changes.add(new RemoveAxiom(ontology, ax));
        }

        // Remove annotation properties
        for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(obsoleteNodeIri) ) {
            if ( ax.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI()) ) {
                // Prepend "obsolete " to the existing label
                String oldLabel = ax.getValue().asLiteral().get().getLiteral();
                changes.add(new AddAxiom(ontology,
                        factory.getOWLAnnotationAssertionAxiom(
                                factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), obsoleteNodeIri,
                                factory.getOWLLiteral("obsolete " + oldLabel))));
            }
            changes.add(new RemoveAxiom(ontology, ax));
        }

        // Add deprecation annotation property
        changes.add(new AddAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(
                        factory.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_DEPRECATED.getIRI()), obsoleteNodeIri,
                        factory.getOWLLiteral(true))));

        // Add "term replaced by"
        if ( v.getHasDirectReplacement() != null ) {
            IRI replacementNodeIri = IRI.create(v.getHasDirectReplacement().getId());
            changes.add(new AddAxiom(ontology,
                    factory.getOWLAnnotationAssertionAxiom(
                            factory.getOWLAnnotationProperty(
                                    Obo2OWLConstants.Obo2OWLVocabulary.IRI_IAO_0100001.getIRI()),
                            obsoleteNodeIri, replacementNodeIri)));
        } else if ( v.getHasNondirectReplacement() != null ) {
            // Add "consider"
            for ( Node consider : v.getHasNondirectReplacement() ) {
                changes.add(new AddAxiom(ontology,
                        factory.getOWLAnnotationAssertionAxiom(
                                factory.getOWLAnnotationProperty(
                                        Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_consider.getIRI()),
                                obsoleteNodeIri, IRI.create(consider.getId()))));
            }
        }

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(NodeObsoletionWithDirectReplacement v) {
        return visit((NodeObsoletion) v);
    }

    @Override
    public List<OWLOntologyChange> visit(NodeObsoletionWithNoDirectReplacement v) {
        return visit((NodeObsoletion) v);
    }

    @Override
    public List<OWLOntologyChange> visit(ClassCreation v) {
        IRI classIRI = IRI.create(v.getAboutNode().getId());

        OWLAxiom newClassAxiom = factory.getOWLDeclarationAxiom(factory.getOWLClass(classIRI));
        OWLAxiom newLabelAxiom = factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), classIRI,
                factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()));

        addedIRIs.add(classIRI);

        return makeList(new AddAxiom(ontology, newClassAxiom), new AddAxiom(ontology, newLabelAxiom));
    }

    @Override
    public List<OWLOntologyChange> visit(EdgeCreation v) {
        // TODO: Support subject and object being something else than OWL classes
        IRI subjectIRI = findClass(v, v.getSubject().getId());
        if ( subjectIRI == null ) {
            return doDefault(v);
        }

        IRI predicateIRI = IRI.create(v.getPredicate().getId());
        IRI objectIRI = IRI.create(v.getObject().getId());
        OWLAxiom edgeAxiom = null;

        if ( predicateIRI.equals(OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI()) ) {
            edgeAxiom = factory.getOWLSubClassOfAxiom(factory.getOWLClass(subjectIRI), factory.getOWLClass(objectIRI));
        } else if ( ontology.containsObjectPropertyInSignature(predicateIRI) ) {
            edgeAxiom = factory.getOWLSubClassOfAxiom(factory.getOWLClass(subjectIRI),
                    factory.getOWLObjectSomeValuesFrom(factory.getOWLObjectProperty(predicateIRI),
                            factory.getOWLClass(objectIRI)));
        }

        if ( edgeAxiom == null ) {
            onReject(v, "Edge predicate <%s> not found", v.getPredicate().getId());
            return doDefault(v);
        }

        return makeList(new AddAxiom(ontology, edgeAxiom));
    }

    @Override
    public List<OWLOntologyChange> visit(EdgeDeletion v) {
        IRI subjectIRI = findClass(v, v.getSubject().getId());
        IRI objectIRI = findClass(v, v.getObject().getId());
        IRI predicateIRI = v.getPredicate() != null ? IRI.create(v.getPredicate().getId()) : null;

        if ( subjectIRI == null || objectIRI == null ) {
            return doDefault(v);
        }

        Set<OWLAxiom> edges = findEdges(subjectIRI, objectIRI, predicateIRI);
        if ( edges.isEmpty() ) {
            onReject(v, "No edge found between %s and %s", subjectIRI.toQuotedString(), objectIRI.toQuotedString());
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        edges.forEach(axiom -> changes.add(new RemoveAxiom(ontology, axiom)));

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(PlaceUnder v) {
        /*
         * The KGCL documentation says "PlaceUnder" is merely an "EdgeCreation" where
         * the predicate is rdfs:subClassOf. The KGCL language has no separate
         * instruction to create such a change, but one could create it directly
         * in-memory. In this case it's unclear whether the predicate should be
         * explicitly set to rdfs:subClassOf, so in case it has not been set we do so
         * here.
         */
        if ( v.getPredicate() == null ) {
            Node predicate = new Node();
            predicate.setId(OWLRDFVocabulary.RDFS_SUBCLASS_OF.toString());
            v.setPredicate(predicate);
        }

        return visit((EdgeCreation) v);
    }

    @Override
    public List<OWLOntologyChange> visit(RemoveUnder v) {
        /*
         * The KGCL documentation says "RemoveUnder" is merely an "EdgeDeletion" where
         * the predicate is rdfs:subClassOf. The KGCL language has no separate
         * instruction to create such a change, but one one create it directly in
         * memory. In this case it's unclear whether the predicate should be explicitly
         * set to rdfs:subClassOf, so in case it has not been set we do so here.
         */
        if ( v.getPredicate() == null ) {
            Node predicate = new Node();
            predicate.setId(OWLRDFVocabulary.RDFS_SUBCLASS_OF.toString());
            v.setPredicate(predicate);
        }

        return visit((EdgeDeletion) v);
    }
}
