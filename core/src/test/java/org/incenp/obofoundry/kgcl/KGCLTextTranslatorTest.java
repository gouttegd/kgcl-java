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

import java.util.ArrayList;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationChange;
import org.incenp.obofoundry.kgcl.model.NodeDeepening;
import org.incenp.obofoundry.kgcl.model.NodeDeletion;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.NodeShallowing;
import org.incenp.obofoundry.kgcl.model.NodeUnobsoletion;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

public class KGCLTextTranslatorTest {

    private static final TestUtils util = new TestUtils();

    private Node defaultNode = util.getNode("0001");

    @Test
    void testRenderNodeRename() {
        NodeRename change = new NodeRename();
        change.setAboutNode(defaultNode);
        change.setOldValue("old label");
        change.setNewValue("new label");

        render(change, "rename EX:0001 from \"old label\" to \"new label\"");
    }

    @Test
    void testRenderNewSynonym() {
        String[] qualifiers = { "exact", "narrow", "broad", "related", null };
        String[] expected = { "create exact synonym \"new synonym\" for EX:0001",
                "create narrow synonym \"new synonym\" for EX:0001", "create broad synonym \"new synonym\" for EX:0001",
                "create related synonym \"new synonym\" for EX:0001", "create synonym \"new synonym\" for EX:0001" };

        for ( int i = 0; i < qualifiers.length; i++ ) {
            NewSynonym change = new NewSynonym();
            change.setAboutNode(defaultNode);
            change.setNewValue("new synonym");
            change.setQualifier(qualifiers[i]);

            render(change, expected[i]);
        }
    }

    @Test
    void testRenderRemoveSynonym() {
        RemoveSynonym change = new RemoveSynonym();
        change.setAboutNode(defaultNode);
        change.setOldValue("old synonym");

        render(change, "remove synonym \"old synonym\" for EX:0001");
    }

    @Test
    void testRenderSynonymReplacement() {
        SynonymReplacement change = new SynonymReplacement();
        change.setAboutNode(defaultNode);
        change.setOldValue("old synonym");
        change.setNewValue("new synonym");

        render(change, "change synonym from \"old synonym\" to \"new synonym\" for EX:0001");
    }

    @Test
    void testRenderNewDefinition() {
        NewTextDefinition change = new NewTextDefinition();
        change.setAboutNode(defaultNode);
        change.setNewValue("new definition");

        render(change, "add definition \"new definition\" for EX:0001");
    }

    @Test
    void testRenderRemoveDefinition() {
        RemoveTextDefinition change = new RemoveTextDefinition();
        change.setAboutNode(defaultNode);

        render(change, "remove definition for EX:0001");
    }

    @Test
    void testRenderChangeDefinition() {
        TextDefinitionReplacement change = new TextDefinitionReplacement();
        change.setAboutNode(defaultNode);
        change.setNewValue("new definition");

        render(change, "change definition of EX:0001 to \"new definition\"");

        change.setOldValue("old definition");

        render(change, "change definition of EX:0001 from \"old definition\" to \"new definition\"");
    }

    @Test
    void testRenderSimpleObsoletion() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(defaultNode);

