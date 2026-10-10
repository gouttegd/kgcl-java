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

/**
 * An object that can resolve labels into proper entity identifiers.
 */
public interface ILabelResolver {

    /**
     * Finds the identifier corresponding to the given label.
     * 
     * @param label The label to resolve.
     * @return The identifier of the entity with the given label, or {@code null} if
     *         the label could not be resolved.
     */
    public String resolve(String label);

    /**
     * Registers a new label-to-identifier mapping.
     * 
     * @param label      The label to register.
     * @param identifier Its corresponding identifier.
     */
    public void add(String label, String identifier);

    /**
     * Mints a new identifier for the given label.
     * <p>
     * This method is used when a KGCL “create” instruction does not include an
     * identifier for the node to be created (e.g.,
     * {@code create class 'my new class'}). It shall return a new identifier for
     * the node to be created.
     * <p>
     * Any subsequent call to {@link #resolve(String)} with the same label shall
     * return the same identifier.
     * 
     * @param label The label for which an identifier is requested.
     * @return The newly minted identifier.
     */
    public String getNewId(String label);
}
