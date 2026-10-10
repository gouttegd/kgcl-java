/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2024,2026 Damien Goutte-Gattat
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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ApplyCommandTest {

    @Test
    void testApplyOneChangeFromCommandLine() {
        runCommand("pizza.ofn", "pizza-no-sultana-topping.ofn", "--kgcl", "obsolete pizza:SultanaTopping");
    }

    @Test
    void testApplySeveralChangesFromCommandLine() {
        runCommand("pizza.ofn", "pizza-no-sultana-topping-no-reine.ofn", "--kgcl", "obsolete pizza:SultanaTopping",
                "--kgcl", "obsolete pizza:LaReine");
    }

    @Test
    void testApplyChangeFromFile() {
        runCommand("pizza.ofn", "pizza-no-sultana-topping.ofn", "--kgcl-file",
                "src/test/resources/obsolete-sultana-topping.kgcl");
    }

    @Test
    void testApplyChangeFromYAMLFile() {
        runCommand("pizza.ofn", "pizza-no-sultana-topping.ofn", "--kgcl-yaml",
                "src/test/resources/obsolete-sultana-topping.yaml");
    }

    @Test
    void testApplyChangeFromFileAndCommandLine() {
        runCommand("pizza.ofn", "pizza-no-sultana-topping-no-reine.ofn", "--kgcl-file",
                "src/test/resources/obsolete-sultana-topping.kgcl", "--kgcl", "obsolete pizza:LaReine");
    }

    @Test
    void testApplyPartialChange() {
        runCommand("pizza.ofn", "pizza-no-sultana-topping.ofn", "--kgcl", "obsolete pizza:SultanaTopping", "--kgcl",
                "obsolete pizza:InexistingPizza", "--no-reject-file");
    }

    @Test
    void testApplyNoPartialChange() {
        runCommand("pizza.ofn", "pizza.ofn", "--kgcl", "obsolete pizza:SultanaTopping", "--kgcl",
                "obsolete pizza:InexistingPizza", "--no-partial-apply", "--no-reject-file");
    }

    @Test
    void testExplicitRejectFile() {
        runCommand("pizza.ofn", null, "--kgcl", "obsolete pizza:InexistingPizza", "--reject-file",
                "src/test/resources/explicit-reject-file.kgcl");
        checkOutput("reject-inexisting-pizza.kgcl", "explicit-reject-file.kgcl");
    }

    @Test
    void testImplicitRejectFile() {
        runCommand("pizza.ofn", null, "--kgcl-file", "src/test/resources/obsolete-inexisting-pizza.kgcl");
        checkOutput("reject-inexisting-pizza.kgcl", "obsolete-inexisting-pizza.kgcl.rej");
    }

    @Test
    void testExplicitOverImplicitRejectFile() {
        runCommand("pizza.ofn", null, "--kgcl-file", "src/test/resources/obsolete-inexisting-pizza.kgcl",
                "--reject-file", "src/test/resources/explicit-reject-file.kgcl");
        checkOutput("reject-inexisting-pizza.kgcl", "explicit-reject-file.kgcl");
    }

    @Test
    void testExplicitNoRejectFile() {
        runCommand("pizza.ofn", null, "--kgcl-file", "src/test/resources/obsolete-inexisting-pizza.kgcl",
                "--no-reject-file");
        File rejectFile = new File("src/test/resources/obsolete-inexisting-pizza.kgcl.rej");
        Assertions.assertFalse(rejectFile.exists());
    }

    @Test
    void testNoDefaultLanguageTag() {
        runCommand("pizza.ofn", "pizza-renamed-reine-all-langs.ofn", "--kgcl",
                "rename pizza:LaReine from 'LaReine' to 'TheQueen'");
    }

    @Test
    void testDefaultLanguageTag() {
        runCommand("pizza.ofn", "pizza-renamed-reine-english-only.ofn", "--kgcl",
                "rename pizza:LaReine from 'LaReine' to 'TheQueen'", "--default-new-language", "en");
    }

    @Test
    void testDefaultLanguageTagWithExplicitDatatype() {
        runCommand("pizza.ofn", "pizza-renamed-reine-no-lang.ofn", "--kgcl",
                "rename pizza:LaReine from 'LaReine' to 'TheQueen'^^xsd:string", "--default-new-language", "en");
    }

    @Test
    void testCreateNewOntology() {
        runCommand("dont-care.ofn", "from-scratch.ofn", "--create", "--add-prefix", "EX: https://example.org/",
                "--kgcl", "create class EX:0001 'class 1'", "--kgcl", "create class EX:0002 'class 2'", "--kgcl",
                "create relation EX:0101 'object property 1'", "--kgcl", "create edge EX:0001 EX:0101 EX:0002");
    }

    @Test
    void testIdAsLabel() {
        runCommand("pizza.ofn", "pizza-no-sultana-topping.ofn", "--kgcl", "obsolete 'SultanaTopping'");
    }

    private void runCommand(String inputFile, String outputFile, String... extra) {
        TestUtils.runCommand("apply", inputFile, outputFile, extra);
    }

    private void checkOutput(String expectedFile, String actualFile) {
        File expected = new File("src/test/resources/" + expectedFile);
        File actual = new File("src/test/resources/" + actualFile);
        boolean same = false;
        try {
            same = FileUtils.contentEquals(expected, actual);
        } catch ( IOException e ) {
            Assertions.fail(e);
        }
        Assertions.assertTrue(same);
        if ( same ) {
            actual.delete();
        }
    }
}
