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

import java.util.ArrayList;
import java.util.List;

import org.incenp.obofoundry.dicer.IAutoIDGenerator;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.Edge;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class AutoIDAllocatorTest {

    private static final TestUtils util = new TestUtils();

    @Test
    void testUpdateNodeChange() {
        NodeRename change = new NodeRename();
        change.setAboutNode(util.getForeignNode(AutoIDAllocator.AUTOID_BASE_IRI + "a"));
        change.setOldValue("old label");
        change.setNewValue("new label");

        change.accept(new AutoIDAllocator(new SequentialIDGenerator()));
        Assertions.assertEquals("https://example.org/0001", change.getAboutNode().getId());
    }

    @Test
    void testUpdatePlaceUnder() {
        PlaceUnder change = new PlaceUnder();
        change.setSubject(util.getForeignNode(AutoIDAllocator.AUTOID_BASE_IRI + "a"));
        change.setObject(util.getForeignNode(AutoIDAllocator.AUTOID_BASE_IRI + "b"));
        
        change.accept(new AutoIDAllocator(new SequentialIDGenerator()));
        Assertions.assertEquals("https://example.org/0001", change.getSubject().getId());
        Assertions.assertEquals("https://example.org/0002", change.getObject().getId());
    }

    @Test
    void testUpdateRemoveUnder() {
        RemoveUnder change = new RemoveUnder();
        change.setSubject(util.getForeignNode(AutoIDAllocator.AUTOID_BASE_IRI + "a"));
        change.setObject(util.getForeignNode(AutoIDAllocator.AUTOID_BASE_IRI + "b"));

        change.accept(new AutoIDAllocator(new SequentialIDGenerator()));
        Assertions.assertEquals("https://example.org/0001", change.getSubject().getId());
        Assertions.assertEquals("https://example.org/0002", change.getObject().getId());
    }

    @Test
    void testUpdateNodeMove() {
        NodeMove change = new NodeMove();
        Edge aboutEdge = new Edge();
        aboutEdge.setSubject(util.getForeignNode(AutoIDAllocator.AUTOID_BASE_IRI + "a"));
        aboutEdge.setPredicate(util.getNode("pred1"));
        change.setAboutEdge(aboutEdge);
        change.setOldValue("https://example.org/9999");
        change.setNewValue(AutoIDAllocator.AUTOID_BASE_IRI + "b");

        change.accept(new AutoIDAllocator(new SequentialIDGenerator()));
        Assertions.assertEquals("https://example.org/0001", change.getAboutEdge().getSubject().getId());
        Assertions.assertEquals("https://example.org/pred1", change.getAboutEdge().getPredicate().getId());
        Assertions.assertEquals("https://example.org/9999", change.getOldValue());
        Assertions.assertEquals("https://example.org/0002", change.getNewValue());
    }

    @Test
    void testUpdateChangeset() {
        // @formatter:off
        String[] commands = new String[] {
                "create class AUTOID:a \"new class A\"",
                "create class AUTOID:b \"new class B\"",
                "create class AUTOID:c \"new class C\"",
                "create edge AUTOID:a rdfs:subClassOf AUTOID:b",
                "change relationship between AUTOID:a and AUTOID:b from AUTOID:d to AUTOID:e"
        };
        String[] expected = new String[] {
                "create class EX:0001 \"new class A\"",
                "create class EX:0002 \"new class B\"",
                "create class EX:0003 \"new class C\"",
                "create edge EX:0001 rdfs:subClassOf EX:0002",
                "change relationship between EX:0001 and EX:0002 from EX:0004 to EX:0005"
        };
        // @formatter:on

        testUpdate(String.join("\n", commands), String.join("\n", expected));
    }

    @Test
    void testIDGenerationFailure() {
        NodeRename change = new NodeRename();
        change.setAboutNode(util.getForeignNode(AutoIDAllocator.AUTOID_BASE_IRI + "a"));
        change.setOldValue("old label");
        change.setNewValue("new label");

        AutoIDAllocator allocator = new AutoIDAllocator(() -> null);
        change.accept(allocator);
        Assertions.assertEquals(AutoIDAllocator.AUTOID_BASE_IRI + "a", change.getAboutNode().getId());
        Assertions.assertFalse(allocator.getUnallocatedIDs().isEmpty());
        Assertions.assertTrue(allocator.getUnallocatedIDs().contains(AutoIDAllocator.AUTOID_BASE_IRI + "a"));
    }

    private class SequentialIDGenerator implements IAutoIDGenerator {

        private String format = "https://example.org/%04d";
        private int cursor = 1;

        @Override
        public String nextID() {
            return String.format(format, cursor++);
        }
    }

    private void testUpdate(String originalCommands, String expectedOutput) {
        KGCLReader reader = new KGCLReader();
        PrefixManager pm = new DefaultPrefixManager();
        pm.setPrefix("AUTOID:", AutoIDAllocator.AUTOID_BASE_IRI);
        pm.setPrefix("EX:", "https://example.org/");
        reader.setPrefixManager(pm);
        
        reader.read(originalCommands + "\n");
        List<Change> changes = reader.getChangeSet();
        new AutoIDAllocator(new SequentialIDGenerator()).reallocate(changes);

        KGCLTextTranslator toText = new KGCLTextTranslator(pm);
        ArrayList<String> output = new ArrayList<String>();
        for ( Change change : changes ) {
            output.add(change.accept(toText));
        }

        Assertions.assertEquals(expectedOutput, String.join("\n", output));
    }
}
