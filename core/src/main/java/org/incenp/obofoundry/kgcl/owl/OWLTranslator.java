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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.incenp.obofoundry.kgcl.ChangeVisitorBase;
import org.incenp.obofoundry.kgcl.RejectedChangeListener;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.NodeChange;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * Base class to translate KGCL change objects into OWL axioms.
 */
public class OWLTranslator extends ChangeVisitorBase<List<OWLOntologyChange>> {

    private List<RejectedChangeListener> listeners = new ArrayList<RejectedChangeListener>();
    protected final List<OWLOntologyChange> empty = new ArrayList<OWLOntologyChange>();
    protected OWLOntology ontology;
    protected OWLDataFactory factory;
    protected OWLReasoner reasoner;

    /**
     * Creates a new instance for the specified ontology.
     * 
     * @param ontology The ontology the changes are intended for.
     * @param reasoner The reasoner to use for operations that require one.
     */
    protected OWLTranslator(OWLOntology ontology, OWLReasoner reasoner) {
        this.ontology = ontology;
        factory = ontology.getOWLOntologyManager().getOWLDataFactory();
        this.reasoner = reasoner;
    }

    /**
     * Adds a listener for change rejection events. All the {@code visit()} methods
     * will return an empty list if they cannot translate the change into OWL axioms
     * for any reason. Use a “reject listener” to get the reason why a change has
     * been rejected.
     * 
     * @param listener The listener to add.
     */
    public void addRejectListener(RejectedChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Called internally whenever a change is rejected. This method formats the
     * provided error message then calls any listener that has been set up by
     * {@link #addRejectListener(RejectedChangeListener)}.
     * 
     * @param change The change that is rejected.
     * @param format The reason for rejecting the change, as a format string.
     * @param args   Arguments referenced by the format specifiers in the format
     *               string.
     */
    protected void onReject(Change change, String format, Object... args) {
        for ( RejectedChangeListener listener : listeners ) {
            listener.rejected(change, String.format(format, args));
        }
    }

    @Override
    protected List<OWLOntologyChange> doDefault(Change v) {
        onReject(v, "Change type not implemented: %s", v.getClass().getName());
        return empty;
    }

    /**
     * Finds all annotations with a literal value that matches the old value of a
     * NodeChange object.
     * 
     * @param property The property of the annotations to look for.
     * @param entity   The entity whose annotations should be retrieved.
     * @param change   The change object whose old value should be compared against
     *                 the annotations' values.
     * @param newValue If {@code true}, this method will compare annotations against
     *                 the <em>new</em> value of the change object, rather than the
     *                 old value.
     * @return A set containing the annotation assertion axioms with a matching
     *         values.
     */
    protected Set<OWLAnnotationAssertionAxiom> findMatchingAnnotations(IRI property, IRI entity, NodeChange change,
            boolean newValue) {
        HashSet<OWLAnnotationAssertionAxiom> axioms = new HashSet<OWLAnnotationAssertionAxiom>();
        OWLAnnotationAssertionAxiom langLessAxiom = null;
        String text = newValue ? change.getNewValue() : change.getOldValue();
        String lang = newValue ? change.getNewLanguage() : change.getOldLanguage();
        String datatype = newValue ? change.getNewDatatype() : change.getOldDatatype();

        for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(entity) ) {
            if ( !ax.getProperty().getIRI().equals(property) ) {
                continue;
            }

            OWLAnnotationValue value = ax.getValue();
            if ( !value.isLiteral() ) {
                continue;
            }

            String valueText = value.asLiteral().get().getLiteral();
            if ( text != null && !valueText.equals(text) ) {
                continue;
            }

            String valueLang = value.asLiteral().get().getLang();
            if ( valueLang.isEmpty() ) {
                // We'll decide later what to do with this one
                langLessAxiom = ax;
                continue;
            }

            // If we are expecting a given language, the language of the value must match.
            if ( lang != null && !valueLang.equals(lang) ) {
                continue;
            }

            // If the new value has an explicit language tag, then even if no language tag
            // has been explicitly specified for the old value, we can only accept a value
            // with the same language as the new value.
            if ( !newValue && change.getNewLanguage() != null && !valueLang.equals(change.getNewLanguage()) ) {
                continue;
            }

            // At this point both the text and the language match.
            axioms.add(ax);
        }

        if ( langLessAxiom != null ) {
            // We accept the langless axiom only if:
            // - no language tag was explicitly specified on the old value
            // - if a language tag was explicitly specified on the new value, we didn't find
            // any annotation in that language
            // - if a datatype was explicitly specified, it matches the datatype of the
            // langless axiom's value
            if ( lang == null && (change.getNewLanguage() == null || axioms.isEmpty())
                    && (datatype == null || langLessAxiom.getValue().asLiteral().get().getDatatype().getIRI().toString()
                            .equals(datatype)) ) {
                axioms.add(langLessAxiom);
            }
        }

        return axioms;
    }

    /**
     * Finds all annotations with a literal value that matches the old value of a
     * NodeChange object.
     * 
     * @param property The property of the annotations to look for.
     * @param entity   The entity whose annotations should be retrieved.
     * @param change   The change object whose old value should be compared against
     *                 the annotations' values.
     * @return A set containing the annotation assertion axioms with a matching
     *         values.
     */
    protected Set<OWLAnnotationAssertionAxiom> findMatchingAnnotations(IRI property, IRI entity, NodeChange change) {
        return findMatchingAnnotations(property, entity, change, false);
    }

    /**
     * Constructs a literal value from the new value parameters of a change object.
     * 
     * @param change  The change object containing the details of the new literal to
     *                construct.
     * @param oldLang If non-{@code null}, and the change object does not contain an
     *                explicit language tag for the new value, this will be used as
     *                the language tag for the literal.
     * @return The constructed OWL literal.
     */
    protected OWLLiteral getLiteral(NodeChange change, String oldLang) {
        if ( change.getNewLanguage() != null ) {
            return factory.getOWLLiteral(change.getNewValue(), change.getNewLanguage());
        } else if ( change.getNewDatatype() != null ) {
            return factory.getOWLLiteral(change.getNewValue(),
                    factory.getOWLDatatype(IRI.create(change.getNewDatatype())));
        } else if ( oldLang != null ) {
            return factory.getOWLLiteral(change.getNewValue(), oldLang);
        } else {
            return factory.getOWLLiteral(change.getNewValue());
        }
    }

    /**
     * Constructs a literal value from the new value parameters of a change object.
     * 
     * @param change The change object containing the details of the new literal to
     *               construct.
     * @return The constructed OWL literal.
     */
    protected OWLLiteral getLiteral(NodeChange change) {
        return getLiteral(change, null);
    }

    /**
     * Gets all axioms representing edges between a subject and an object.
     * 
     * @param subject   The subject to look for.
     * @param object    The object to look for.
     * @param predicate The predicate to look for. May be {@code null}, in which
     *                  case the method will return all edges between the subject
     *                  and the object regardless of their predicate.
     * @return A set of matching axioms.
     */
    protected Set<OWLAxiom> findEdges(IRI subject, IRI object, IRI predicate) {
        HashSet<OWLAxiom> edges = new HashSet<OWLAxiom>();

        OWLObjectProperty property = null;
        if ( predicate != null && !OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI().equals(predicate) ) {
            property = factory.getOWLObjectProperty(predicate);
        }

        // Search for edges between classes
        for ( OWLAxiom axiom : ontology.getAxioms(factory.getOWLClass(subject), Imports.INCLUDED) ) {
            if ( axiom instanceof OWLSubClassOfAxiom ) {
                OWLSubClassOfAxiom scoa = (OWLSubClassOfAxiom) axiom;
                OWLClassExpression objectExpression = scoa.getSuperClass();
                OWLClass objectClass = factory.getOWLClass(object);

                if ( objectExpression.containsEntityInSignature(objectClass) && objectExpression.getClassesInSignature().size() == 1 ) {
                    if ( predicate == null ) {
                        // No predicate specified, so any edge between subject and object is a match
                        edges.add(scoa);
                    } else if ( property != null
                            && objectExpression.getObjectPropertiesInSignature().contains(property) ) {
                        // Predicate is a property and this expression has it, so it's a match
                        edges.add(scoa);
                    } else if ( property == null && objectExpression.isNamed() ) {
                        // Predicate is rdfs:subClassOf and this expression is the object, it's a match
                        edges.add(scoa);
                    }
                }
            }
        }

        // TODO: Search for edges between properties or individuals

        // Search for annotations that can be assimilated to edges (annotations whose
        // value is an IRI)
        for ( OWLAnnotationAssertionAxiom axiom : ontology.getAnnotationAssertionAxioms(subject) ) {
            if ( axiom.getValue().isIRI() && axiom.getValue().asIRI().get().equals(object) ) {
                if ( predicate == null || axiom.getProperty().getIRI().equals(predicate) ) {
                    edges.add(axiom);
                }
            }
        }

        return edges;
    }
}
