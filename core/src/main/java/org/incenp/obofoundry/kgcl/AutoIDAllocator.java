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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.incenp.obofoundry.dicer.IAutoIDGenerator;
import org.incenp.obofoundry.dicer.IDNotFoundException;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.Edge;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeChange;
import org.incenp.obofoundry.kgcl.model.NodeDeepening;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeShallowing;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;

/**
 * Helper object to assign automatically generated IDs to KGCL change objects.
 * This class will inspect KGCL change objects and replace occurrences of IDs in
 * the <em>https://w3id.org/kgcl/autoid/</em> namespace by automatically
 * generated IDs.
 */
public class AutoIDAllocator extends ChangeVisitorBase<Void> {

    public final static String AUTOID_BASE_IRI = "https://w3id.org/kgcl/autoid/";

    private IAutoIDGenerator idGenerator;
    private HashMap<String, String> idMap = new HashMap<String, String>();
    private HashSet<String> unallocatedIDs = new HashSet<String>();

    /**
     * Creates a new instance.
     * 
     * @param generator The ID generator that will produce the IDs to assign.
     */
    public AutoIDAllocator(IAutoIDGenerator generator) {
        idGenerator = generator;
    }

    /**
     * Assigns automatic IDs in the given list of change objects. The objects are
     * modified in place.
     * 
     * @param changes The list of change objects to update with automatically
     *                assigned IDs.
     * @return {@code true} if all required IDs have been successfully generated,
     *         otherwise {@code false}.
     */
    public boolean reallocate(List<Change> changes) {
        unallocatedIDs.clear();

        for ( Change change : changes ) {
            change.accept(this);
        }

        return unallocatedIDs.isEmpty();
    }

    public Set<String> getUnallocatedIDs() {
        return unallocatedIDs;
    }

    private boolean isAuto(String id) {
        return id.startsWith(AUTOID_BASE_IRI);
    }

    private String getAutoID(String id) {
        String autoID = idMap.get(id);
        if ( autoID == null ) {
            if ( idGenerator != null ) {
                try {
                    autoID = idGenerator.nextID();
                } catch ( IDNotFoundException e ) {
                }
            }
            if ( autoID == null ) {
                unallocatedIDs.add(id);
                autoID = id;
            }
            idMap.put(id, autoID);
        }
        return autoID;
    }

    @Override
    protected Void doDefault(Change change) {
        if ( change instanceof NodeChange ) {
            visit((NodeChange) change);
        }
        return null;
    }

    @Override
    public Void visit(NodeChange change) {
        visit(change.getAboutNode());
        return null;
    }

    @Override
    public Void visit(EdgeCreation change) {
        visit(change.getSubject());
        visit(change.getPredicate());
        visit(change.getObject());
        visit(change.getAboutEdge());
        return null;
    }

    @Override
    public Void visit(PlaceUnder change) {
        return visit((EdgeCreation) change);
    }

    @Override
    public Void visit(EdgeDeletion change) {
        visit(change.getSubject());
        visit(change.getPredicate());
        visit(change.getObject());
        visit(change.getAboutEdge());
        return null;
    }

    @Override
    public Void visit(RemoveUnder change) {
        return visit((EdgeDeletion) change);
    }

    @Override
    public Void visit(NodeMove change) {
        visit(change.getAboutEdge());
        if ( change.getOldValue() != null && isAuto(change.getOldValue()) ) {
            change.setOldValue(getAutoID(change.getOldValue()));
        }
        if ( change.getNewValue() != null && isAuto(change.getNewValue()) ) {
            change.setNewValue(getAutoID(change.getNewValue()));
        }
        return null;
    }

    @Override
    public Void visit(NodeDeepening change) {
        return visit((NodeMove) change);
    }

    @Override
    public Void visit(NodeShallowing change) {
        return visit((NodeMove) change);
    }

    @Override
    public Void visit(PredicateChange change) {
        visit(change.getAboutEdge());
        if ( change.getOldValue() != null && isAuto(change.getOldValue()) ) {
            change.setOldValue(getAutoID(change.getOldValue()));
        }
        if ( change.getNewValue() != null && isAuto(change.getNewValue()) ) {
            change.setNewValue(getAutoID(change.getNewValue()));
        }
        return null;
    }

    private void visit(Node node) {
        if ( node != null && isAuto(node.getId()) ) {
            node.setId(getAutoID(node.getId()));
        }
    }

    private void visit(Edge edge) {
        if ( edge != null ) {
            visit(edge.getSubject());
            visit(edge.getPredicate());
            visit(edge.getObject());
        }
    }
}
