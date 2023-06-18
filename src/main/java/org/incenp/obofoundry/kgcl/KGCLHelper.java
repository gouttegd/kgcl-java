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

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * A class providing static helper methods to work with KGCL.
 */
public class KGCLHelper {

    /**
     * Parse KGCL from a string.
     * 
     * @param kgcl     The KGCL instructions to parse.
     * @param ontology An ontology that may provide a prefix manager to allow the
     *                 parser to expand CURIEs into IRIs. May be null.
     * @return A KGCL changeset.
     * @throws IOException
     */
    public static List<Change> parse(String kgcl, OWLOntology ontology) throws IOException {
        KGCLReader reader = new KGCLReader(new StringReader(kgcl));
        reader.setPrefixManager(ontology);
        return reader.read();
    }

    /**
     * Parse KGCL from a file.
     * 
     * @param kgcl     The file to parse.
     * @param ontology An ontology that may provide a prefix manager to allow the
     *                 parser to expand CURIEs into IRIs. May be null.
     * @return A KGCL changeset.
     * @throws IOException
     */
    public static List<Change> parse(File kgcl, OWLOntology ontology) throws IOException {
        KGCLReader reader = new KGCLReader(kgcl);
        reader.setPrefixManager(ontology);
        return reader.read();
    }

    /**
     * Apply a KGCL changeset to an ontology.
     * 
     * @param changeset The changeset to apply.
     * @param ontology  The ontology to apply it to.
     */
    public static void apply(List<Change> changeset, OWLOntology ontology) {
        Change2OwlVisitor visitor = new Change2OwlVisitor(ontology);
        for ( Change change : changeset ) {
            change.accept(visitor);
        }
        visitor.apply();
    }
}
