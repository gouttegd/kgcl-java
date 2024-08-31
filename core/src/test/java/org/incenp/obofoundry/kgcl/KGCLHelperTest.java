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
import java.util.ArrayList;
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
    private void initialisePatcher() {
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
        List<Change> changeset = null;
        try {
            changeset = KGCLHelper.parse("obsolete pizza:LaReine", prefixManager);
        } catch ( IOException e ) {
            Assertions.fail(e);
        }

        Assertions.assertEquals(1, changeset.size());
    }

    @Test
    void testStringParsingWithSyntaxError() {
        List<Change> changeset = null;
        try {
            changeset = KGCLHelper.parse("not kgcl", prefixManager);
        } catch ( IOException e ) {
            Assertions.fail(e);
        }

        Assertions.assertTrue(changeset.isEmpty());
    }

    @Test
    void testStringParsingWithErrorCollection() {
        ArrayList<KGCLSyntaxError> errors = new ArrayList<KGCLSyntaxError>();
        List<Change> changeset = null;
        try {
            changeset = KGCLHelper.parse("not kgcl", prefixManager, errors);
        } catch ( IOException e ) {
            Assertions.fail(e);
        }

        Assertions.assertTrue(changeset.isEmpty());
        Assertions.assertFalse(errors.isEmpty());
    }

    @Test
    void testUsingLabelsAsIds() {
        List<Change> changeset = null;
        List<KGCLSyntaxError> errors = new ArrayList<KGCLSyntaxError>();
        IEntityLabelResolver resolver = new OntologyBasedLabelResolver(ontology);

        try {
            changeset = KGCLHelper.parse("obsolete 'LaReine'", prefixManager, errors, resolver);
        } catch ( IOException e ) {
            Assertions.fail(e);
        }
        Assertions.assertEquals(1, changeset.size());
        Assertions.assertTrue(errors.isEmpty());

        try {
            changeset = KGCLHelper.parse("obsolete 'bogus label'", prefixManager, errors, resolver);
        } catch ( IOException e ) {
            Assertions.fail(e);
        }
        Assertions.assertTrue(changeset.isEmpty());
        Assertions.assertEquals(1, errors.size());
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
