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

import org.incenp.obofoundry.kgcl.model.AddNodeToSubset;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.ComplexChange;
import org.incenp.obofoundry.kgcl.model.EdgeChange;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.EdgeLogicalInterpretationChange;
import org.incenp.obofoundry.kgcl.model.EdgeObsoletion;
import org.incenp.obofoundry.kgcl.model.EdgeRewiring;
import org.incenp.obofoundry.kgcl.model.IChangeVisitor;
import org.incenp.obofoundry.kgcl.model.LogicalAxiomChange;
import org.incenp.obofoundry.kgcl.model.MappingCreation;
import org.incenp.obofoundry.kgcl.model.MappingPredicateChange;
import org.incenp.obofoundry.kgcl.model.MappingReplacement;
import org.incenp.obofoundry.kgcl.model.MetadataAssertionPredicateChange;
import org.incenp.obofoundry.kgcl.model.MetadataAssertionReplacement;
import org.incenp.obofoundry.kgcl.model.MultiNodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NameBecomesSynonym;
import org.incenp.obofoundry.kgcl.model.NewMapping;
import org.incenp.obofoundry.kgcl.model.NewMetadataAssertion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationChange;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationReplacement;
import org.incenp.obofoundry.kgcl.model.NodeChange;
import org.incenp.obofoundry.kgcl.model.NodeCreation;
import org.incenp.obofoundry.kgcl.model.NodeDeepening;
import org.incenp.obofoundry.kgcl.model.NodeDeletion;
import org.incenp.obofoundry.kgcl.model.NodeDirectMerge;
import org.incenp.obofoundry.kgcl.model.NodeMappingChange;
import org.incenp.obofoundry.kgcl.model.NodeMetadataAssertionChange;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.NodeShallowing;
import org.incenp.obofoundry.kgcl.model.NodeSynonymChange;
import org.incenp.obofoundry.kgcl.model.NodeTextDefinitionChange;
import org.incenp.obofoundry.kgcl.model.NodeUnobsoletion;
import org.incenp.obofoundry.kgcl.model.ObjectPropertyCreation;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveMapping;
import org.incenp.obofoundry.kgcl.model.RemoveMetadataAssertion;
import org.incenp.obofoundry.kgcl.model.RemoveNodeFromSubset;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;
import org.incenp.obofoundry.kgcl.model.SetLanguageForName;
import org.incenp.obofoundry.kgcl.model.SimpleChange;
import org.incenp.obofoundry.kgcl.model.SynonymPredicateChange;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.incenp.obofoundry.kgcl.model.Transaction;
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
public class Change2OwlVisitor implements IChangeVisitor {

    private OWLOntology ontology;
    private OWLDataFactory factory;
    private ArrayList<OWLOntologyChange> changes;

    /**
     * Creates a new instance for the specified ontology.
     * 
     * @param ontology The ontology the changes are intended for.
     */
    public Change2OwlVisitor(OWLOntology ontology) {
        this.ontology = ontology;
        factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        changes = new ArrayList<OWLOntologyChange>();
    }

    /**
     * Apply the changes to the ontology.
     */
    public void apply() {
        if ( changes.size() > 0 ) {
            ontology.getOWLOntologyManager().applyChanges(changes);
        }
    }

    protected void onError(Change change, String format, Object... args) {
    }

    @Override
    public void visit(Change v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SimpleChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(PlaceUnder v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MappingCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeDeletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveUnder v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeObsoletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeRewiring v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeMove v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeDeepening v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeShallowing v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(PredicateChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeLogicalInterpretationChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LogicalAxiomChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeRename v) {
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
            onError(v, "No node with IRI '%s' and label '%s'%s", v.getAboutNode().getId(), v.getOldValue(),
                    v.getOldLanguage());
            return;
        }

        RemoveAxiom removeOldLabel = new RemoveAxiom(ontology, oldLabelAxiom);
        AddAxiom addNewLabel = new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
                IRI.create(v.getAboutNode().getId()), factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage())));

        changes.add(removeOldLabel);
        changes.add(addNewLabel);
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

    @Override
    public void visit(SetLanguageForName v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeAnnotationChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeAnnotationReplacement v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeSynonymChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NewSynonym v) {
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

        changes.add(new AddAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(propertyIri), aboutNodeIri,
                        factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
    }

    @Override
    public void visit(NameBecomesSynonym v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveSynonym v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SynonymReplacement v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SynonymPredicateChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeMappingChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NewMapping v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveMapping v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MappingReplacement v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MappingPredicateChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeMetadataAssertionChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NewMetadataAssertion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveMetadataAssertion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MetadataAssertionReplacement v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MetadataAssertionPredicateChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeTextDefinitionChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NewTextDefinition v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveTextDefinition v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(TextDefinitionReplacement v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(AddNodeToSubset v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveNodeFromSubset v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeObsoletion v) {
        IRI obsoleteNodeIri = IRI.create(v.getAboutNode().getId());

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
    }

    @Override
    public void visit(NodeDirectMerge v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeObsoletionWithDirectReplacement v) {
        visit((NodeObsoletion) v);
    }

    @Override
    public void visit(NodeObsoletionWithNoDirectReplacement v) {
        visit((NodeObsoletion) v);
    }

    @Override
    public void visit(NodeUnobsoletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ClassCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ObjectPropertyCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeDeletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ComplexChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MultiNodeObsoletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Transaction v) {
        // TODO Auto-generated method stub

    }

}
