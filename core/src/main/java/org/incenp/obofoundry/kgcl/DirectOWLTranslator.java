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
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
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
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerRuntimeException;
import org.semanticweb.owlapi.util.OWLAxiomVisitorExAdapter;
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
public class DirectOWLTranslator extends OWLTranslator {

    private Set<IRI> addedIRIs = new HashSet<IRI>();
    private Set<OWLAxiom> removedAxioms = new HashSet<OWLAxiom>();

    /**
     * Creates a new instance for the specified ontology.
     * 
     * @param ontology The OWL API ontology the changes are intended for.
     * @param reasoner The reasoner to use for checking the {@code NodeDeepening}
     *                 and {@code NodeShallowing} operations.
     */
    public DirectOWLTranslator(OWLOntology ontology, OWLReasoner reasoner) {
        super(ontology, reasoner);
    }

    private boolean compareValue(OWLAnnotationValue value, String changeText, String changeLang) {
        if ( !value.isLiteral() ) {
            return false;
        }

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

    private Set<OWLSubClassOfAxiom> findEdges(IRI subjectIRI, IRI objectIRI, IRI predicateIRI) {
        HashSet<OWLSubClassOfAxiom> axioms = new HashSet<OWLSubClassOfAxiom>();
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

    private boolean isAncestor(NodeMove v, IRI base, IRI ancestor) {
        if ( reasoner == null ) {
            // No reasoner, skip the check
            return true;
        }

        try {
            NodeSet<OWLClass> ancestors = reasoner.getSuperClasses(factory.getOWLClass(base), false);
            if ( !ancestors.containsEntity(factory.getOWLClass(ancestor)) ) {
                onReject(v, "%s is not an ancestor of %s", ancestor.toQuotedString(), base.toQuotedString());
                return false;
            } else {
                return true;
            }
        } catch ( OWLReasonerRuntimeException e ) {
            onReject(v, "Cannot check whether %s is an ancestor of %s: %s", ancestor.toQuotedString(),
                    base.toQuotedString(), e.getMessage());
            return false;
        }
    }

    private List<OWLOntologyChange> makeList(OWLOntologyChange... args) {
        ArrayList<OWLOntologyChange> list = new ArrayList<OWLOntologyChange>();
        for ( OWLOntologyChange c : args ) {
            list.add(c);
        }
        return list;
    }

    private RemoveAxiom removeAxiom(OWLAxiom axiom) {
        removedAxioms.add(axiom);
        return new RemoveAxiom(ontology, axiom);
    }

    @Override
    public List<OWLOntologyChange> visit(NodeRename v) {
        if ( !aboutNodeExists(v) ) {
            return empty;
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
            return empty;
        }

        AddAxiom addNewLabel = new AddAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(
                factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
                IRI.create(v.getAboutNode().getId()), factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage())));

        return makeList(removeAxiom(oldLabelAxiom), addNewLabel);
    }

