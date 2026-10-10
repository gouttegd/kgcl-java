/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023,2024,2026 Damien Goutte-Gattat
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.incenp.obofoundry.kgcl.model.DeprecationProfile;
import org.obolibrary.obo2owl.Obo2OWLConstants.Obo2OWLVocabulary;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * Deprecates entities within an ontology.
 * <p>
 * This class provides a configurable way of deprecating entities. The
 * configuration is based on Protégé’s concept of ”deprecation profile”.
 */
public class EntityDeprecator {

    private OWLOntology ontology;
    private OWLDataFactory factory;

    private boolean removeDefAxioms = true;
    private boolean rewireRefAxioms = true;
    private boolean removeAnnots = true;
    private Map<IRI, String> annotPrefixes = new HashMap<>();
    private IRI replacedIRI = Obo2OWLVocabulary.IRI_IAO_0100001.getIRI();
    private IRI considerIRI = Obo2OWLVocabulary.IRI_OIO_consider.getIRI();
    private String onlyLang = "en";

    /**
     * Creates a new instance with the default deprecation profile.
     * <p>
     * The default deprecation profile corresponds to the original behaviour of
     * KGCL-Java.
     * 
     * @param ontology The ontology in which entities are to be deprecated.
     */
    public EntityDeprecator(OWLOntology ontology) {
        this(ontology, null);
    }

    /**
     * Creates a new instance with the given deprecation profile.
     * 
     * @param ontology The ontology in which entities are to be deprecated.
     * @param profile  The deprecation profile to use.
     */
    public EntityDeprecator(OWLOntology ontology, DeprecationProfile profile) {
        this.ontology = ontology;
        factory = ontology.getOWLOntologyManager().getOWLDataFactory();

        String labelPrefix = "obsolete ";

        if ( profile != null ) {
            removeDefAxioms = maybeGetBool(profile.getRemoveLogicalDefinition());
            rewireRefAxioms = maybeGetBool(profile.getRewireAxioms());
            removeAnnots = maybeGetBool(profile.getRemoveAnnotationAssertions());
            onlyLang = profile.getOnlyLanguage();

            labelPrefix = maybeGetPrefix(profile.getLabelPrefix());
            for ( String labelProperty : profile.getLabelPropertyIris(true) ) {
                annotPrefixes.put(IRI.create(labelProperty), labelPrefix);
            }

            String annotPrefix = maybeGetPrefix(profile.getAnnotationValuePrefix());
            for ( String preserved : profile.getPreservedAnnotationAssertionPropertyIris(true) ) {
                annotPrefixes.put(IRI.create(preserved), annotPrefix);
            }

            replacedIRI = maybeGetIRI(profile.getReplacedByAnnotationPropertyIri());
            considerIRI = maybeGetIRI(profile.getAlternateEntityAnnotationPropertyIri());
        }

        annotPrefixes.put(OWLRDFVocabulary.RDFS_LABEL.getIRI(), labelPrefix);
    }

    /**
     * Deprecates an entity.
     * 
     * @param target      The IRI of the entity to deprecate.
     * @param replacement The IRI of the entity that directly replaces the entity
     *                       to deprecate. May be {@code null}.
     * @param alternatives   The IRIs of the entities that are potential
     *                       (non-direct) replacements for the entity to deprecate.
     *                       May be {@code null}.
     * @param removed        This will receive the axioms that are to be removed
     *                       from the ontology.
     * @param added          This will receive the axioms that are to be added to
     *                       the ontology.
     */
    public void deprecate(IRI target, IRI replacement, Set<IRI> alternatives, Set<OWLAxiom> removed,
            Set<OWLAxiom> added) {
        updateAnnotations(target, removed, added);
        addDeprecationAxioms(target, replacement, alternatives, added);

        if ( removeDefAxioms ) {
            removeLogicalAxioms(target, removed);
        }

        if ( rewireRefAxioms ) {
            rewireAxioms(target, replacement, alternatives, removed, added);
        }
    }

    private void updateAnnotations(IRI target, Set<OWLAxiom> removed, Set<OWLAxiom> added) {
        Map<IRI, ForeignAnnotInfo> foreignAnnots = new HashMap<>();

        for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(target) ) {
            IRI propertyIRI = ax.getProperty().getIRI();
            if ( annotPrefixes.containsKey(propertyIRI) && ax.getValue().isLiteral() ) {
                String prefix = annotPrefixes.get(propertyIRI);
                if ( prefix == null ) {
                    // No prefixing, so we just keep the annotation as it is
                    continue;
                }

                String oldLabel = ax.getValue().asLiteral().get().getLiteral();
                String oldLang = ax.getValue().asLiteral().get().getLang();
                if ( onlyLang != null && !oldLang.isEmpty() && !oldLang.startsWith(onlyLang) ) {
                    // Set the foreign axiom aside for now
                    foreignAnnots.computeIfAbsent(propertyIRI, (iri) -> new ForeignAnnotInfo()).axioms.add(ax);
                } else {
                    // Prefix the annotation value
                    removed.add(ax);
                    added.add(factory.getOWLAnnotationAssertionAxiom(ax.getProperty(), target,
                            factory.getOWLLiteral(prefix + oldLabel, oldLang), ax.getAnnotations()));
                    foreignAnnots.computeIfAbsent(propertyIRI, (iri) -> new ForeignAnnotInfo()).keep = false;
                }
            } else if ( removeAnnots ) {
                removed.add(ax);
            }
        }

