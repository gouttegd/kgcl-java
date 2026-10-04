/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
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
