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

package org.incenp.obofoundry.idrange;

import java.io.File;
import java.util.HashMap;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDatatypeDefinitionAxiom;
import org.semanticweb.owlapi.model.OWLDatatypeRestriction;
import org.semanticweb.owlapi.model.OWLFacetRestriction;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.util.OWLDataVisitorExAdapter;
import org.semanticweb.owlapi.vocab.OWLFacet;

/**
 * A parser for OBO-style ID range policy files.
 */
public class IDRangePolicyParser {

    private static final IRI IDPREFIX_IRI = IRI.create("http://purl.obolibrary.org/obo/IAO_0000599");
    private static final IRI IDSFOR_IRI = IRI.create("http://purl.obolibrary.org/obo/IAO_0000598");
    private static final IRI IDDIGITS_IRI = IRI.create("http://purl.obolibrary.org/obo/IAO_0000596");
    private static final IRI ALLOCATEDTO_IRI = IRI.create("http://purl.obolibrary.org/obo/IAO_0000597");

    String filename;

    /**
     * Creates a new instance to parse the specified file.
     * 
     * @param filename The name of the file to parse.
     */
    public IDRangePolicyParser(String filename) {
        this.filename = filename;
    }

    /**
     * Parses the policy file.
     * 
     * @return The ID range policy as read from the file.
     * @throws IDRangePolicyException If any error occurs when parsing the policy
     *                                file.
     */
    public IIDRangePolicy parse() throws IDRangePolicyException {
        OWLOntology ont = null;
        try {
            ont = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File(filename));
        } catch ( OWLOntologyCreationException e ) {
            throw new IDRangePolicyException("Cannot load ID range policy", e);
        }

        IDRangePolicy policy = new IDRangePolicy();
        for ( OWLAnnotation annot : ont.getAnnotations() ) {
            OWLAnnotationValue value = annot.getValue();
            if ( !value.isLiteral() ) {
                continue;
            }

            IRI iri = annot.getProperty().getIRI();
            if ( iri.equals(IDPREFIX_IRI) ) {
                policy.prefix = value.asLiteral().get().getLiteral();
            } else if ( iri.equals(IDSFOR_IRI) ) {
                policy.prefixName = value.asLiteral().get().getLiteral();
            } else if ( iri.equals(IDDIGITS_IRI) ) {
                if ( !value.asLiteral().get().isInteger() ) {
                    throw new IDRangePolicyException(
                            String.format("Invalid iddigits value: %s", value.asLiteral().get().getLiteral()));
                }
                policy.width = value.asLiteral().get().parseInteger();
            }
        }

        if ( policy.prefix == null ) {
            throw new IDRangePolicyException("No prefix specified in ID range policy");
        }

        RangeDatatypeVisitor visitor = new RangeDatatypeVisitor();
        for ( OWLDatatype datatype : ont.getDatatypesInSignature() ) {
            String name = null;
            for ( OWLAnnotationAssertionAxiom ax : ont.getAnnotationAssertionAxioms(datatype.getIRI()) ) {
                if ( ax.getProperty().getIRI().equals(ALLOCATEDTO_IRI) && ax.getValue().isLiteral() ) {
                    name = ax.getValue().asLiteral().get().getLiteral();
                }
            }
            if ( name == null ) {
                continue;
            }

            for ( OWLDatatypeDefinitionAxiom ax : ont.getDatatypeDefinitions(datatype) ) {
                IDRange range = ax.getDataRange().accept(visitor);
                if ( range == null ) {
                    throw new IDRangePolicyException("Invalid range definition");
                }
                policy.ranges.put(name, range);
            }
        }

        return policy;
    }

    private class RangeDatatypeVisitor extends OWLDataVisitorExAdapter<IDRange> {
        public RangeDatatypeVisitor() {
            super(null);
        }

        @Override
        public IDRange visit(OWLDatatypeRestriction node) {
            int lower = 1;
            int upper = -1;

            for ( OWLFacetRestriction restriction : node.getFacetRestrictions() ) {
                if ( restriction.getFacetValue().isInteger() ) {
                    int value = restriction.getFacetValue().parseInteger();

                    OWLFacet facet = restriction.getFacet();
                    if ( facet == OWLFacet.MIN_EXCLUSIVE ) {
                        lower = value + 1;
                    } else if ( facet == OWLFacet.MIN_INCLUSIVE ) {
                        lower = value;
                    } else if ( facet == OWLFacet.MAX_INCLUSIVE ) {
                        upper = value + 1;
                    } else if ( facet == OWLFacet.MAX_EXCLUSIVE ) {
                        upper = value;
                    }
                }
            }

            if ( lower >= 0 && upper > lower ) {
                return new IDRange(lower, upper);
            }

            return null;
        }
    }

    private class IDRangePolicy implements IIDRangePolicy {

        String prefix;
        String prefixName;
        int width;
        HashMap<String, IDRange> ranges = new HashMap<String, IDRange>();

        @Override
        public String getPrefix() {
            return prefix;
        }

        @Override
        public String getPrefixName() {
            return prefixName;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public IDRange getRange(String name) {
            return ranges.get(name);
        }

    }
}
