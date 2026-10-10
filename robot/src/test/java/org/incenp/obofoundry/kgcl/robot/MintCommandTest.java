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

package org.incenp.obofoundry.kgcl.robot;

import org.junit.jupiter.api.Test;

public class MintCommandTest {

    private final static String idRangeFile = "src/test/resources/idranges.owl";

    @Test
    void testReallocateWithExplicitRange() {
        runCommand("temporary-ids.ofn", "reallocated-ids.ofn", "--minted-id-prefix", "https://example.org/",
                "--pad-width", "4", "--min-id", "501");
    }

    @Test
    void testReallocateWithIDRangePolicy() {
        runCommand("temporary-ids.ofn", "reallocated-ids.ofn", "--id-range-file", idRangeFile, "--id-range-name",
                "Bob");
    }

    @Test
    void testKeepDeprecated() {
        runCommand("temporary-ids.ofn", "reallocated-ids-keep-deprecated.ofn", "--id-range-file", idRangeFile,
                "--id-range-name", "Bob", "--keep-deprecated");
    }

    @Test
    void testMintedFrom() {
        runCommand("temporary-ids.ofn", "reallocated-ids-minted-from.ofn", "--id-range-file", idRangeFile,
                "--id-range-name", "Bob", "--minted-from-property", "https://example.org/properties#mintedFrom");
    }

    private void runCommand(String inputFile, String outputFile, String... extra) {
        TestUtils.runCommand("mint", inputFile, outputFile, extra);
    }
}
