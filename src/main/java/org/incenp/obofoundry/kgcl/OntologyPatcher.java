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
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

/**
 * Apply KGCL changes to an ontology. This class is intended to be at a slightly
 * higher level than {@link Change2OwlVisitor}.
 */
public class OntologyPatcher implements Change2OwlRejectListener {

    private OWLOntology ontology;
    private Change2OwlVisitor visitor;
    private int nRejected;

    /**
     * Create a new instance to update the specified ontology.
     * 
     * @param ontology The ontology that changes should be applied to.
     */
    public OntologyPatcher(OWLOntology ontology) {
        this.ontology = ontology;
        visitor = new Change2OwlVisitor(ontology);
        visitor.addRejectListener(this);
    }

    /**
     * Apply the specified changes to the target ontology.
     * 
     * @param changes        The changes to apply.
     * @param noPartialApply If {@code true}, changes will only be applied if they
     *                       can be applied; otherwise, changes that can be applied
     *                       will be applied, rejected changes will be ignored.
     */
    public void apply(List<Change> changes, boolean noPartialApply) {
        ArrayList<OWLOntologyChange> owlChanges = new ArrayList<OWLOntologyChange>();
        nRejected = 0;
        for ( Change change : changes ) {
            owlChanges.addAll(change.accept(visitor));
        }

        if ( owlChanges.size() > 0 ) {
            if ( nRejected == 0 || !noPartialApply ) {
                ontology.getOWLOntologyManager().applyChanges(owlChanges);
            }
        }
    }

    @Override
    public void rejected(Change change, String reason) {
        nRejected += 1;
    }
}
