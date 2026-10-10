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

package org.incenp.obofoundry.kgcl.owl;

import java.io.File;
import java.util.ArrayList;

import org.incenp.obofoundry.kgcl.RejectedChange;
import org.incenp.obofoundry.kgcl.TestUtils;
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
    void initialisePatcher() {
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