        render(change, "obsolete EX:0001");
    }

    @Test
    void testRenderObsoletionWithDirectReplacement() {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();
        change.setAboutNode(defaultNode);
        change.setHasDirectReplacement(util.getNode("0002"));

        render(change, "obsolete EX:0001 with replacement EX:0002");
    }

    @Test
    void testRenderObsoletionWithNoDirectReplacement() {
        NodeObsoletionWithNoDirectReplacement change = new NodeObsoletionWithNoDirectReplacement();
        change.setAboutNode(defaultNode);
        change.setHasNondirectReplacement(new ArrayList<Node>());
        change.getHasNondirectReplacement().add(util.getNode("0002"));

        render(change, "obsolete EX:0001 with alternative EX:0002");

        change.getHasNondirectReplacement().add(util.getNode("0003"));

        render(change, "obsolete EX:0001 with alternative EX:0002,EX:0003");
    }

    @Test
    void testRenderUnobsoletion() {
        NodeUnobsoletion change = new NodeUnobsoletion();
        change.setAboutNode(defaultNode);

        render(change, "unobsolete EX:0001");
    }

    @Test
    void testRenderNodeDeletion() {
        NodeDeletion change = new NodeDeletion();
        change.setAboutNode(defaultNode);

        render(change, "delete EX:0001");
    }

    @Test
    void testRenderClassCreation() {
        ClassCreation change = new ClassCreation();
        change.setAboutNode(defaultNode);
        change.setNewValue("new label");

        render(change, "create class EX:0001 \"new label\"");
    }

    @Test
    void testRenderEdgeCreation() {
        EdgeCreation change = new EdgeCreation();
        change.setSubject(util.getNode("0001"));
        change.setPredicate(util.getNode("is_related_to"));
        change.setObject(util.getNode("0002"));

        render(change, "create edge EX:0001 EX:is_related_to EX:0002");
    }

    @Test
    void testRenderEdgeDeletion() {
        EdgeDeletion change = new EdgeDeletion();
        change.setSubject(util.getNode("0001"));
        change.setPredicate(util.getNode("is_related_to"));
        change.setObject(util.getNode("0002"));

        render(change, "delete edge EX:0001 EX:is_related_to EX:0002");
    }

    @Test
    void testRendePlaceUnder() {
        PlaceUnder change = new PlaceUnder();
        change.setSubject(util.getNode("0001"));
        change.setObject(util.getNode("0002"));

        render(change, "create edge EX:0001 rdfs:subClassOf EX:0002");
    }

    @Test
    void testRenderRemoveUnder() {
        RemoveUnder change = new RemoveUnder();
        change.setSubject(util.getNode("0001"));
        change.setObject(util.getNode("0002"));

        render(change, "delete edge EX:0001 rdfs:subClassOf EX:0002");
    }

    @Test
    void testRenderNodeMove() {
        NodeMove change = new NodeMove();
        change.setAboutEdge(util.getEdge("0001", null, null));
        change.setOldValue(util.getId("0002"));
        change.setNewValue(util.getId("0003"));

        render(change, "move EX:0001 from EX:0002 to EX:0003");
    }

    @Test
    void testRenderNodeDeepening() {
        NodeDeepening change = new NodeDeepening();
        change.setAboutEdge(util.getEdge("0001", null, null));
        change.setOldValue(util.getId("0002"));
        change.setNewValue(util.getId("0003"));

        render(change, "deepen EX:0001 from EX:0002 to EX:0003");
    }

    @Test
    void testRenderNodeShallowing() {
        NodeShallowing change = new NodeShallowing();
        change.setAboutEdge(util.getEdge("0001", null, null));
        change.setOldValue(util.getId("0002"));
        change.setNewValue(util.getId("0003"));

        render(change, "shallow EX:0001 from EX:0002 to EX:0003");
    }

    @Test
    void testRenderPredicateChange() {
        PredicateChange change = new PredicateChange();
        change.setAboutEdge(util.getEdge("0001", null, "0002"));
        change.setOldValue(util.getId("is_related_to"));
        change.setNewValue(util.getId("is_a"));

        render(change, "change relationship between EX:0001 and EX:0002 from EX:is_related_to to EX:is_a");
    }

    @Test
    void testRenderAnnotationChange() {
        NodeAnnotationChange change = new NodeAnnotationChange();
        change.setAboutNode(defaultNode);
        change.setAnnotationProperty(util.getId("hasProperty"));
        change.setOldValue("old value");
        change.setNewValue("new value");

        render(change, "change annotation of EX:0001 with EX:hasProperty from \"old value\" to \"new value\"");
    }

    @Test
    void testRenderUnshortenedIds() {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();
        change.setAboutNode(defaultNode);
        change.setHasDirectReplacement(util.getNode("0002"));

        render(change, "obsolete <https://example.org/0001> with replacement <https://example.org/0002>", false);
    }

    @Test
    void testRenderValueWithQuotes() {
        NewTextDefinition change = new NewTextDefinition();
        change.setAboutNode(defaultNode);
        change.setNewValue("new \"definition\"");

        render(change, "add definition \"new \\\"definition\\\"\" for EX:0001");
    }

    @Test
    void testRenderValueWithLangTag() {
        NewTextDefinition change = new NewTextDefinition();
        change.setAboutNode(defaultNode);
        change.setNewValue("new definition");
        change.setNewLanguage("en");

        render(change, "add definition \"new definition\"@en for EX:0001");
    }

    /*
     * Helper methods.
     */

    /*
     * Converts a KGCL change into its textual representation and check that the
     * conversion is what we expect.
     */
    private void render(Change change, String expected, boolean shortenId) {
        KGCLTextTranslator translator = null;
        if ( shortenId ) {
            DefaultPrefixManager pm = new DefaultPrefixManager();
            pm.setPrefix("EX:", "https://example.org/");
            translator = new KGCLTextTranslator(pm);
        } else {
            translator = new KGCLTextTranslator();
        }

        String actual = change.accept(translator);
        Assertions.assertEquals(expected, actual);
    }

    /* Same, but IDs are always shortened. */
    private void render(Change change, String expected) {
        render(change, expected, true);
    }
}
