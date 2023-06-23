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
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.obolibrary.obo2owl.Obo2OWLConstants;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * A visitor to convert a list of KGCL
 * {@link org.incenp.obofoundry.kgcl.model.Change} objects into a list of OWL
 * API {@link org.semanticweb.owlapi.model.OWLOntologyChange} objects that, when
 * applied to an ontology, would implement the requested changes.
 */
public class Change2OwlVisitor extends ChangeVisitorBase<List<OWLOntologyChange>> {

    private OWLOntology ontology;
    private OWLDataFactory factory;
    private List<Change2OwlRejectListener> listeners;

    /**
     * Creates a new instance for the specified ontology.
     * 
     * @param ontology The ontology the changes are intended for.
     */
    public Change2OwlVisitor(OWLOntology ontology) {
        this.ontology = ontology;
        factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        listeners = new ArrayList<Change2OwlRejectListener>();
    }

    /**
     * Adds a listener for change rejection events.
     * 
     * @param listener The listener to add.
     */
    public void addRejectListener(Change2OwlRejectListener listener) {
        listeners.add(listener);
    }

    protected void onReject(Change change, String format, Object... args) {
        for ( Change2OwlRejectListener listener : listeners ) {
            listener.rejected(change, String.format(format, args));
        }
    }

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

    private List<OWLOntologyChange> makeList(OWLOntologyChange... args) {
        ArrayList<OWLOntologyChange> list = new ArrayList<OWLOntologyChange>();
        for ( OWLOntologyChange c : args ) {
            list.add(c);
        }
        return list;
    }

    @Override
    public List<OWLOntologyChange> visit(NodeRename v) {
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
            onReject(v, "No node with IRI '%s' and label '%s'%s", v.getAboutNode().getId(), v.getOldValue(),
                    v.getOldLanguage());
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
                    return makeList(new RemoveAxiom(ontology, ax));
                }
            }
        }

        return doDefault(v);
    }

    @Override
    public List<OWLOntologyChange> visit(SynonymReplacement v) {
        // I’d like to implement this as a RemoveSynonym followed by a AddSynonym to
        // avoid code duplication, but the catch is that we need to find out the type of
        // the synonym to remove (exact, narrow, broad, related?) so that we can create
        // a new synonym of the same type.
        IRI aboutNodeIri = IRI.create(v.getAboutNode().getId());
        for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(aboutNodeIri) ) {
            IRI propertyIri = ax.getProperty().getIRI();
            if ( propertyIri.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasExactSynonym.getIRI())
                    || propertyIri.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasBroadSynonym.getIRI())
                    || propertyIri.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasNarrowSynonym.getIRI())
                    || propertyIri.equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasRelatedSynonym.getIRI()) ) {
                if ( compareValue(ax.getValue(), v.getOldValue(), v.getOldLanguage()) ) {
                    return makeList(new RemoveAxiom(ontology, ax),
                            new AddAxiom(ontology,
                                    factory.getOWLAnnotationAssertionAxiom(
                                            factory.getOWLAnnotationProperty(propertyIri), aboutNodeIri,
                                            factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
                }
            }
        }

        return doDefault(v);
    }

    @Override
    public List<OWLOntologyChange> visit(NewTextDefinition v) {
        return makeList(new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(Obo2OWLConstants.Obo2OWLVocabulary.IRI_IAO_0000115.getIRI()),
                IRI.create(v.getAboutNode().getId()), factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
    }

    @Override
    public List<OWLOntologyChange> visit(RemoveTextDefinition v) {
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
        if ( changes != null ) {
            changes.addAll(addNewDefinition.accept(this));
        }
        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(NodeObsoletion v) {
        IRI obsoleteNodeIri = IRI.create(v.getAboutNode().getId());
        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // Remove the axioms that make up the class definition
        for ( OWLAxiom ax : ontology.getAxioms(factory.getOWLClass(obsoleteNodeIri), Imports.INCLUDED) ) {
            changes.add(new RemoveAxiom(ontology, ax));
        }

        // Remove annotation properties
        for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(obsoleteNodeIri)) {
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
        }
        else if ( v.getHasNondirectReplacement() != null ) {
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
}
