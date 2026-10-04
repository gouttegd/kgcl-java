/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
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
