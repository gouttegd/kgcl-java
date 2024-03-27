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

package org.incenp.obofoundry.kgcl.robot;

import org.junit.jupiter.api.Test;

public class MintCommandTest {

    private final static String idRangeFile = "../core/src/test/resources/idranges.owl";

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
