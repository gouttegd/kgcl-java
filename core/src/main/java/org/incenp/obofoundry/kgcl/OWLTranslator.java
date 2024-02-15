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

package org.incenp.obofoundry.kgcl;

import java.util.ArrayList;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

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

}
