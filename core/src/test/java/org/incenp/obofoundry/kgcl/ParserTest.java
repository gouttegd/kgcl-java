/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023 Damien Goutte-Gattat
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.junit.jupiter.api.Test;

class ParserTest {

    KGCLReader stringReader = new KGCLReader();

    @Test
    void testFileParser() throws IOException {
        KGCLReader reader = new KGCLReader("src/test/resources/sample1.kgcl");
        assertTrue(reader.read());
        assertEquals(5, reader.getChangeSet().size());
    }

    @Test
    void testEmptyInput() {
        // An "empty" string (really empty or containing only blanks) should be parsed
        // successfully and yield an empty changeset.
        doTestString("", 0);
        doTestString(" ", 0);
        doTestString("\n", 0);
        doTestString("\n\n", 0);
    }

    @Test
    void testCommentedInputOnly() {
        // Same for a string containing only comments...
        doTestString("# comment\n", 0);
        doTestString("\n# comment\n", 0);

        // except this one: comments must end with a new line
        doTestString("# comment", -1);
    }

    @Test
    void testSingleChange() {
        doTestString("obsolete EX:0001", 1);
        doTestString("obsolete EX:0001\n", 1);
        doTestString("\nobsolete EX:0001", 1);
        doTestString("\nobsolete EX:0001\n", 1);
        doTestString("# comment\nobsolete EX:0001", 1);
        doTestString("# comment\nobsolete EX:0001\n# comment\n", 1);
    }

    @Test
    void testRenameChange() {
        doTestString("rename EX:0001 from 'old label' to 'new label'", 1);
    }

    @Test
    void testObsoleteChange() {
        doTestString("obsolete EX:0001", 1);
        doTestString("obsolete EX:0001 with replacement EX:0002", 1);
        doTestString("obsolete EX:0001 with alternative EX:0002", 1);
        doTestString("obsolete EX:0001 with alternative EX:0002,EX:0003", 1);
    }

    @Test
    void testUnobsoleteChange() {
        doTestString("unobsolete EX:0001", 1);
    }

    @Test
    void testDeleteChange() {
        doTestString("delete EX:0001", 1);
    }

    @Test
    void testNewSynonymChange() {
        doTestString("create synonym 'new synonym' for EX:0001", 1);
        doTestString("create exact synonym 'new synonym' for EX:0001", 1);
        doTestString("create narrow synonym 'new synonym' for EX:0001", 1);
        doTestString("create broad synonym 'new synonym' for EX:0001", 1);
        doTestString("create related synonym 'new synonym' for EX:0001", 1);
    }

    @Test
    void testRemoveSynonymChange() {
        doTestString("remove synonym 'old synonym' for EX:0001", 1);
    }

    @Test
    void testChangeSynonymChange() {
        doTestString("change synonym from 'old synonym' to 'new synonym' for EX:0001", 1);
    }

    @Test
    void testNewDefinitionChange() {
        doTestString("add definition 'new definition' to EX:0001", 1);
    }

    @Test
    void testRemoveDefinitionChange() {
        doTestString("remove definition for EX:0001", 1);
    }

    @Test
    void testChangeDefinitionChange() {
        doTestString("change definition of EX:0001 to 'new definition'", 1);
        doTestString("change definition of EX:0001 from 'old definition' to 'new definition'", 1);
    }

    @Test
    void testNewClassChange() {
        doTestString("create class EX:0001 'new label'", 1);
    }

    @Test
    void testNewEdgeChange() {
        doTestString("create edge EX:0001 PRED:0002 EX:0003", 1);
    }

    @Test
    void testDeleteEdgeChange() {
        doTestString("delete edge EX:0001 PRED:0002 EX:0003", 1);
    }

    @Test
    void testMoveChange() {
        doTestString("move EX:0001 from EX:0002 to EX:0003", 1);
        doTestString("deepen EX:0001 from EX:0002 to EX:0003", 1);
        doTestString("shallow EX:0001 from EX:0002 to EX:0003", 1);
    }

    @Test
    void testPredicateChange() {
        doTestString("change relationship between EX:0001 and EX:0002 from EX:0003 to EX:0004", 1);
    }

    @Test
    void testAnnotationChange() {
        doTestString("change annotation of EX:0001 with PROP:0002 from 'old value' to 'new value'", 1);
    }

    /*
     * Attempts to parse a KGCL string and checks that the parser either returns the
     * expected number of changes. If expectedChanges is negative, the parser is
     * expected to fail.
     */
    void doTestString(String kgcl, int expectedChanges) {
        boolean success = stringReader.read(kgcl);
        List<Change> changes = stringReader.getChangeSet();
        List<KGCLSyntaxError> errors = stringReader.getErrors();

        if ( expectedChanges < 0 ) {
            // Expected failure
            assertFalse(success);
            assertNotNull(changes);
            assertEquals(changes.size(), 0);
            assertTrue(errors.size() > 0);
        } else {
            assertTrue(success);
            assertNotNull(changes);
            assertEquals(expectedChanges, changes.size());
            assertEquals(0, errors.size());
        }
    }
}
