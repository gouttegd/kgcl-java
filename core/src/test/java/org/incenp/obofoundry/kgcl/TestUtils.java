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

import java.util.Collection;

import org.incenp.obofoundry.kgcl.model.Edge;
import org.incenp.obofoundry.kgcl.model.Node;
import org.junit.jupiter.api.Assertions;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/*
 * A class with helper methods to test KGCL changes.
 */
public class TestUtils {

    // Default base IRI for IDs constructed by this class
    public final static String EXAMPLE_BASE = "https://example.org/";

    private String base_iri;
    private OWLDataFactory factory;

    // Creates a new instance with a specific base IRI
    public TestUtils(String base_iri) {
        this.base_iri = base_iri;
        factory = OWLManager.getOWLDataFactory();
    }

    // Creates a new instance with a specific base IRI and a pre-existing factory
    public TestUtils(String base_iri, OWLDataFactory factory) {
        this.base_iri = base_iri;
        this.factory = factory;
    }

    // Creates a new instance with the default base IRI
    public TestUtils() {
        this(EXAMPLE_BASE);
    }

    // Gets an ID in the base namespace
    public String getId(String id) {
        return base_iri + id;
    }

    // Gets an IRI in the base namespace
    public IRI getIRI(String id) {
        return IRI.create(getId(id));
    }

    // Gets a KGCL node with an ID in the base namespace
    public Node getNode(String id) {
        Node node = new Node();
        node.setId(getId(id));
        return node;
    }

    // Gets a KGCL node with an absolute ID
    public Node getForeignNode(String id) {
        Node node = new Node();
        node.setId(id);
        return node;
    }

    // Gets a KGCL edge with subject, predicate, and object IDs relative to the base
    // namespace
    public Edge getEdge(String subject, String predicate, String object) {
        Edge edge = new Edge();
        if ( subject != null ) {
            edge.setSubject(getNode(subject));
        }
        if ( predicate != null ) {
            edge.setPredicate(getNode(predicate));
        }
        if ( object != null ) {
            edge.setObject(getNode(object));
        }
        return edge;
    }

    // Gets a prefix manager with "EX:" associated to the base namespace
    public PrefixManager getPrefixManager() {
        DefaultPrefixManager pm = new DefaultPrefixManager();
        pm.setPrefix("EX:", base_iri);
        return pm;
    }

    // Gets a OWL class in the base namespace
    public OWLClass getKlass(String id) {
        return factory.getOWLClass(getIRI(id));
    }

    // Gets a OWL object property in the base namespace
    public OWLObjectProperty getObjectProperty(String id) {
        return factory.getOWLObjectProperty(getIRI(id));
    }

    // Compare the contents of iterables, without expecting that items should appear
    // in any given order.
    public <T> void assertUnsortedIterableEquals(Collection<T> expected, Collection<T> actual) {
        Assertions.assertEquals(expected.size(), actual.size());
        for ( T item : expected ) {
            Assertions.assertTrue(actual.contains(item),
                    String.format("Expected item not found: %s\n", item.toString()));
        }
    }
}
