/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
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

package org.incenp.obofoundry.kgcl.cli;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SimpleCLITest {

    @Test
    void testKGCLToYAMLConversion() throws IOException {
        runCommand(0, new String[] { "sample1.kgcl" }, "sample1.yaml",
                new String[] { "-f", "yaml", "--prefix", "EX: https://example.org/" });
    }

    @Test
    void testYAMLtoKGCLConversion() throws IOException {
        runCommand(0, new String[] { "sample1.yaml" }, "sample1.kgcl",
                new String[] { "-f", "kgcl", "--prefix", "EX: https://example.org/" });
    }

    @Test
    void testMergingChangesets() throws IOException {
        runCommand(0,
                new String[] { "sample1.yaml", "obsolete-sultana-topping.kgcl" },
                "test-merging.kgcl",
                new String[] { "-f", "kgcl",
                               "--prefix", "EX: https://example.org",
                               "--prefix", "pizza: http://www.co-ode.org/ontologies/pizza#" });
    }

    @Test
    void testNormalize() throws IOException {
        runCommand(0, new String[] { "samples.yaml" }, "test-normalizing.yaml", new String[] { "-f", "yaml" });
    }

    private void runCommand(int code, String[] inputs, String output, String[] others) throws IOException {
        int nArgs = inputs.length + others.length;
        if ( output != null ) {
            nArgs += 2;
        }

        String[] args = new String[nArgs];
        nArgs = 0;

        for ( String input : inputs ) {
            args[nArgs++] = getInputPath(input);
        }
        for ( String other : others ) {
            args[nArgs++] = other;
        }
        if ( output != null ) {
            args[nArgs++] = "--output";
            args[nArgs] = getOutputPath(output);
        }

        Assertions.assertEquals(code, SimpleCLI.run(args));

        if ( code == 0 && output != null ) {
            checkOutput(output);
        }
    }

    private String getInputPath(String name) {
        File f = new File("../core/src/test/resources/" + name);
        if ( !f.exists() ) {
            f = new File("../robot/src/test/resources/" + name);
        }
        if ( !f.exists() ) {
            f = new File("src/test/resources/" + name);
        }
        return f.exists() ? f.getPath() : name;
    }

    private String getOutputPath(String name) {
        return "src/test/resources/output/" + name + ".out";
    }

    private void checkOutput(String output) throws IOException {
        String path = getOutputPath(output);
        File expected = new File(path.substring(0, path.length() - 4));
        File written = new File(path);
        boolean same = FileUtils.contentEquals(expected, written);
        Assertions.assertTrue(same);
        if ( same ) {
            written.delete();
        }
    }
}
