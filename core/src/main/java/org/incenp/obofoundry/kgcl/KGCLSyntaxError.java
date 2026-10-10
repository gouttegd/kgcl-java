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

/**
 * A syntax error encountered when parsing a KGCL program.
 */
public class KGCLSyntaxError {
    private int line;
    private int position;
    private String message;

    /**
     * Creates a new instance.
     * 
     * @param line     The line number where the error occurred.
     * @param position The position in the line where the error occurred.
     * @param message  The error message from the ANTLR parser.
     */
    public KGCLSyntaxError(int line, int position, String message) {
        this.line = line;
        this.position = position;
        this.message = message;
    }

    /**
     * Gets the line where the error occurred.
     * 
     * @return The 1-based index of the offending line, starting from the beginning
     *         of the input.
     */
    public int getLine() {
        return line;
    }

    /**
     * Gets the position in the line where the error occurred.
     * 
     * @return The 0-based index of the offending character, starting from the
     *         beginning of the line.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Gets the error message.
     * 
     * @return The error message from the ANTLR parser.
     */
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("line %d, column %d: %s", line, position, message);
    }
}
