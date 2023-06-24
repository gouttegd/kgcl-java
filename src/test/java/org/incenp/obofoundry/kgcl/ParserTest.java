package org.incenp.obofoundry.kgcl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

class ParserTest {

    @Test
    void testFileParser() throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("sample1.kgcl");
        KGCLReader reader = new KGCLReader(in);
        assertTrue(reader.read());
        assertEquals(reader.getChangeSet().size(), 5);
    }
}
