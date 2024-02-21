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
import java.util.ArrayList;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyPatcherTest {

    private static final TestUtils util = new TestUtils("http://www.co-ode.org/ontologies/pizza/pizza.owl#");

    private OWLOntology ontology;
    private OntologyPatcher patcher;

    @BeforeEach
    private void initialisePatcher() {
        OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();

        try {
            ontology = mgr.loadOntologyFromOntologyDocument(new File("src/test/resources/pizza.ofn"));
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail(e);
        }

        patcher = new OntologyPatcher(ontology, null);
    }

    @Test
    void testApplyOneChange() {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(util.getNode("SultanaTopping"));

        // We're not going to test whether the ontology is changed in exactly the way it
        // should be. We'll just check the number of axioms. The DirectOWLTranslatorTest
        // is there to check that changes are translated into the correct axioms.
        int nOrigAxioms = ontology.getAxiomCount();

        Assertions.assertTrue(patcher.apply(change));
        Assertions.assertFalse(patcher.hasRejectedChanges());

        int nAxioms = ontology.getAxiomCount();
        Assertions.assertEquals(5, nOrigAxioms - nAxioms);
    }

    @Test
    void testApplyChangesetWithInvalidChange() {
        NodeObsoletion c1 = new NodeObsoletion();
        c1.setAboutNode(util.getNode("SultanaTopping"));

        RemoveSynonym c2 = new RemoveSynonym();
        c2.setAboutNode(util.getNode("LaReine"));
        c2.setOldValue("The Queen");

        ArrayList<Change> changeset = new ArrayList<Change>();
        changeset.add(c1);
        changeset.add(c2);

        int nOrigAxioms = ontology.getAxiomCount();

        Assertions.assertFalse(patcher.apply(changeset));

        int nAxioms = ontology.getAxiomCount();
        Assertions.assertEquals(5, nOrigAxioms - nAxioms);

        Assertions.assertTrue(patcher.hasRejectedChanges());
        Assertions.assertEquals(1, patcher.getRejectedChanges().size());

        RejectedChange rejected = patcher.getRejectedChanges().get(0);
        Assertions.assertEquals(c2, rejected.getChange());
        Assertions.assertEquals(
                "Synonym \"The Queen\" not found on <http://www.co-ode.org/ontologies/pizza/pizza.owl#LaReine>",
                rejected.getReason());
    }

    @Test
    void testApplyValidChangesetWithNoPartialApply() {
        NodeObsoletion c1 = new NodeObsoletion();
        c1.setAboutNode(util.getNode("SultanaTopping"));

        ArrayList<Change> changeset = new ArrayList<Change>();
        changeset.add(c1);

        int nOrigAxioms = ontology.getAxiomCount();

        Assertions.assertTrue(patcher.apply(changeset, true));

        int nAxioms = ontology.getAxiomCount();
        Assertions.assertEquals(5, nOrigAxioms - nAxioms);
        Assertions.assertFalse(patcher.hasRejectedChanges());
    }

    @Test
    void testApplyInvalidChangesetWithNoPartialApply() {
        NodeObsoletion c1 = new NodeObsoletion();
        c1.setAboutNode(util.getNode("SultanaTopping"));

        RemoveSynonym c2 = new RemoveSynonym();
        c2.setAboutNode(util.getNode("LaReine"));
        c2.setOldValue("The Queen");

        ArrayList<Change> changeset = new ArrayList<Change>();
        changeset.add(c1);
        changeset.add(c2);

        int nOrigAxioms = ontology.getAxiomCount();

        Assertions.assertFalse(patcher.apply(changeset, true));

        int nAxioms = ontology.getAxiomCount();
        Assertions.assertEquals(nOrigAxioms, nAxioms);
    }
}
