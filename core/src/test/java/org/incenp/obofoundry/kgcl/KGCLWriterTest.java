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

package org.incenp.obofoundry.kgcl;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class KGCLWriterTest {

    private static final TestUtils util = new TestUtils();

    @Test
    void testWriteSingleChange() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getNode("0001"));
        testSimpleWrite(w -> w.write(change), "obsolete <https://example.org/0001>\n");
    }

    @Test
    void testWriteChangeset() {
        NodeObsoletion c1 = new NodeObsoletion();
        c1.setAboutNode(util.getNode("0001"));

        ClassCreation c2 = new ClassCreation();
        c2.setAboutNode(util.getNode("0002"));
        c2.setNewValue("new class");

        ArrayList<Change> changeset = new ArrayList<Change>();
        changeset.add(c1);
        changeset.add(c2);

        testSimpleWrite(w -> w.write(changeset),
                "obsolete <https://example.org/0001>\ncreate class <https://example.org/0002> \"new class\"\n");
    }

    @Test
    void testWriteComment() {
        testSimpleWrite(w -> w.write("a comment"), "# a comment\n");
    }

    @Test
    void testMultipleWrite() {
        NodeObsoletion c1 = new NodeObsoletion();
        c1.setAboutNode(util.getNode("0001"));

        ClassCreation c2 = new ClassCreation();
        c2.setAboutNode(util.getNode("0002"));
        c2.setNewValue("new class");

        testSimpleWrite(w -> {
            w.write(c1);
            w.write("a comment");
            w.write(c2);
        }, "obsolete <https://example.org/0001>\n# a comment\ncreate class <https://example.org/0002> \"new class\"\n");
    }

    @Test
    void testWriteWithPrefixManager() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getNode("0001"));

        testSimpleWrite(w -> {
            w.setPrefixManager(util.getPrefixManager());
            w.write(change);
        }, "obsolete EX:0001\n");
    }

    @Test
    void testWriteWithOntologyDerivedPrefixManager() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getForeignNode("http://www.co-ode.org/ontologies/pizza/pizza.owl#LaReine"));

        OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
        try {
            OWLOntology ont = mgr.loadOntologyFromOntologyDocument(new File("src/test/resources/pizza.ofn"));
            testSimpleWrite(w -> {
                w.setPrefixManager(ont);
                w.write(change);
            }, "obsolete pizza:LaReine\n");
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail(e);
        }
    }

    @Test
    void testWriteWithCustomPrefixMap() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getNode("0001"));

        HashMap<String, String> prefixMap = new HashMap<String, String>();
        prefixMap.put("EXA", TestUtils.EXAMPLE_BASE);

        testSimpleWrite(w -> {
            w.setPrefixMap(prefixMap);
            w.write(change);
        }, "obsolete EXA:0001\n");
    }

    /*
     * Helper method to test the KGCLWriter. This creates a string-backed writer,
     * calls the provided callback with the writer, then checks that the writer
     * wrote what was expected.
     */
    private void testSimpleWrite(IKGCLWriterConsumer c, String expected) {
        StringWriter writer = new StringWriter();
        KGCLWriter kgclWriter = new KGCLWriter(writer);
        try {
            c.accept(kgclWriter);
            kgclWriter.close();
        } catch ( IOException ioe ) {
            Assertions.fail(ioe);
        }

        Assertions.assertEquals(expected, writer.toString());
    }
}

/* The callback passed to the testSimpleWrite method above. */
interface IKGCLWriterConsumer {
    void accept(KGCLWriter writer) throws IOException;
}
