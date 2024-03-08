/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023,2024 Damien Goutte-Gattat
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.incenp.obofoundry.kgcl.model.AddNodeToSubset;
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
import org.incenp.obofoundry.kgcl.model.OntologySubset;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveNodeFromSubset;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

class KGCLReaderTest {

    private static final TestUtils util = new TestUtils();

    @Test
    void testFileParser() throws IOException {
        KGCLReader reader = new KGCLReader("src/test/resources/sample1.kgcl");
        reader.setPrefixManager(util.getPrefixManager());
        Assertions.assertTrue(reader.read());
        Assertions.assertEquals(5, reader.getChangeSet().size());

        NodeRename c1 = new NodeRename();
        c1.setAboutNode(util.getNode("0001"));
        c1.setOldValue("foo");
        c1.setNewValue("FOO");

        NewSynonym c2 = new NewSynonym();
        c2.setAboutNode(util.getNode("0003"));
        c2.setNewValue("bazz");
        c2.setQualifier("broad");

        NodeObsoletionWithNoDirectReplacement c3 = new NodeObsoletionWithNoDirectReplacement();
        c3.setAboutNode(util.getNode("0002"));
        c3.setHasNondirectReplacement(new ArrayList<Node>());
        c3.getHasNondirectReplacement().add(util.getNode("0003"));

        ClassCreation c4 = new ClassCreation();
        c4.setAboutNode(util.getNode("0004"));
        c4.setNewValue("xyz");

        EdgeCreation c5 = new EdgeCreation();
        c5.setSubject(util.getNode("0004"));
        c5.setPredicate(util.getForeignNode(OWLRDFVocabulary.RDFS_SUBCLASS_OF.toString()));
        c5.setObject(util.getNode("0003"));

        Change[] expected = { c1, c2, c3, c4, c5 };
        for (int i = 0; i < 5; i++) {
            Assertions.assertEquals(expected[i], reader.getChangeSet().get(i));
        }
    }

    @Test
    void testParserCallsReadAsNeeded() throws IOException {
        KGCLReader reader = new KGCLReader("src/test/resources/sample1.kgcl");
        Assertions.assertEquals(5, reader.getChangeSet().size());
    }

    @Test
    void testChangesetIsResetUponNewRead() {
        KGCLReader reader = new KGCLReader();
        reader.read("obsolete EX:0001");
        Assertions.assertEquals(1, reader.getChangeSet().size());

        reader.read("unobsolete EX:0001");
        Assertions.assertEquals(1, reader.getChangeSet().size());
    }

    @Test
    void testErrorListIsResetUponNewRead() {
        KGCLReader reader = new KGCLReader();
        reader.read("this is not a valid KGCL command");
        Assertions.assertTrue(reader.getErrors().size() > 0);

        reader.read("obsolete EX:0001");
        Assertions.assertTrue(reader.getErrors().isEmpty());
    }

    @Test
    void testEmptyInputIsValid() {
        testParse("", null);
        testParse(" ", null);
        testParse("\n", null);
        testParse("\n\n", null);
    }

    @Test
    void testCommentedInputOnlyIsValid() {
        testParse("# comment\n", null);
        testParse("\n# comment\n", null);

        // Invalid: comments must end with a new line
        testParseFail("# comment");
    }

