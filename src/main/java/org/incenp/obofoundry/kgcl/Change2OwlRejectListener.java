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
 * Listener for "rejected change" events -- when the Change2Owl visitor cannot
 * translate a change into OWL axioms because of a mismatch between what the
 * target ontology contains and what the change expects.
 */
public interface Change2OwlRejectListener {

    /**
     * This method is called when a change cannot be applied to an ontology.
     * 
     * @param change The change that is rejected.
     * @param reason A human-readable reason for the rejection.
     */
    void rejected(Change change, String reason);
}