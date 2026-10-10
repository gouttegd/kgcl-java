/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2024 Damien Goutte-Gattat
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
