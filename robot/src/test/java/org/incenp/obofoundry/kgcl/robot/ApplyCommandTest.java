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