    @Override
    public List<OWLOntologyChange> visit(NewSynonym v) {
        if ( !aboutNodeExists(v) ) {
            return empty;
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

        // Check for existing synonym
        for ( OWLAnnotationAssertionAxiom axiom : ontology.getAnnotationAssertionAxioms(aboutNodeIri) ) {
            if ( axiom.getProperty().getIRI().equals(propertyIri)
                    && compareValue(axiom.getValue(), v.getNewValue(), v.getNewLanguage()) ) {
                return empty;
            }
        }

        return makeList(new AddAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(propertyIri), aboutNodeIri,
                        factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
    }

    @Override
    public List<OWLOntologyChange> visit(RemoveSynonym v) {
        if ( !aboutNodeExists(v) ) {
            return empty;
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
                    changes.add(removeAxiom(ax));
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
            return empty;
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
                    changes.add(removeAxiom(ax));
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
            return empty;
        }

        IRI aboutNodeIRI = IRI.create(v.getAboutNode().getId());
        IRI definitionIRI = Obo2OWLConstants.Obo2OWLVocabulary.IRI_IAO_0000115.getIRI();

        // Check for existing definition
        for ( OWLAnnotationAssertionAxiom axiom : ontology.getAnnotationAssertionAxioms(aboutNodeIRI) ) {
            if ( axiom.getProperty().getIRI().equals(definitionIRI)
                    && compareValue(axiom.getValue(), v.getNewValue(), v.getNewLanguage()) ) {
                return empty;
            }
        }

        return makeList(new AddAxiom(ontology,
                factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(definitionIRI), aboutNodeIRI,
                        factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
    }

    @Override
    public List<OWLOntologyChange> visit(RemoveTextDefinition v) {
        if ( !aboutNodeExists(v) ) {
            return empty;
        }

        for ( OWLAnnotationAssertionAxiom ax : ontology
                .getAnnotationAssertionAxioms(IRI.create(v.getAboutNode().getId())) ) {
            if ( ax.getProperty().getIRI().equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_IAO_0000115.getIRI()) ) {
                // If we have the text of the definition to remove, check that it matches the
                // definition we found. The KGCL command syntax does not allow specifying the
                // text of the definition to remove, but the KGCL model does.
                if ( v.getOldValue() == null || compareValue(ax.getValue(), v.getOldValue(), v.getOldLanguage()) ) {
                    return makeList(removeAxiom(ax));
                }
            }
        }

        onReject(v, "Definition not found on <%s>", v.getAboutNode().getId());
        return empty;
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
            return empty;
        }

        IRI obsoleteNodeIri = IRI.create(v.getAboutNode().getId());
        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        // Remove the axioms that make up the class definition
        for ( OWLAxiom ax : ontology.getAxioms(factory.getOWLClass(obsoleteNodeIri), Imports.INCLUDED) ) {
            changes.add(removeAxiom(ax));
        }

        // Remove annotation properties
        Set<OWLAxiom> foreignLabels = new HashSet<OWLAxiom>();
        boolean keepForeignLabels = true;
        for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(obsoleteNodeIri) ) {
            if ( ax.getProperty().getIRI().equals(OWLRDFVocabulary.RDFS_LABEL.getIRI()) && ax.getValue().isLiteral() ) {
                // Prepend "obsolete " to the existing label. We only do that if the label has
                // no language tag or is explicitly an English label, because "obsolete" may not
                // mean anything (or may mean something different) in another language.
                String oldLabel = ax.getValue().asLiteral().get().getLiteral();
                String oldLang = ax.getValue().asLiteral().get().getLang();
                if ( oldLang.isEmpty() || oldLang.equalsIgnoreCase("en") || oldLang.startsWith("en-") ) {
                    changes.add(removeAxiom(ax));
                    changes.add(new AddAxiom(ontology,
                            factory.getOWLAnnotationAssertionAxiom(
                                    factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
                                    obsoleteNodeIri, factory.getOWLLiteral("obsolete " + oldLabel, oldLang))));
                    keepForeignLabels = false;
                } else {
                    // Set foreign (non-English) labels aside for now
                    foreignLabels.add(ax);
                }
            } else {
                changes.add(removeAxiom(ax));
            }
        }
        if ( !keepForeignLabels ) {
            // There was a neutral or English label, so we can remove the foreign ones
            foreignLabels.forEach(ax -> changes.add(removeAxiom(ax)));
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

            // Since the class has a direct replacement, we can rewrite all axioms referring
            // to it to make them refer to the replacement class
            AxiomRewritingVisitor rewriter = new AxiomRewritingVisitor(factory, obsoleteNodeIri, replacementNodeIri);
            for ( OWLAxiom axiom : ontology.getReferencingAxioms(obsoleteNodeIri, Imports.INCLUDED) ) {
                // Do not rewrite axioms that are already slated for removal
                if ( removedAxioms.contains(axiom) ) {
                    continue;
                }

                // Do not rewrite foreign label axioms
                if ( keepForeignLabels && foreignLabels.contains(axiom) ) {
                    continue;
                }

                OWLAxiom rewrittenAxiom = axiom.accept(rewriter);
                if ( rewrittenAxiom != null ) {
                    changes.add(removeAxiom(axiom));
                    changes.add(new AddAxiom(ontology, rewrittenAxiom));
                }
            }
        } else if ( v.getHasNondirectReplacement() != null ) {
            // Add "consider"
            for ( Node consider : v.getHasNondirectReplacement() ) {
                changes.add(new AddAxiom(ontology,
                        factory.getOWLAnnotationAssertionAxiom(
                                factory.getOWLAnnotationProperty(
                                        Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_consider.getIRI()),
                                obsoleteNodeIri, IRI.create(consider.getId()))));
            }

            /*
             * FIXME: It’s unclear to me what should be done with referencing axioms in this
             * case. Obviously we cannot rewrite them, but should we remove them or leave
             * them alone? Since they are expected to be removed when there is no
             * replacement at all (see below), it would be consistent to also remove them
             * when there are only non-direct replacements. But this creates the risk that
             * the axioms forcefully removed in that manner are never later manually
             * rewritten by editors, since they might not even realise those axioms were
             * there and had been removed.
             * 
             * https://github.com/INCATools/kgcl/issues/52
             */
        } else {
            // No replacement or alternative, the expectation from the KGCL folks is that
            // all referencing axioms should be removed
            for (OWLAxiom axiom : ontology.getReferencingAxioms(obsoleteNodeIri, Imports.INCLUDED)) {
                if ( (!(axiom instanceof OWLDeclarationAxiom))
                        && (keepForeignLabels && !foreignLabels.contains(axiom)) ) {
                    changes.add(removeAxiom(axiom));
                }
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
    public List<OWLOntologyChange> visit(NodeUnobsoletion v) {
        IRI nodeId = IRI.create(v.getAboutNode().getId());
        if ( !ontology.containsClassInSignature(nodeId) ) {
            return empty;
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(nodeId) ) {
            if ( ax.isDeprecatedIRIAssertion() ) {
                changes.add(removeAxiom(ax));
            } else if ( ax.getProperty().isLabel() && ax.getValue().isLiteral() ) {
                String label = ax.getValue().asLiteral().get().getLiteral();
                if ( label.startsWith("obsolete ") ) {
                    changes.add(removeAxiom(ax));
                    changes.add(new AddAxiom(ontology,
                            factory.getOWLAnnotationAssertionAxiom(
                                    factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()), nodeId,
                                    factory.getOWLLiteral(label.substring(9)))));
                }
            }
        }

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(NodeDeletion v) {
        IRI nodeId = IRI.create(v.getAboutNode().getId());
        if ( !ontology.containsClassInSignature(nodeId) ) {
            return empty;
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        ontology.getReferencingAxioms(nodeId, Imports.INCLUDED).forEach(ax -> changes.add(removeAxiom(ax)));

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(ClassCreation v) {
        IRI classIRI = IRI.create(v.getAboutNode().getId());
        if ( ontology.containsClassInSignature(classIRI) ) {
            onReject(v, "Class <%s> already exists", classIRI.toString());
            return empty;
        }

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
            return empty;
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
            return empty;
        }

        return makeList(new AddAxiom(ontology, edgeAxiom));
    }

    @Override
    public List<OWLOntologyChange> visit(EdgeDeletion v) {
        IRI subjectIRI = findClass(v, v.getSubject().getId());
        IRI objectIRI = findClass(v, v.getObject().getId());
        IRI predicateIRI = v.getPredicate() != null ? IRI.create(v.getPredicate().getId()) : null;

        if ( subjectIRI == null || objectIRI == null ) {
            return empty;
        }

        Set<OWLSubClassOfAxiom> edges = findEdges(subjectIRI, objectIRI, predicateIRI);
        if ( edges.isEmpty() ) {
            onReject(v, "No edge found between %s and %s", subjectIRI.toQuotedString(), objectIRI.toQuotedString());
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        edges.forEach(axiom -> changes.add(removeAxiom(axiom)));

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

    @Override
    public List<OWLOntologyChange> visit(NodeMove v) {
        IRI subjectIRI = findClass(v, v.getAboutEdge().getSubject().getId());
        IRI oldObjectIRI = findClass(v, v.getOldValue());
        IRI newObjectIRI = findClass(v, v.getNewValue());

        if ( subjectIRI == null || oldObjectIRI == null || newObjectIRI == null ) {
            return empty;
        }

        Set<OWLSubClassOfAxiom> edges = findEdges(subjectIRI, oldObjectIRI, null);
        if ( edges.isEmpty() ) {
            onReject(v, "No edge found between %s and %s", subjectIRI.toQuotedString(), oldObjectIRI.toQuotedString());
        }

        if ( v instanceof NodeDeepening && !isAncestor(v, newObjectIRI, oldObjectIRI) ) {
            return empty;
        } else if ( v instanceof NodeShallowing && !isAncestor(v, oldObjectIRI, newObjectIRI) ) {
            return empty;
        }

        HashSet<OWLAxiom> newAxioms = new HashSet<OWLAxiom>();
        ClassRewritingVisitor visitor = new ClassRewritingVisitor(factory, oldObjectIRI, newObjectIRI);
        for ( OWLSubClassOfAxiom scoa : edges ) {
            newAxioms.add(factory.getOWLSubClassOfAxiom(scoa.getSubClass(), scoa.getSuperClass().accept(visitor),
                    scoa.getAnnotations()));
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        edges.forEach(axiom -> changes.add(removeAxiom(axiom)));
        newAxioms.forEach(axiom -> changes.add(new AddAxiom(ontology, axiom)));

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(NodeDeepening v) {
        return visit((NodeMove) v);
    }

    @Override
    public List<OWLOntologyChange> visit(NodeShallowing v) {
        return visit((NodeMove) v);
    }

    @Override
    public List<OWLOntologyChange> visit(PredicateChange v) {
        IRI subjectIRI = findClass(v, v.getAboutEdge().getSubject().getId());
        IRI objectIRI = findClass(v, v.getAboutEdge().getObject().getId());
        IRI oldPredicateIRI = IRI.create(v.getOldValue());
        IRI newPredicateIRI = IRI.create(v.getNewValue());

        if (subjectIRI == null || objectIRI == null) {
            return empty;
        }

        Set<OWLSubClassOfAxiom> edges = findEdges(subjectIRI, objectIRI, oldPredicateIRI);
        if ( edges.isEmpty() ) {
            onReject(v, "No %s edge found between %s and %s", oldPredicateIRI.toQuotedString(),
                    subjectIRI.toQuotedString(), objectIRI.toQuotedString());
            return empty;
        }

        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        for ( OWLSubClassOfAxiom axiom : edges ) {
            OWLAxiom newAxiom;
            if ( oldPredicateIRI.equals(OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI()) ) {
                // Change from rdfs:subClassOf to a property restriction
                newAxiom = factory.getOWLSubClassOfAxiom(axiom.getSubClass(), factory.getOWLObjectSomeValuesFrom(
                        factory.getOWLObjectProperty(newPredicateIRI), axiom.getSuperClass()));
            } else if ( newPredicateIRI.equals(OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI()) ) {
                // Change from a property restriction to a direct classification
                newAxiom = factory.getOWLSubClassOfAxiom(axiom.getSubClass(), factory.getOWLClass(objectIRI));
            } else {
                // Change from a property restriction to another property restriction
                PropertyRewritingVisitor visitor = new PropertyRewritingVisitor(factory, oldPredicateIRI,
                        newPredicateIRI);
                newAxiom = factory.getOWLSubClassOfAxiom(axiom.getSubClass(), axiom.getSuperClass().accept(visitor));
            }

            changes.add(removeAxiom(axiom));
            changes.add(new AddAxiom(ontology, newAxiom.getAnnotatedAxiom(axiom.getAnnotations())));
        }

        return changes;
    }

    @Override
    public List<OWLOntologyChange> visit(NodeAnnotationChange v) {
        ArrayList<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        IRI nodeId = IRI.create(v.getAboutNode().getId());
        if ( !ontology.containsClassInSignature(nodeId) ) {
            onReject(v, "Class %s not found in signature", nodeId.toQuotedString());
            return changes;
        }

        IRI propertyId = IRI.create(v.getAnnotationProperty());
        if ( !ontology.containsAnnotationPropertyInSignature(propertyId) ) {
            onReject(v, "Property %s not found in signature", propertyId.toQuotedString());
            return changes;
        }

        int found = 0;
        for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(nodeId) ) {
            if ( ax.getProperty().getIRI().equals(propertyId) ) {
                found += 1;
                if ( compareValue(ax.getValue(), v.getOldValue(), v.getOldLanguage()) ) {
                    changes.add(removeAxiom(ax));
                    changes.add(new AddAxiom(ontology,
                            factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(propertyId), nodeId,
                                    factory.getOWLLiteral(v.getNewValue(), v.getNewLanguage()))));
                }
            }
        }

        if ( found > 0 && changes.isEmpty() ) {
            onReject(v, "Expected annotation value not found for property %s on node %s", propertyId.toQuotedString(),
                    nodeId.toQuotedString());
        }

        return changes;
    }

    /*
     * Rewrite a class expression to change any reference to a given class to
     * reference to another class.
     */
    private class ClassRewritingVisitor extends RecursiveClassExpressionVisitorBase {

        private IRI oldObject;
        private IRI newObject;

        protected ClassRewritingVisitor(OWLDataFactory factory, IRI oldObject, IRI newObject) {
            super(factory);
            this.oldObject = oldObject;
            this.newObject = newObject;
        }

        @Override
        public OWLClassExpression visit(OWLClass ce) {
            if ( ce.getIRI().equals(oldObject) ) {
                return factory.getOWLClass(newObject);
            } else {
                return factory.getOWLClass(ce.getIRI());
            }
        }
    }

    /*
     * Rewrite a class expression to change any reference to a given object property
     * to reference to another property.
     */
    private class PropertyRewritingVisitor extends RecursiveClassExpressionVisitorBase {
        private IRI oldProperty;
        private IRI newProperty;

        protected PropertyRewritingVisitor(OWLDataFactory factory, IRI oldProperty, IRI newProperty) {
            super(factory);
            this.oldProperty = oldProperty;
            this.newProperty = newProperty;
        }

        @Override
        public OWLObjectPropertyExpression visit(OWLObjectPropertyExpression pe) {
            if ( pe.isNamed() ) {
                if ( pe.asOWLObjectProperty().getIRI().equals(oldProperty) ) {
                    return factory.getOWLObjectProperty(newProperty);
                }
            }
            return pe;
        }
    }

    /*
     * Rewrite all logical axioms to change any reference to a given class to a
     * reference to another class.
     */
    private class AxiomRewritingVisitor extends OWLAxiomVisitorExAdapter<OWLAxiom> {

        private ClassRewritingVisitor rewriter;
        private OWLDataFactory factory;

        public AxiomRewritingVisitor(OWLDataFactory factory, IRI oldClass, IRI newClass) {
            super(null);
            rewriter = new ClassRewritingVisitor(factory, oldClass, newClass);
            this.factory = factory;
        }

        @Override
        public OWLAxiom doDefault(OWLAxiom axiom) {
            return null;
        }

        @Override
        public OWLAxiom visit(OWLSubClassOfAxiom axiom) {
            return factory.getOWLSubClassOfAxiom(axiom.getSubClass().accept(rewriter),
                    axiom.getSuperClass().accept(rewriter), axiom.getAnnotations());
        }

        @Override
        public OWLAxiom visit(OWLEquivalentClassesAxiom axiom) {
            HashSet<OWLClassExpression> equivs = new HashSet<OWLClassExpression>();
            for ( OWLClassExpression ce : axiom.getClassExpressions() ) {
                equivs.add(ce.accept(rewriter));
            }
            return factory.getOWLEquivalentClassesAxiom(equivs, axiom.getAnnotations());
        }

        @Override
        public OWLAxiom visit(OWLDisjointClassesAxiom axiom) {
            HashSet<OWLClassExpression> disjoints = new HashSet<OWLClassExpression>();
            for ( OWLClassExpression ce : axiom.getClassExpressions() ) {
                disjoints.add(ce.accept(rewriter));
            }
            return factory.getOWLDisjointClassesAxiom(disjoints, axiom.getAnnotations());
        }

        @Override
        public OWLAxiom visit(OWLDisjointUnionAxiom axiom) {
            HashSet<OWLClassExpression> union = new HashSet<OWLClassExpression>();
            for ( OWLClassExpression ce : axiom.getClassExpressions() ) {
                union.add(ce.accept(rewriter));
            }
            return factory.getOWLDisjointUnionAxiom(axiom.getOWLClass().accept(rewriter).asOWLClass(), union,
                    axiom.getAnnotations());
        }
    }
}
