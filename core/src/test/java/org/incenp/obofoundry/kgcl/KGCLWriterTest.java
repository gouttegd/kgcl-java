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
