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

package org.incenp.obofoundry.idrange;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IDRangePolicyParserTest {

    @Test
    void testLoadingSamplePolicy() throws Exception {
        IDRangePolicyParser parser = new IDRangePolicyParser("src/test/resources/idranges.owl");
        IIDRangePolicy policy = parser.parse();

        Assertions.assertEquals("https://example.org/", policy.getPrefix());
        Assertions.assertEquals("EX", policy.getPrefixName());
        Assertions.assertEquals(4, policy.getWidth());

        IDRange alice = policy.getRange("Alice");
        Assertions.assertNotNull(alice);
        Assertions.assertEquals(1, alice.getLowerBound());
        Assertions.assertEquals(500000, alice.getUpperBound());

        IDRange bob = policy.getRange("Bob");
        Assertions.assertNotNull("bob");
        Assertions.assertEquals(500001, bob.getLowerBound());
        Assertions.assertEquals(1000000, bob.getUpperBound());
    }
}
