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
