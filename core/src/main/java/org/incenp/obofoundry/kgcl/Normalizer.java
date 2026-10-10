/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
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

import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.Edge;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;

/**
 * Normalizes a KGCL changeset.
 * <p>
 * Some KGCL changes can be described in more than one way. For example, the
 * edge to create in a <code>EdgeCreation</code> change can be described using
 * either the <code>about_edge</code> slot, or using the <code>subject</code>,
 * <code>predicate</code>, and <code>object</code> slots. This is most likely an
 * error in the KGCL model, but one that has never received any attention. In
 * KGCL-Java, we consider that the “correct” way to describe the edge is through
 * the <code>about_edge</code> slot.
 * <p>
 * This object is intended to fix this kind of improper usage of KGCL slots.
 * 
 * @see <a href="https://github.com/INCATools/kgcl/issues/53">KGCL issue #53</a>
 */
public class Normalizer extends ChangeVisitorBase<Void> {

    @Override
    public Void visit(EdgeCreation v) {
        Edge edge = v.getAboutEdge();
        if ( edge == null ) {
            edge = new Edge();
            edge.setSubject(v.getSubject());
            edge.setPredicate(v.getPredicate());
            edge.setObject(v.getObject());
            v.setAboutEdge(edge);
            v.setSubject(null);
            v.setPredicate(null);
            v.setObject(null);
        }
        return null;
    }

    @Override
    public Void visit(PlaceUnder v) {
        return visit((EdgeCreation) v);
    }

    @Override
    public Void visit(EdgeDeletion v) {
        Edge edge = v.getAboutEdge();
        if ( edge == null ) {
            edge = new Edge();
            edge.setSubject(v.getSubject());
            edge.setPredicate(v.getPredicate());
            edge.setObject(v.getObject());
            v.setAboutEdge(edge);
            v.setSubject(null);
            v.setPredicate(null);
            v.setObject(null);
        }
        return null;
    }

    @Override
    public Void visit(RemoveUnder v) {
        return visit((EdgeDeletion) v);
    }

    /**
     * Normalizes all changes in the given list.
     * 
     * @param changeset The list of change objects to normalizes.
     * @return The input list after normalization.
     */
    public static List<Change> normalize(List<Change> changeset) {
        Normalizer visitor = new Normalizer();
        changeset.forEach((c) -> c.accept(visitor));
        return changeset;
    }
}
