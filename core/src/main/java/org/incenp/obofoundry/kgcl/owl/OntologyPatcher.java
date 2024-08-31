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

package org.incenp.obofoundry.kgcl.owl;

import java.util.ArrayList;
import java.util.List;

import org.incenp.obofoundry.kgcl.IPatcher;
import org.incenp.obofoundry.kgcl.RejectedChange;
import org.incenp.obofoundry.kgcl.RejectedChangeListener;
import org.incenp.obofoundry.kgcl.model.Change;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * A class to apply KGCL-described changes to a OWL API ontology. This class is
 * intended to be at a slightly higher level than {@link DirectOWLTranslator} and
 * as such is the preferred way of modifying an ontology with KGCL.
 * <p>
 * Typical usage:
 * 
 * <pre>
 * List&lt;Change&gt; changeSet = ... ;
 * OWLOntology ontology = ... ;
 * 
 * OntologyPatcher patcher = new OntologyPatcher(ontology);
 * if ( patcher.apply(changeSet) ) {
 *     // All changes were successfully applied
 *     ontology.saveOntology(...);
 * } else {
 *     // Some changes at least could not be applied
 *     for ( RejectedChange rc : patcher.getRejectedChanges() ) {
 *         System.err.printf("Change rejected: %s\n", rc.getReason());
 *     }
 * }
 * </pre>
 */
public class OntologyPatcher implements IPatcher, RejectedChangeListener {

    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private OWLTranslator translator;
    private ArrayList<RejectedChange> rejectedChanges;
    private boolean isProvisional;

    /**
     * Creates a new instance to update the specified ontology.
     * 
     * @param ontology The ontology that changes should be applied to.
     * @param reasoner The reasoner to use (needed for operations such as
     *                 {@code NodeDeepening} and {@code NodeShallowing}).
     */
    public OntologyPatcher(OWLOntology ontology, OWLReasoner reasoner) {
        this.ontology = ontology;
        this.reasoner = reasoner;
        rejectedChanges = new ArrayList<RejectedChange>();
        isProvisional = false;
    }

    /**
     * Sets this patcher object in "provisional" mode, where changes are recorded in
     * the ontology rather applied.
     * 
     * @param provisional {@code true} to enable "provisional" mode, {@code false}
     *                    for "normal" mode.
     */
    public void setProvisional(boolean provisional) {
        isProvisional = provisional;
        if ( translator != null ) {
            translator = null;
        }
    }

    @Override
    public boolean apply(Change change) {
        List<OWLOntologyChange> owlChanges = change.accept(getTranslator());
        if ( owlChanges.size() > 0 ) {
            ontology.getOWLOntologyManager().applyChanges(owlChanges);
            return true;
        }

        return false;
    }

    @Override
    public boolean apply(List<Change> changes, boolean noPartialApply) {
        ArrayList<OWLOntologyChange> owlChanges = new ArrayList<OWLOntologyChange>();
        int nRejected = rejectedChanges.size();
        for ( Change change : changes ) {
            owlChanges.addAll(change.accept(getTranslator()));
        }

        if ( owlChanges.size() > 0 ) {
            if ( rejectedChanges.size() == nRejected || !noPartialApply ) {
                ontology.getOWLOntologyManager().applyChanges(owlChanges);
            }
        }

        return rejectedChanges.size() == nRejected;
    }

    @Override
    public boolean hasRejectedChanges() {
        return !rejectedChanges.isEmpty();
    }

    @Override
    public List<RejectedChange> getRejectedChanges() {
        return rejectedChanges;
    }

    /**
     * This method is called whenever a change is rejected. For internal use only,
     * it should never be called by client code.
     */
    @Override
    public void rejected(Change change, String reason) {
        rejectedChanges.add(new RejectedChange(change, reason));
    }

    private OWLTranslator getTranslator() {
        if ( translator == null ) {
            translator = isProvisional ? new ProvisionalOWLTranslator(ontology, reasoner)
                    : new DirectOWLTranslator(ontology, reasoner);
            translator.addRejectListener(this);
        }
        return translator;
    }
}
