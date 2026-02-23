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
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
