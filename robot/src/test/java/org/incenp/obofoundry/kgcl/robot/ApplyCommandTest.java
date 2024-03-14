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
import org.geneontology.owl.differ.Differ;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.obolibrary.robot.CommandManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

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

    /*
     * Try running a KGCL-Apply command and check that the output ontology matches
     * what we expect.
     * 
     * inputFile: the input ontology (filename relative to
     * {,../core}/src/test/resources). outputFile: expected output file (likewise);
     * if null, ignore the output.
     */
    private void runCommand(String inputFile, String outputFile, String... extra) {
        File input = new File("/src/test/resources/" + inputFile);
        if ( !input.exists() ) {
            input = new File("../core/src/test/resources/" + inputFile);
        }

        File expectedOutput = null;
        File actualOutput = null;
        if ( outputFile != null ) {
            expectedOutput = new File("src/test/resources/" + outputFile);
            if ( !expectedOutput.exists() ) {
                expectedOutput = new File("../core/src/test/resources/" + outputFile);
            }
            actualOutput = new File("src/test/resources/output-" + outputFile);
        } else {
            actualOutput = new File("dont-care.ofn");
        }

        String[] args = new String[1 + 2 + 2 + extra.length];
        args[0] = "kgcl-apply";
        args[1] = "--input";
        args[2] = input.getPath();
        args[3] = "--output";
        args[4] = actualOutput.getPath();
        for ( int i = 0; i < extra.length; i++ ) {
            args[i + 5] = extra[i];
        }

        CommandManager robot = new CommandManager();
        robot.addCommand("kgcl-apply", new ApplyCommand());
        robot.main(args);

        if ( outputFile != null ) {
            compareOntologies(expectedOutput, actualOutput);
        } else if ( actualOutput.exists() ) {
            actualOutput.delete();
        }
    }

    private void compareOntologies(File expected, File actual) {
        try {
            OWLOntology expectedOntology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(expected);
            OWLOntology actualOntology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(actual);
            Differ.BasicDiff diff = Differ.diff(expectedOntology, actualOntology);
            Assertions.assertTrue(diff.isEmpty());
            if ( diff.isEmpty() ) {
                actual.delete();
            }
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail(e);
        }
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