        if ( onlyLang != null ) {
            for ( ForeignAnnotInfo fai : foreignAnnots.values() ) {
                if ( !fai.keep ) {
                    removed.addAll(fai.axioms);
                }
            }
        }
    }

    private void addDeprecationAxioms(IRI target, IRI replacement, Set<IRI> alternatives, Set<OWLAxiom> added) {
        // Add deprecation annotation property
        OWLAnnotationProperty prop = factory.getOWLAnnotationProperty(OWLRDFVocabulary.OWL_DEPRECATED.getIRI());
        added.add(factory.getOWLAnnotationAssertionAxiom(prop, target, factory.getOWLLiteral(true)));

        // Add "replaced by" or "consider" annotations
        if ( replacement != null && replacedIRI != null ) {
            prop = factory.getOWLAnnotationProperty(replacedIRI);
            added.add(factory.getOWLAnnotationAssertionAxiom(prop, target, replacement));
        } else if ( alternatives != null && considerIRI != null ) {
            prop = factory.getOWLAnnotationProperty(considerIRI);
            for ( IRI alt : alternatives ) {
                added.add(factory.getOWLAnnotationAssertionAxiom(prop, target, alt));
            }
        }
    }

    private void removeLogicalAxioms(IRI target, Set<OWLAxiom> removed) {
        // FIXME: Support removing logical axioms for something else than a class
        for ( OWLAxiom ax : ontology.getAxioms(factory.getOWLClass(target), Imports.INCLUDED) ) {
            removed.add(ax);
        }
    }

    private void rewireAxioms(IRI target, IRI replacement, Set<IRI> alternatives, Set<OWLAxiom> removed,
            Set<OWLAxiom> added) {
        if ( replacement != null && replacedIRI != null ) {
            // Direct replacement, we can rewire all axioms referring to the obsolete class
            // to make them refer to the replacement class
            // FIXME: Support rewiring for something else than a class
            // FIXME: Support reparenting to another class
            AxiomRewritingVisitor rewriter = new AxiomRewritingVisitor(factory, target, replacement);
            Set<OWLClassAxiom> definingAxioms = ontology.getAxioms(factory.getOWLClass(target), Imports.INCLUDED);
            for ( OWLAxiom axiom : ontology.getReferencingAxioms(target, Imports.INCLUDED) ) {
                if ( removed.contains(axiom) ) {
                    // Do not rewire axioms that are already slated for removal
                    continue;
                }
                if ( definingAxioms.contains(axiom) ) {
                    // If logical definition axioms have not been removed, do not rewire them
                    continue;
                }

                OWLAxiom rewired = axiom.accept(rewriter);
                if ( rewired != null ) {
                    removed.add(axiom);
                    added.add(rewired);
                }
            }
        } else if ( alternatives != null && !alternatives.isEmpty() && considerIRI != null ) {
            /*
             * FIXME: It’s unclear to me what should be done with referencing axioms in this
             * case. Obviously we cannot rewire them, but should we remove them or leave
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
            // No replacement or alternatives, the expectation from the KGCL folks is that
            // all referencing axioms should be removed
            Set<OWLClassAxiom> definingAxioms = ontology.getAxioms(factory.getOWLClass(target), Imports.INCLUDED);
            for ( OWLAxiom axiom : ontology.getReferencingAxioms(target, Imports.INCLUDED) ) {
                if ( axiom instanceof OWLDeclarationAxiom ) {
                    continue; // Always keep declaration
                } else if ( axiom instanceof OWLAnnotationAssertionAxiom ) {
                    continue; // Annotations have already been dealt with
                } else if ( definingAxioms.contains(axiom) ) {
                    continue; // Do not remove logical definition axioms
                }
                removed.add(axiom);
            }
        }
    }

    private boolean maybeGetBool(Boolean b) {
        return b != null ? b.booleanValue() : false;
    }

    private IRI maybeGetIRI(String s) {
        return s != null ? IRI.create(s) : null;
    }

    private String maybeGetPrefix(String orig) {
        if ( orig == null || orig.isEmpty() || orig.isBlank() ) {
            return null;
        }
        String prefix = orig.trim();
        return prefix.endsWith("_") || prefix.endsWith("-") ? prefix : prefix + " ";
    }

    // Helper structure for the updateAnnotations method, to keep track of the
    // "foreign axioms" for any given property
    private class ForeignAnnotInfo {
        boolean keep = true; // Whether the foreign axioms should be preserved from removal
        Set<OWLAxiom> axioms = new HashSet<>(); // All found foreign axioms
    }
}
