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

import org.incenp.obofoundry.kgcl.model.Change;

/**
 * A change that could not be applied to the intended target graph.
 * <p>
 * A change that is perfectly valid according to the KGCL model may still fail
 * to be applied to a knowledge graph, if the contents of the knowledge graph
 * does not match what the change expects. For example:
 * <ul>
 * <li>{@code obsolete EX:0001} will fail if the graph does not contain a node
 * with ID {@code EX:0001};
 * <li>{@code rename EX:0001 from "old label" to "new label"} if the current
 * label of node {@code EX:0001} is not {@code old label}.
 * </ul>
 * <p>
 * This class is merely a container for a tuple associating a KGCL change that
 * could not be applied and a human-readable message explaining what the
 * mismatch was.
 */
public class RejectedChange {

    private Change change;
    private String reason;

    /**
     * Creates a new instance.
     * 
     * @param change The change that could not be applied.
     * @param reason The reason for rejecting the change.
     */
    public RejectedChange(Change change, String reason) {
        this.change = change;
        this.reason = reason;
    }

    /**
     * Gets the rejected change.
     * 
     * @return The change that could not be applied.
     */
    public Change getChange() {
        return change;
    }

    /**
     * Gets the reason for rejection.
     * 
     * @return A human-readable message explaining why the change could not be
     *         applied.
     */
    public String getReason() {
        return reason;
    }
}