    @Test
    void testLeadingOrTrailingWhitespaces() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getNode("0001"));

        testParse("obsolete EX:0001", change);
        testParse("obsolete EX:0001\n", change);
        testParse("\nobsolete EX:0001", change);
        testParse("\nobsolete EX:0001\n", change);
    }

    @Test
    void testLeadingOrTrailingComment() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getNode("0001"));

        testParse("# comment\nobsolete EX:0001", change);
        testParse("# comment\nobsolete EX:0001\n# comment\n", change);
    }

    @Test
    void testUnshortenedId() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getNode("0001"));

        testParse("obsolete <https://example.org/0001>", change);
    }

    @Test
    void testUnknownPrefixIsOBOStyleID() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getForeignNode("http://purl.obolibrary.org/obo/PFX_0001"));

        testParse("obsolete PFX:0001", change);

        // Same with no prefix manager at all
        testParse(r -> r.read("obsolete PFX:0001"), change);
    }

    @Test
    void testUsingOntologyDerivedPrefixManager() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getForeignNode("http://www.co-ode.org/ontologies/pizza/pizza.owl#LaReine"));

        OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
        try {
            OWLOntology ont = mgr.loadOntologyFromOntologyDocument(new File("src/test/resources/pizza.ofn"));
            testParse(r -> {
                r.setPrefixManager(ont);
                r.read("obsolete pizza:LaReine");
            }, change);
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail(e);
        }
    }

    @Test
    void testRenameChange() {
        NodeRename change = new NodeRename();
        change.setAboutNode(util.getNode("0001"));
        change.setOldValue("old label");
        change.setNewValue("new label");

        testParse("rename EX:0001 from 'old label' to 'new label'", change);
    }

    @Test
    void testChangeValueWithLanguageTag() {
        NodeRename change = new NodeRename();
        change.setAboutNode(util.getNode("0001"));
        change.setOldValue("old label");
        change.setOldLanguage("en");
        change.setNewValue("nouvelle étiquette");
        change.setNewLanguage("fr");

        testParse("rename EX:0001 from 'old label'@en to 'nouvelle étiquette'@fr", change);
    }

    @Test
    void testChangeValueInDoubleQuotes() {
        NodeRename change = new NodeRename();
        change.setAboutNode(util.getNode("0001"));
        change.setOldValue("old label");
        change.setNewValue("new label");

        testParse("rename EX:0001 from \"old label\" to 'new label'", change);
    }

    @Test
    void testChangeValueWithInnerQuotes() {
        NodeRename change = new NodeRename();
        change.setAboutNode(util.getNode("0001"));
        change.setOldValue("old' label");
        change.setNewValue("new label");

        testParse("rename EX:0001 from 'old\\' label' to 'new label'", change);
    }

    @Test
    void testObsoleteChange() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getNode("0001"));

        testParse("obsolete EX:0001", change);
    }

    @Test
    void testObsoleteChangeWithReplacement() {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();
        change.setAboutNode(util.getNode("0001"));
        change.setHasDirectReplacement(util.getNode("0002"));

        testParse("obsolete EX:0001 with replacement EX:0002", change);
    }

    @Test
    void testObsoleteChangeWithNoDirectReplacement() {
        NodeObsoletionWithNoDirectReplacement change = new NodeObsoletionWithNoDirectReplacement();
        change.setAboutNode(util.getNode("0001"));
        change.setHasNondirectReplacement(new ArrayList<Node>());
        change.getHasNondirectReplacement().add(util.getNode("0002"));

        testParse("obsolete EX:0001 with alternative EX:0002", change);

        change.getHasNondirectReplacement().add(util.getNode("0003"));
        testParse("obsolete EX:0001 with alternative EX:0002,EX:0003", change);
    }

    @Test
    void testUnobsoleteChange() {
        NodeUnobsoletion change = new NodeUnobsoletion();
        change.setAboutNode(util.getNode("0001"));

        testParse("unobsolete EX:0001", change);
    }

    @Test
    void testDeleteChange() {
        NodeDeletion change = new NodeDeletion();
        change.setAboutNode(util.getNode("0001"));

        testParse("delete EX:0001", change);
    }

    @Test
    void testNewSynonymChange() {
        String[] qualifiers = { null, "exact", "narrow", "broad", "related" };

        for ( int i = 0; i < qualifiers.length; i++ ) {
            String qualifier = qualifiers[i];
            NewSynonym change = new NewSynonym();
            change.setAboutNode(util.getNode("0001"));
            change.setNewValue("new synonym");
            change.setQualifier(qualifier);

            String kgcl = String.format("create %s synonym 'new synonym' for EX:0001",
                    qualifier != null ? qualifier : "");
            testParse(kgcl, change);
        }
    }

    @Test
    void testRemoveSynonymChange() {
        RemoveSynonym change = new RemoveSynonym();
        change.setAboutNode(util.getNode("0001"));
        change.setOldValue("old synonym");

        testParse("remove synonym 'old synonym' for EX:0001", change);
    }

    @Test
    void testChangeSynonymChange() {
        SynonymReplacement change = new SynonymReplacement();
        change.setAboutNode(util.getNode("0001"));
        change.setOldValue("old synonym");
        change.setNewValue("new synonym");

        testParse("change synonym from 'old synonym' to 'new synonym' for EX:0001", change);
    }

    @Test
    void testNewDefinitionChange() {
        NewTextDefinition change = new NewTextDefinition();
        change.setAboutNode(util.getNode("0001"));
        change.setNewValue("new definition");

        testParse("add definition 'new definition' to EX:0001", change);
    }

    @Test
    void testRemoveDefinitionChange() {
        RemoveTextDefinition change = new RemoveTextDefinition();
        change.setAboutNode(util.getNode("0001"));

        testParse("remove definition for EX:0001", change);
    }

    @Test
    void testChangeDefinitionChange() {
        TextDefinitionReplacement change = new TextDefinitionReplacement();
        change.setAboutNode(util.getNode("0001"));
        change.setNewValue("new definition");

        testParse("change definition of EX:0001 to 'new definition'", change);

        change.setOldValue("old definition");
        testParse("change definition of EX:0001 from 'old definition' to 'new definition'", change);
    }

    @Test
    void testNewClassChange() {
        ClassCreation change = new ClassCreation();
        change.setAboutNode(util.getNode("0001"));
        change.setNewValue("new label");

        testParse("create class EX:0001 'new label'", change);
    }

    @Test
    void testNewEdgeChange() {
        EdgeCreation change = new EdgeCreation();
        change.setSubject(util.getNode("0001"));
        change.setPredicate(util.getNode("is_related_to"));
        change.setObject(util.getNode("0002"));

        testParse("create edge EX:0001 EX:is_related_to EX:0002", change);
    }

    @Test
    void testDeleteEdgeChange() {
        EdgeDeletion change = new EdgeDeletion();
        change.setSubject(util.getNode("0001"));
        change.setPredicate(util.getNode("is_related_to"));
        change.setObject(util.getNode("0002"));

        testParse("delete edge EX:0001 EX:is_related_to EX:0002", change);
    }

    @Test
    void testNodeMoveChange() {
        NodeMove change = new NodeMove();
        change.setAboutEdge(util.getEdge("0001", null, null));
        change.setOldValue(util.getId("0002"));
        change.setNewValue(util.getId("0003"));

        testParse("move EX:0001 from EX:0002 to EX:0003", change);
    }

    @Test
    void testNodeDeepeningChange() {
        NodeDeepening change = new NodeDeepening();
        change.setAboutEdge(util.getEdge("0001", null, null));
        change.setOldValue(util.getId("0002"));
        change.setNewValue(util.getId("0003"));

        testParse("deepen EX:0001 from EX:0002 to EX:0003", change);
    }

    @Test
    void testNodeShallowingChange() {
        NodeShallowing change = new NodeShallowing();
        change.setAboutEdge(util.getEdge("0001", null, null));
        change.setOldValue(util.getId("0002"));
        change.setNewValue(util.getId("0003"));

        testParse("shallow EX:0001 from EX:0002 to EX:0003", change);
    }

    @Test
    void testPredicateChange() {
        PredicateChange change = new PredicateChange();
        change.setAboutEdge(util.getEdge("0001", null, "0002"));
        change.setOldValue(util.getId("is_a"));
        change.setNewValue(util.getId("is_related_to"));

        testParse("change relationship between EX:0001 and EX:0002 from EX:is_a to EX:is_related_to", change);
    }

    @Test
    void testAnnotationChange() {
        NodeAnnotationChange change = new NodeAnnotationChange();
        change.setAboutNode(util.getNode("0001"));
        change.setAnnotationProperty(util.getId("hasProperty"));
        change.setOldValue("old value");
        change.setNewValue("new value");

        testParse("change annotation of EX:0001 with EX:hasProperty from 'old value' to 'new value'", change);
    }

    @Test
    void testAddSubsetChange() {
        AddNodeToSubset change = new AddNodeToSubset();
        change.setAboutNode(util.getNode("0001"));
        OntologySubset subset = new OntologySubset();
        subset.setId(util.getId("subset_a"));
        change.setInSubset(subset);

        testParse("add EX:0001 to subset EX:subset_a", change);
    }

    @Test
    void testRemoveSubsetChange() {
        RemoveNodeFromSubset change = new RemoveNodeFromSubset();
        change.setAboutNode(util.getNode("0001"));
        OntologySubset subset = new OntologySubset();
        subset.setId(util.getId("subset_a"));
        change.setInSubset(subset);

        testParse("remove EX:0001 from subset EX:subset_a", change);
    }

    /*
     * Helper method to test the KGCLReader. It initialises a non-file-based reader,
     * calls the specified callback with the reader, then checks that the change
     * parsed by the reader matches what was expected.
     */
    void testParse(Consumer<KGCLReader> c, Change expected) {
        KGCLReader reader = new KGCLReader();
        c.accept(reader);

        Assertions.assertTrue(reader.getErrors().isEmpty());

        List<Change> changes = reader.getChangeSet();
        if ( expected == null ) {
            // The parser should always return an empty list.
            Assertions.assertTrue(changes.isEmpty());
        } else {
            Assertions.assertEquals(1, changes.size());
            Assertions.assertEquals(expected, changes.get(0));
        }
    }

    /*
     * Variant of the previous method to test parsing a single string.
     */
    void testParse(String kgcl, Change expected) {
        testParse(r -> {
            r.setPrefixManager(util.getPrefixManager());
            Assertions.assertTrue(r.read(kgcl));
        }, expected);
    }

    /*
     * Try parsing a known bogus KGCL string and check that it fails as expected.
     */
    void testParseFail(String kgcl) {
        KGCLReader reader = new KGCLReader();
        Assertions.assertFalse(reader.read(kgcl));
        Assertions.assertTrue(reader.getErrors().size() > 0);
    }
}
