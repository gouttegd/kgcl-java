/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023,2024 Damien Goutte-Gattat
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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.Namespaces;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class ProvisionalOWLTranslator extends OWLTranslator {

    private static final String KGCL = "https://w3id.org/kgcl/";
    private static final IRI PENDING_CHANGE_IRI = IRI.create(KGCL + "PendingChange");
    private static final IRI DATE_IRI = IRI.create(Namespaces.DCTERMS.toString(), "date");

    private OWLAnnotationProperty pendingChangeProperty;

    public ProvisionalOWLTranslator(OWLOntology ontology, OWLReasoner reasoner) {
        super(ontology, reasoner);

        pendingChangeProperty = factory.getOWLAnnotationProperty(PENDING_CHANGE_IRI);
    }

    public List<OWLOntologyChange> visit(NewSynonym v) {
        IRI nodeIRI = IRI.create(v.getAboutNode().getId());
        if ( !ontology.containsEntityInSignature(nodeIRI) ) {
            onReject(v, "Node <%s> not found in signature", nodeIRI.toString());
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        HashSet<OWLAnnotation> annots = new HashSet<OWLAnnotation>();

        String qualifier = v.getQualifier();
        if ( qualifier != null ) {
            annots.add(factory.getOWLAnnotation(getKGCLProperty("qualifier"), factory.getOWLLiteral(qualifier)));
        }

        annots.add(factory.getOWLAnnotation(getKGCLProperty("new_value"),
                factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage())));
        addMetadata(v, annots);

        changes.add(new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(pendingChangeProperty, nodeIRI,
                IRI.create(KGCL + "NewSynonym"), annots)));

        return changes;
    }

    public List<OWLOntologyChange> visit(NodeObsoletion v) {
        IRI nodeIRI = IRI.create(v.getAboutNode().getId());
        if ( !ontology.containsEntityInSignature(nodeIRI) ) {
            onReject(v, "Node <%s> not found in signature", nodeIRI.toString());
            return empty;
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        HashSet<OWLAnnotation> annots = new HashSet<OWLAnnotation>();

        if ( v.getHasDirectReplacement() != null ) {
            IRI replacementNodeIRI = IRI.create(v.getHasDirectReplacement().getId());
            annots.add(factory.getOWLAnnotation(getKGCLProperty("has_direct_replacement"), replacementNodeIRI));
        } else if ( v.getHasNondirectReplacement() != null ) {
            for ( Node consider : v.getHasNondirectReplacement() ) {
                annots.add(factory.getOWLAnnotation(getKGCLProperty("has_nondirect_replacement"),
                        IRI.create(consider.getId())));
            }
        }

        addMetadata(v, annots);

        changes.add(new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(pendingChangeProperty, nodeIRI,
                IRI.create(KGCL + "NodeObsoletion"), annots)));

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
    public List<OWLOntologyChange> visit(EdgeCreation v) {
        IRI nodeIRI = IRI.create(v.getSubject().getId());
        if ( !ontology.containsEntityInSignature(nodeIRI) ) {
            onReject(v, "Node <%s> not found in signature", nodeIRI.toString());
            return empty;
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        HashSet<OWLAnnotation> annots = new HashSet<OWLAnnotation>();

        annots.add(factory.getOWLAnnotation(getKGCLProperty("predicate"), IRI.create(v.getPredicate().getId())));
        annots.add(factory.getOWLAnnotation(getKGCLProperty("object"), IRI.create(v.getObject().getId())));
        addMetadata(v, annots);

        changes.add(new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(pendingChangeProperty, nodeIRI,
                IRI.create(KGCL + "EdgeCreation"), annots)));

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(PlaceUnder v) {
        if ( v.getPredicate() == null ) {
            Node predicate = new Node();
            predicate.setId(OWLRDFVocabulary.RDFS_SUBCLASS_OF.toString());
            v.setPredicate(predicate);
        }

        return visit((EdgeCreation) v);
    }

    /**
     * Extracts the list of provisional changes stored as KGCL annotations within
     * the ontology.
     * <p>
     * This method basically reverts what all the {@code visit(...)} methods are
     * doing, converting the KGCL annotations back into KGCL objects.
     * 
     * @param remove If {@code true}, the annotations are removed from the ontology
     *               during the process.
     * @param before Only return the changes that are older than the specified date.
     *               If {@code null}, all changes are returned.
     * @return The list of provisional changes.
     */
    public List<Change> extractProvisionalChanges(boolean remove, ZonedDateTime before) {
        List<Change> changeset = new ArrayList<Change>();
        Set<OWLAxiom> removeAxioms = new HashSet<OWLAxiom>();

        for ( OWLAnnotationAssertionAxiom axiom : ontology.getAxioms(AxiomType.ANNOTATION_ASSERTION) ) {
            if ( axiom.getProperty().getIRI().equals(PENDING_CHANGE_IRI) ) {
                if ( !axiom.getValue().isIRI() ) {
                    continue;
                }

                if ( before != null && !isOlderThan(axiom, before) ) {
                    continue;
                }

                String changeType = axiom.getValue().asIRI().get().toString().substring(KGCL.length());
                Change v = null;
                switch ( changeType ) {
                case "NewSynonym":
                    v = extractNewSynonym(axiom);
                    break;
                case "NodeObsoletion":
                    v = extractNodeObsoletion(axiom);
                    break;
                case "EdgeCreation":
                case "PlaceUnder":
                    v = extractEdgeCreation(axiom);
                    break;
                }

                if ( v != null ) {
                    changeset.add(v);
                    removeAxioms.add(axiom);
                }
            }
        }

        if ( remove ) {
            ontology.getOWLOntologyManager().removeAxioms(ontology, removeAxioms);
        }

        return changeset;
    }

    private boolean isOlderThan(OWLAnnotationAssertionAxiom axiom, ZonedDateTime date) {
        for ( OWLAnnotation annot : axiom.getAnnotations() ) {
            if (annot.getProperty().getIRI().equals(DATE_IRI) && annot.getValue().isLiteral()) {
                OWLLiteral value = annot.getValue().asLiteral().get();
                if ( value.getDatatype().isBuiltIn()
                        && value.getDatatype().getBuiltInDatatype().equals(OWL2Datatype.XSD_DATE_TIME) ) {
                    ZonedDateTime dt = ZonedDateTime.parse(value.getLiteral());
                    if ( dt.isBefore(date) ) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Change extractNewSynonym(OWLAnnotationAssertionAxiom axiom) {
        NewSynonym v = new NewSynonym();
        Node about = new Node();
        about.setId(((IRI) axiom.getSubject()).toString());
        v.setAboutNode(about);

        for (OWLAnnotation annot : axiom.getAnnotations()) {
            String prop = annot.getProperty().getIRI().toString().substring(KGCL.length());
            if ( prop.equals("qualifier") && annot.getValue().isLiteral() ) {
                v.setQualifier(annot.getValue().asLiteral().get().getLiteral());
            } else if ( prop.equals("new_value") && annot.getValue().isLiteral() ) {
                v.setNewValue(annot.getValue().asLiteral().get().getLiteral());
                v.setNewLanguage(annot.getValue().asLiteral().get().getLang());
            }
        }

        if ( v.getNewValue() == null ) {
            v = null;
        }

        return v;
    }

    private Change extractNodeObsoletion(OWLAnnotationAssertionAxiom axiom) {
        NodeObsoletion v = new NodeObsoletion();
        Node about = new Node();
        about.setId(((IRI) axiom.getSubject()).toString());
        v.setAboutNode(about);

        for ( OWLAnnotation annot : axiom.getAnnotations() ) {
            if ( annot.getProperty().getIRI().toString().equals(KGCL + "has_direct_replacement") ) {
                Node repl = new Node();
                repl.setId(annot.getValue().asIRI().get().toString());
                v.setHasDirectReplacement(repl);
            } else if ( annot.getProperty().getIRI().toString().equals(KGCL + "has_nondirect_replacement") ) {
                Node repl = new Node();
                repl.setId(annot.getValue().asIRI().get().toString());
                if ( v.getHasNondirectReplacement() == null ) {
                    v.setHasNondirectReplacement(new ArrayList<Node>());
                }
                v.getHasNondirectReplacement().add(repl);
            }
        }

        return v;
    }

    private Change extractEdgeCreation(OWLAnnotationAssertionAxiom axiom) {
        EdgeCreation v = new EdgeCreation();
        Node subject = new Node();
        subject.setId(((IRI) axiom.getSubject()).toString());
        v.setSubject(subject);

        for ( OWLAnnotation annot : axiom.getAnnotations() ) {
            if ( annot.getProperty().getIRI().toString().equals(KGCL + "predicate") ) {
                Node predicate = new Node();
                predicate.setId(annot.getValue().asIRI().get().toString());
                v.setPredicate(predicate);
            } else if ( annot.getProperty().getIRI().toString().equals(KGCL + "object") ) {
                Node object = new Node();
                object.setId(annot.getValue().asIRI().get().toString());
                v.setObject(object);
            }
        }

        return v;
    }

    private OWLAnnotationProperty getKGCLProperty(String name) {
        return factory.getOWLAnnotationProperty(IRI.create(KGCL + name));
    }

    private void addMetadata(Change change, Set<OWLAnnotation> annotations) {
        ZonedDateTime date = change.getChangeDate();
        if ( date == null ) {
            date = ZonedDateTime.now();
        }
        date = date.withNano(0);
        annotations.add(factory.getOWLAnnotation(factory.getOWLAnnotationProperty(DATE_IRI), factory
                .getOWLLiteral(date.format(DateTimeFormatter.ISO_INSTANT), OWL2Datatype.XSD_DATE_TIME)));
    }
}
