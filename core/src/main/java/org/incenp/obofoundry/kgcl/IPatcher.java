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

import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;

/**
 * An object to apply KGCL-described changes to a knowledge graph.
 */
public interface IPatcher {

    /**
     * Applies a single change to the knowledge graph.
     * 
     * @param change The change to apply.
     * @return {@code true} if the change has been successfully applied, or
     *         {@code false} if the change has been rejected.
     */
    public boolean apply(Change change);

    /**
     * Applies a changeset to the knowledge graph. This method shall try to apply
     * all changes in the given list. If some changes cannot be applied, they shall
     * be ignored (and a call to {@link #getRejectedChanges()} shall return the
     * concerned changes), while the remaining changes shall be applied normally.
     *
     * @param changes The list of changes to apply.
     * @return {@code true} if all changes were applied successfully, otherwise
     *         {@code false}.
     */
    default public boolean apply(List<Change> changes) {
        return apply(changes, false);
    }

    /**
     * Applies a changeset to the knowledge graph.
     * 
     * @param changes        The list of changes to apply.
     * @param noPartialApply If {@code true}, changes shall only be applied if all
     *                       the changes in the list can be applied; if
     *                       {@code false}, changes that can be applied will be
     *                       effectively applied, while rejected changes will be
     *                       ignored.
     * @return {@code true} if all changes were applied successfully, otherwise
     *         {@code false}.
     */
    public boolean apply(List<Change> changes, boolean noPartialApply);

    /**
     * Indicates whether changes have been rejected by this patcher.
     * 
     * @return {@code true} if at least one change has ever been rejected in the
     *         lifetime of this object, otherwise {@code false}.
     */
    public boolean hasRejectedChanges();

    /**
     * Gets the changes that have been rejected by this patcher.
     * <p>
     * A change may be “rejected” if the contents of the knowledge graph does not
     * match what is expected by the change. For example, a change that attempts to
     * modify a node will be rejected if the node does not exist in the knowledge
     * graph.
     * 
     * @return A list of all the changes that have been rejected in the lifetime of
     *         this patcher.
     */
    public List<RejectedChange> getRejectedChanges();
}
