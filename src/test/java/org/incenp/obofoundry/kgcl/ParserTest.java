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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.junit.jupiter.api.Test;

class ParserTest {

    KGCLReader stringReader = new KGCLReader();

    @Test
    void testFileParser() throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("sample1.kgcl");
        KGCLReader reader = new KGCLReader(in);
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
            assertNull(changes);
            assertTrue(errors.size() > 0);
        } else {
            assertTrue(success);
            assertNotNull(changes);
            assertEquals(expectedChanges, changes.size());
            assertEquals(0, errors.size());
        }
    }
}
