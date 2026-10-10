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

package org.incenp.obofoundry.kgcl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.incenp.obofoundry.kgcl.model.AddNodeToSubset;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class YAMLReaderTest {

    @Test
    void testReadingYAMLFile() throws IOException {
        Map<String, String> prefixMap = new HashMap<>();
        prefixMap.put("CHANGE:", "https://example.org/");
        prefixMap.put("GO:", "http://purl.obolibrary.org/obo/GO_");
        prefixMap.put("BFO:", "http://purl.obolibrary.org/obo/BFO_");
        prefixMap.put("rdfs:", "http://www.w3.org/2000/01/rdf-schema#");
        List<Change> changeset = KGCLHelper.parseYAML(new File("src/test/resources/samples.yaml"), prefixMap);

        Assertions.assertEquals(19, changeset.size());

        Assertions.assertEquals("https://example.org/000", changeset.get(0).getId());
        Assertions.assertInstanceOf(NodeRename.class, changeset.get(0));
        Assertions.assertEquals("nuclear envelope", ((NodeRename) changeset.get(0)).getOldValue());
        Assertions.assertEquals("foo bar", ((NodeRename) changeset.get(0)).getNewValue());
        Assertions.assertEquals("http://purl.obolibrary.org/obo/GO_0005635",
                ((NodeRename) changeset.get(0)).getAboutNode().getId());

        Assertions.assertInstanceOf(NodeObsoletionWithDirectReplacement.class, changeset.get(2));
        Assertions.assertEquals("http://purl.obolibrary.org/obo/GO_0005634",
                ((NodeObsoletionWithDirectReplacement) changeset.get(2)).getAboutNode().getId());
        Assertions.assertEquals("http://purl.obolibrary.org/obo/GO_999",
                ((NodeObsoletionWithDirectReplacement) changeset.get(2)).getHasDirectReplacement().getId());

        Assertions.assertInstanceOf(NewSynonym.class, changeset.get(3));
        Assertions.assertEquals("exact", ((NewSynonym) changeset.get(3)).getQualifier());

        Assertions.assertInstanceOf(AddNodeToSubset.class, changeset.get(6));
        Assertions.assertEquals("foo", ((AddNodeToSubset) changeset.get(6)).getInSubset().getId());

        Assertions.assertInstanceOf(EdgeCreation.class, changeset.get(10));
        Assertions.assertEquals("http://purl.obolibrary.org/obo/GO_0005634",
                ((EdgeCreation) changeset.get(10)).getSubject().getId());
        Assertions.assertEquals("http://purl.obolibrary.org/obo/BFO_0000050",
                ((EdgeCreation) changeset.get(10)).getPredicate().getId());
        Assertions.assertEquals("http://purl.obolibrary.org/obo/GO_0009411",
                ((EdgeCreation) changeset.get(10)).getObject().getId());

        Assertions.assertInstanceOf(PredicateChange.class, changeset.get(12));
        Assertions.assertEquals("http://purl.obolibrary.org/obo/BFO_0000050",
                ((PredicateChange) changeset.get(12)).getOldValue());
        Assertions.assertEquals("http://www.w3.org/2000/01/rdf-schema#subClassOf",
                ((PredicateChange) changeset.get(12)).getNewValue());
        Assertions.assertEquals("http://purl.obolibrary.org/obo/GO_0005635",
                ((PredicateChange) changeset.get(12)).getAboutEdge().getSubject().getId());
        Assertions.assertEquals("http://purl.obolibrary.org/obo/BFO_0000050",
                ((PredicateChange) changeset.get(12)).getAboutEdge().getPredicate().getId());
        Assertions.assertEquals("http://purl.obolibrary.org/obo/GO_0005634",
                ((PredicateChange) changeset.get(12)).getAboutEdge().getObject().getId());
    }
}
