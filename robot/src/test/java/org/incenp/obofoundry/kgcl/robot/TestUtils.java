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

import org.geneontology.owl.differ.Differ;
import org.junit.jupiter.api.Assertions;
import org.obolibrary.robot.CommandManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/*
 * Helper methods to test the ROBOT commands.
 */
public class TestUtils {

    /*
     * Tries running a command from the KGCL plugin and checks that the output
     * ontology matches what we expect.
     * 
     * @param command The command to run ("apply", "mint").
     * 
     * @param inputFile The input ontology (as a filename relative to {@code
     * {,../core}/src/test/resources}.
     * 
     * @param outputFile The expected output file (likewise). If {@code null}, the
     * output is ignored.
     * 
     * @param extra Any extra arguments to pass to the command.
     */
    public static void runCommand(String command, String inputFile, String outputFile, String... extra) {
        File input = new File("src/test/resources/" + inputFile);
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
        args[0] = "kgcl-" + command;
        args[1] = "--input";
        args[2] = input.getPath();
        args[3] = "--output";
        args[4] = actualOutput.getPath();
        for ( int i = 0; i < extra.length; i++ ) {
            args[i + 5] = extra[i];
        }

        CommandManager robot = new CommandManager();
        robot.addCommand("kgcl-apply", new ApplyCommand());
        robot.addCommand("kgcl-mint", new MintCommand());
        robot.main(args);

        if ( outputFile != null ) {
            compareOntologies(expectedOutput, actualOutput);
        } else if ( actualOutput.exists() ) {
            actualOutput.delete();
        }
    }

    private static void compareOntologies(File expected, File actual) {
        try {
            OWLOntology expectedOntology = OWLManager.createOWLOntologyManager()
                    .loadOntologyFromOntologyDocument(expected);
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
}
