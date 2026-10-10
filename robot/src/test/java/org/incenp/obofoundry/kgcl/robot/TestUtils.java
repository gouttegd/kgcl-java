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
