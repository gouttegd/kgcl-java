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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.parser.KGCLLexer;
import org.incenp.obofoundry.kgcl.parser.KGCLParser;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * A class providing static helper methods to work with KGCL. This is intended
 * to be the main public interface.
 */
public class KGCLHelper {

    private static KGCLParser getParser(CharStream stream) {
        KGCLLexer lexer = new KGCLLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new KGCLParser(tokens);
    }

    private static List<Change> doParse(KGCLParser parser, OWLOntology ontology) {
        ParseTree tree = parser.changeset();

        PrefixManager prefixManager = null;
        if ( ontology.getOWLOntologyManager().getOntologyFormat(ontology).isPrefixOWLOntologyFormat() ) {
            prefixManager = ontology.getOWLOntologyManager().getOntologyFormat(ontology).asPrefixOWLOntologyFormat();
        }

        ParseTree2ChangeVisitor visitor = new ParseTree2ChangeVisitor(prefixManager);
        visitor.visit(tree);
        return visitor.getChangeSet();
    }

    /**
     * Parse KGCL from a string.
     * 
     * @param kgcl     The KGCL instructions to parse.
     * @param ontology An ontology that may provide a prefix manager to allow the
     *                 parser to expand CURIEs into IRIs. May be null.
     * @return A KGCL changeset.
     */
    public static List<Change> parse(String kgcl, OWLOntology ontology) {
        KGCLParser parser = getParser(CharStreams.fromString(kgcl));
        return doParse(parser, ontology);
    }

    /**
     * Parse KGCL from a file.
     * 
     * @param kgcl     The file to parse.
     * @param ontology An ontology that may provide a prefix manager to allow the
     *                 parser to expand CURIEs into IRIs. May be null.
     * @return A KGCL changeset.
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<Change> parse(File kgcl, OWLOntology ontology) throws FileNotFoundException, IOException {
        KGCLParser parser = getParser(CharStreams.fromStream(new FileInputStream(kgcl)));
        return doParse(parser, ontology);
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
