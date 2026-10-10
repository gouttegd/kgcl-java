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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.owl.OntologyBasedLabelResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;

public class KGCLHelperTest {

    private static final TestUtils util = new TestUtils("http://www.co-ode.org/ontologies/pizza/pizza.owl#");

    private OWLOntology ontology;
    private PrefixManager prefixManager;

    @BeforeEach
    void initialisePatcher() {
        OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();

        try {
            ontology = mgr.loadOntologyFromOntologyDocument(new File("src/test/resources/pizza.ofn"));
            prefixManager = mgr.getOntologyFormat(ontology).asPrefixOWLOntologyFormat();
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail(e);
        }
    }

    @Test
    void testStringParsing() {
        List<Change> changeset = KGCLHelper.parse("obsolete pizza:LaReine", prefixManager);
        Assertions.assertEquals(1, changeset.size());
    }

    @Test
    void testStringParsingWithSyntaxError() {
        List<Change> changeset = KGCLHelper.parse("not kgcl", prefixManager);
        Assertions.assertTrue(changeset.isEmpty());
    }

    @Test
    void testStringParsingWithErrorCollection() {
        ArrayList<KGCLSyntaxError> errors = new ArrayList<KGCLSyntaxError>();
        List<Change> changeset = KGCLHelper.parse("not kgcl", prefixManager, errors);

        Assertions.assertTrue(changeset.isEmpty());
        Assertions.assertFalse(errors.isEmpty());
    }

    @Test
    void testUsingLabelsAsIds() {
        List<Change> changeset = null;
        List<KGCLSyntaxError> errors = new ArrayList<KGCLSyntaxError>();
        SimpleLabelResolver resolver = new OntologyBasedLabelResolver(ontology);

        changeset = KGCLHelper.parse("obsolete 'LaReine'", prefixManager, errors, resolver);
        Assertions.assertEquals(1, changeset.size());
        Assertions.assertTrue(errors.isEmpty());

        changeset = KGCLHelper.parse("obsolete 'bogus label'", prefixManager, errors, resolver);
        Assertions.assertTrue(changeset.isEmpty());
        Assertions.assertEquals(1, errors.size());
    }

    @Test
    void testUsingCustomPrefixMap() {
        List<Change> changeset = null;
        HashMap<String, String> prefixMap = new HashMap<String, String>();
        prefixMap.put("EXA", TestUtils.EXAMPLE_BASE);

        changeset = KGCLHelper.parse("obsolete EXA:0001", prefixMap, null, null);
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(new TestUtils().getNode("0001"));
        Assertions.assertEquals(change, changeset.get(0));
    }

    @Test
    void applyChangeset() {
        int nOrigAxioms = ontology.getAxiomCount();
        KGCLHelper.apply(getChangeset(true), ontology, null, false);
        int nAxioms = ontology.getAxiomCount();

        Assertions.assertEquals(5, nOrigAxioms - nAxioms);
    }

    @Test
    void testApplyChangesetWithErrorCollection() {
        List<Change> changeset = getChangeset(true);
        List<RejectedChange> rejects = new ArrayList<RejectedChange>();
        int nOrigAxioms = ontology.getAxiomCount();

        KGCLHelper.apply(changeset, ontology, null, false, rejects);
        int nAxioms = ontology.getAxiomCount();

        Assertions.assertEquals(5, nOrigAxioms - nAxioms);
        Assertions.assertEquals(1, rejects.size());
        Assertions.assertEquals(changeset.get(1), rejects.get(0).getChange());
    }

    private List<Change> getChangeset(boolean withInvalid) {
        ArrayList<Change> changeset = new ArrayList<Change>();

        NodeObsoletion c1 = new NodeObsoletion();
        c1.setAboutNode(util.getNode("SultanaTopping"));
        changeset.add(c1);

        if ( withInvalid ) {
            RemoveSynonym c2 = new RemoveSynonym();
            c2.setAboutNode(util.getNode("LaReine"));
            c2.setOldValue("The Queen");
            changeset.add(c2);
        }

        return changeset;
    }
}
