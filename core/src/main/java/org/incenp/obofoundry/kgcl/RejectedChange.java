/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023 Damien Goutte-Gattat
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
