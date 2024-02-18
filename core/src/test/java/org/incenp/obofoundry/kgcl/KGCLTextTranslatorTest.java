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
import org.incenp.obofoundry.kgcl.model.Edge;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationChange;
import org.incenp.obofoundry.kgcl.model.NodeChange;
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

    private static final String EXAMPLE_BASE = "https://example.org/";

    @Test
    void testRenderNodeRename() {
        NodeRename change = new NodeRename();
        setAboutNode(change, "0001");
        setValue(change, "old label", "en", true);
        setValue(change, "new label", "en");

        render(change, "rename EX:0001 from \"old label\"@en to \"new label\"@en");
    }

    @Test
    void testRenderNewSynonym() {
        String[] qualifiers = { "exact", "narrow", "broad", "related", null };
        String[] expected = { "create exact synonym \"new synonym\"@en for EX:0001",
                "create narrow synonym \"new synonym\"@en for EX:0001",
                "create broad synonym \"new synonym\"@en for EX:0001",
                "create related synonym \"new synonym\"@en for EX:0001",
                "create synonym \"new synonym\"@en for EX:0001" };

        for ( int i = 0; i < qualifiers.length; i++ ) {
            NewSynonym change = new NewSynonym();
            setAboutNode(change, "0001");
            setValue(change, "new synonym", "en");
            change.setQualifier(qualifiers[i]);

            render(change, expected[i]);
        }
    }

    @Test
    void testRenderRemoveSynonym() {
        RemoveSynonym change = new RemoveSynonym();
        setAboutNode(change, "0001");
        setValue(change, "old synonym", "en", true);

        render(change, "remove synonym \"old synonym\"@en for EX:0001");
    }

    @Test
    void testRenderSynonymReplacement() {
        SynonymReplacement change = new SynonymReplacement();
        setAboutNode(change, "0001");
        setValue(change, "old synonym", "en", true);
        setValue(change, "new synonym", "en");

        render(change, "change synonym from \"old synonym\"@en to \"new synonym\"@en for EX:0001");
    }

    @Test
    void testRenderNewDefinition() {
        NewTextDefinition change = new NewTextDefinition();
        setAboutNode(change, "0001");
        setValue(change, "new definition", "en");

        render(change, "add definition \"new definition\"@en for EX:0001");
    }

    @Test
    void testRenderRemoveDefinition() {
        RemoveTextDefinition change = new RemoveTextDefinition();
        setAboutNode(change, "0001");

        render(change, "remove definition for EX:0001");
    }

    @Test
    void testRenderChangeDefinition() {
        TextDefinitionReplacement change = new TextDefinitionReplacement();
        setAboutNode(change, "0001");
        setValue(change, "new definition", "en");

        render(change, "change definition of EX:0001 to \"new definition\"@en");

        setValue(change, "old definition", "en", true);

        render(change, "change definition of EX:0001 from \"old definition\"@en to \"new definition\"@en");
    }

    @Test
    void testRenderSimpleObsoletion() {
        NodeObsoletion change = new NodeObsoletion();
        setAboutNode(change, "0001");

        render(change, "obsolete EX:0001");
    }

    @Test
    void testRenderObsoletionWithDirectReplacement() {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();
        setAboutNode(change, "0001");
        change.setHasDirectReplacement(getNode("0002"));

        render(change, "obsolete EX:0001 with replacement EX:0002");
    }

    @Test
    void testRenderObsoletionWithNoDirectReplacement() {
        NodeObsoletionWithNoDirectReplacement change = new NodeObsoletionWithNoDirectReplacement();
        setAboutNode(change, "0001");
        change.setHasNondirectReplacement(new ArrayList<Node>());
        change.getHasNondirectReplacement().add(getNode("0002"));

        render(change, "obsolete EX:0001 with alternative EX:0002");

        change.getHasNondirectReplacement().add(getNode("0003"));

        render(change, "obsolete EX:0001 with alternative EX:0002,EX:0003");
    }

    @Test
    void testRenderUnobsoletion() {
        NodeUnobsoletion change = new NodeUnobsoletion();
        setAboutNode(change, "0001");

        render(change, "unobsolete EX:0001");
    }

    @Test
    void testRenderNodeDeletion() {
        NodeDeletion change = new NodeDeletion();
        setAboutNode(change, "0001");

        render(change, "delete EX:0001");
    }

    @Test
    void testRenderClassCreation() {
        ClassCreation change = new ClassCreation();
        setAboutNode(change, "0001");
        setValue(change, "new label", "en");

        render(change, "create class EX:0001 \"new label\"@en");
    }

    @Test
    void testRenderEdgeCreation() {
        EdgeCreation change = new EdgeCreation();
        change.setSubject(getNode("0001"));
        change.setPredicate(getNode("is_related_to"));
        change.setObject(getNode("0002"));

        render(change, "create edge EX:0001 EX:is_related_to EX:0002");
    }

    @Test
    void testRenderEdgeDeletion() {
        EdgeDeletion change = new EdgeDeletion();
        change.setSubject(getNode("0001"));
        change.setPredicate(getNode("is_related_to"));
        change.setObject(getNode("0002"));

        render(change, "delete edge EX:0001 EX:is_related_to EX:0002");
    }

    @Test
    void testRendePlaceUnder() {
        PlaceUnder change = new PlaceUnder();
        change.setSubject(getNode("0001"));
        change.setObject(getNode("0002"));

        render(change, "create edge EX:0001 rdfs:subClassOf EX:0002");
    }

    @Test
    void testRenderRemoveUnder() {
        RemoveUnder change = new RemoveUnder();
        change.setSubject(getNode("0001"));
        change.setObject(getNode("0002"));

        render(change, "delete edge EX:0001 rdfs:subClassOf EX:0002");
    }

    @Test
    void testRenderNodeMove() {
        NodeMove change = new NodeMove();
        Edge edge = new Edge();
        edge.setSubject(getNode("0001"));
        change.setAboutEdge(edge);
        change.setOldValue(getNode("0002").getId());
        change.setNewValue(getNode("0003").getId());

        render(change, "move EX:0001 from EX:0002 to EX:0003");
    }

    @Test
    void testRenderNodeDeepening() {
        NodeDeepening change = new NodeDeepening();
        Edge edge = new Edge();
        edge.setSubject(getNode("0001"));
        change.setAboutEdge(edge);
        change.setOldValue(getNode("0002").getId());
        change.setNewValue(getNode("0003").getId());

        render(change, "deepen EX:0001 from EX:0002 to EX:0003");
    }

    @Test
    void testRenderNodeShallowing() {
        NodeShallowing change = new NodeShallowing();
        Edge edge = new Edge();
        edge.setSubject(getNode("0001"));
        change.setAboutEdge(edge);
        change.setOldValue(getNode("0002").getId());
        change.setNewValue(getNode("0003").getId());

        render(change, "shallow EX:0001 from EX:0002 to EX:0003");
    }

    @Test
    void testRenderPredicateChange() {
        PredicateChange change = new PredicateChange();
        Edge edge = new Edge();
        edge.setSubject(getNode("0001"));
        edge.setObject(getNode("0002"));
        change.setAboutEdge(edge);
        change.setOldValue(getNode("is_related_to").getId());
        change.setNewValue(getNode("is_a").getId());

        render(change, "change relationship between EX:0001 and EX:0002 from EX:is_related_to to EX:is_a");
    }

    @Test
    void testRenderAnnotationChange() {
        NodeAnnotationChange change = new NodeAnnotationChange();
        setAboutNode(change, "0001");
        change.setAnnotationProperty(getNode("hasProperty").getId());
        setValue(change, "old value", "en", true);
        setValue(change, "new value", "en");

        render(change, "change annotation of EX:0001 with EX:hasProperty from \"old value\"@en to \"new value\"@en");
    }

    @Test
    void testRenderUnshortenedIds() {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();
        setAboutNode(change, "0001");
        change.setHasDirectReplacement(getNode("0002"));

        render(change, "obsolete <https://example.org/0001> with replacement <https://example.org/0002>", false);
    }

    @Test
    void testRenderValueWithQuotes() {
        NewTextDefinition change = new NewTextDefinition();
        setAboutNode(change, "0001");
        setValue(change, "new \"definition\"", "en");

        render(change, "add definition \"new \\\"definition\\\"\"@en for EX:0001");
    }

    @Test
    void testRenderValueWithNoLangTag() {
        NewTextDefinition change = new NewTextDefinition();
        setAboutNode(change, "0001");
        setValue(change, "new definition", null);

        render(change, "add definition \"new definition\" for EX:0001");
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

    private Node getNode(String id) {
        Node node = new Node();
        node.setId(EXAMPLE_BASE + id);
        return node;
    }

    private void setAboutNode(NodeChange change, String id) {
        change.setAboutNode(getNode(id));
    }

    private void setValue(NodeChange change, String value, String language, boolean old) {
        if ( old ) {
            change.setOldValue(value);
            change.setOldLanguage(language);
        } else {
            change.setNewValue(value);
            change.setNewLanguage(language);
        }
    }

    private void setValue(NodeChange change, String value, String language) {
        setValue(change, value, language, false);
    }
}
