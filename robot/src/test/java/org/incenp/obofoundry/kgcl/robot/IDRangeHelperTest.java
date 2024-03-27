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

package org.incenp.obofoundry.kgcl.robot;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.incenp.obofoundry.idrange.IDRangePolicyException;
import org.incenp.obofoundry.kgcl.IAutoIDGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class IDRangeHelperTest {

    private final static String testFile = "../core/src/test/resources/idranges.owl";

    @Test
    void testFindOneIDRangeFile() throws IOException {
        File tmpFile = new File("test-idranges.owl");
        FileUtils.copyFile(new File(testFile), tmpFile);

        String found = IDRangeHelper.findIDRangeFile();
        Assertions.assertEquals("test-idranges.owl", found);

        tmpFile.delete();
    }

    @Test
    void testFindOnlyOneFile() throws IOException {
        File srcFile = new File(testFile);
        File tmpFile1 = new File("test1-idranges.owl");
        File tmpFile2 = new File("test2-idranges.owl");
        FileUtils.copyFile(srcFile, tmpFile1);
        FileUtils.copyFile(srcFile, tmpFile2);

        String found = IDRangeHelper.findIDRangeFile();
        Assertions.assertNull(found);

        tmpFile1.delete();
        tmpFile2.delete();
    }

    @Test
    void testGetGeneratorForRequestedRange() {
        String[] defaults = new String[] { "Bob", "Charlie" };
        try {
            IAutoIDGenerator gen = IDRangeHelper.getIDGenerator(getOntology(), testFile, "Alice", defaults, false);
            String nextID = gen.nextID();
            Assertions.assertEquals("https://example.org/0001", nextID);
        } catch ( IDRangePolicyException e ) {
            Assertions.fail("Unexpected exception", e);
        }
    }

    @Test
    void testFailIfRequestedRangeDoesNotExist() {
        String[] defaults = new String[] { "Bob", "Charlie" };
        Assertions.assertThrows(IDRangePolicyException.class,
                () -> IDRangeHelper.getIDGenerator(getOntology(), testFile, "Charlie", defaults, false),
                "Requested range not found in ID range file");
    }

    @Test
    void testGetGeneratorForDefaultRange() {
        String[] defaults = new String[] { "Bob", "Charlie" };
        try {
            IAutoIDGenerator gen = IDRangeHelper.getIDGenerator(getOntology(), testFile, null, defaults, false);
            String nextID = gen.nextID();
            Assertions.assertEquals("https://example.org/0501", nextID);
        } catch ( IDRangePolicyException e ) {
            Assertions.fail("Unexpected exception", e);
        }
    }

    @Test
    void testFailIfNoRangeFound() {
        String[] defaults = new String[] { "Charlie", "Daphne" };
        Assertions.assertThrows(IDRangePolicyException.class,
                () -> IDRangeHelper.getIDGenerator(getOntology(), testFile, null, defaults, false),
                "No suitable range found in ID range file");
    }

    @Test
    void testMaybeFailUponErrorIfRangeFileExplicitlyRequested() {
        String[] defaults = new String[] { "Bob", "Charlie" };
        Assertions.assertThrows(IDRangePolicyException.class,
                () -> IDRangeHelper.maybeGetIDGenerator(getOntology(), testFile, "Charlie", defaults, false),
                "Requested range not found in ID range file");
    }

    @Test
    void testMaybeDontFailUponErrorIfUsingDefaultFile() throws IOException {
        File tmpFile = new File("test-idranges.owl");
        FileUtils.copyFile(new File(testFile), tmpFile);

        String[] defaults = new String[] {"Bob", "Charlie"};
        try {
            IAutoIDGenerator gen = IDRangeHelper.maybeGetIDGenerator(getOntology(), null, "Charlie", defaults, false);
            Assertions.assertNull(gen);
        } catch ( IDRangePolicyException e ) {
            Assertions.fail("Unexpected exception", e);
        }
    }

    private OWLOntology getOntology() {
        try {
            return OWLManager.createOWLOntologyManager().createOntology();
        } catch ( OWLOntologyCreationException e ) {
            Assertions.fail("Cannot create test ontology");
            return null;
        }
    }
}
