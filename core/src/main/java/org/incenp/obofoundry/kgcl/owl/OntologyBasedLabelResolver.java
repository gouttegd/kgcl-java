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

import java.util.HashMap;
import java.util.HashSet;

import org.incenp.obofoundry.kgcl.SimpleLabelResolver;
import org.obolibrary.obo2owl.Obo2OWLConstants;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/**
 * An object to resolve labels into identifiers using the {@code rdfs:label}
 * annotations of an ontology’s entities.
 */
public class OntologyBasedLabelResolver extends SimpleLabelResolver
{

    private HashMap<String, String> idMap = new HashMap<String, String>();
    private OWLOntology ontology;

    /**
     * Creates a new instance to resolve labels based on the contents of the
     * provided ontology.
     * 
     * @param ontology The ontology to use to resolve labels.
     */
    public OntologyBasedLabelResolver(OWLOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public String resolve(String label) {
        // We first lookup in the parent's dictionary, in case the label has a newly
        // minted ID. Such IDs takes precedence over the ontology's contents.
        String resolved = super.resolve(label);
        if ( resolved == null ) {
            if ( idMap.isEmpty() ) {
                // Rather than querying the ontology for each lookup, we build a one-time map of
                // all labels. Since this requires iterating over the entire ontology, we do
                // that lazily, so that we may in fact not have to do it at all if we are never
                // asked to resolve an identifier.
                buildIdMap();
            }
            resolved = idMap.get(label);
        }

        // We accept "is_a" as a shortcut for "rdfs:subClassOf". We check for it only
        // after everything else, to avoid masking another class or relation in the
        // ontology that would have a "is_a" label (admittedly such a label would be a
        // very bad idea, but just in case).
        // FIXME: Ideally, "is_a" should be resolved into "rdfs:subClassOf" when it is
        // used between classes, and into "rdfs:subPropertyOf" when it is used between
        // properties. But providing the resolver with the necessary context to do that
        // would complicate things too much for arguably little gain (it is assumed that
        // most uses of "is_a" will be between classes). So, "is_a" can only be used as
        // a shortcut when dealing with classes. For subsumption relations between
        // properties, "rdfs:subPropertyOf" should be used explicitly.
        if ( resolved == null && label.equals("is_a") ) {
            resolved = OWLRDFVocabulary.RDFS_SUBCLASS_OF.getIRI().toString();
        }

        return resolved;
    }

    private void buildIdMap() {
        HashSet<String> ambiguousLabels = new HashSet<String>();

        for ( OWLEntity entity : ontology.getSignature(Imports.INCLUDED) ) {
            String iri = entity.getIRI().toString();
            for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(entity.getIRI()) ) {
                // We check the provided "label" against both rdfs:label and oboInOwl:shorthand
                // annotations. FIXME: Should we also check against oboInOwl:hasExactSynonym?
                if ( ax.getProperty().isLabel() || ax.getProperty().getIRI()
                        .equals(Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_shorthand.getIRI()) ) {
                    if ( ax.getValue().isLiteral() ) {
                        String label = ax.getValue().asLiteral().get().getLiteral();
                        String existing = idMap.get(label);
                        if ( existing == null ) {
                            idMap.put(label, iri);
                        } else if ( !existing.equals(iri) ) {
                            ambiguousLabels.add(label);
                        }
                    }
                }
            }
        }

        for ( String ambiguous : ambiguousLabels ) {
            idMap.remove(ambiguous);
        }
    }

}
