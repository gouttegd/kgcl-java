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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.parser.KGCLLexer;
import org.incenp.obofoundry.kgcl.parser.KGCLParser;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * A parser to read a KGCL program.
 */
public class KGCLReader {
    private Reader input;
    private PrefixManager prefixManager;

    /**
     * Create a new instance to read from a reader object.
     * 
     * @param kgclInput The reader to get the KGCL instructions from.
     */
    public KGCLReader(Reader kgclInput) {
        input = kgclInput;
    }

    /**
     * Create a new instance to read from a stream.
     * 
     * @param kgclInput The stream to get the KGCL instructions from.
     */
    public KGCLReader(InputStream kgclInput) {
        input = new BufferedReader(input);
    }

    /**
     * Create a new instance to read from a file.
     * 
     * @param kgclFile The file to read the KGCL instructions from.
     * @throws FileNotFoundException If the file cannot be found.
     */
    public KGCLReader(File kgclFile) throws FileNotFoundException {
        input = new BufferedReader(new FileReader(kgclFile));
    }

    /**
     * Create a new instance to read from a file.
     * 
     * @param kgclFilename The name of the file to read from.
     * @throws FileNotFoundException If the file cannot be found.
     */
    public KGCLReader(String kgclFilename) throws FileNotFoundException {
        this(new File(kgclFilename));
    }

    /**
     * Set the prefix manager to use to expand short identifiers.
     * 
     * @param manager The prefix manager (may be {@code null}).
     */
    public void setPrefixManager(PrefixManager manager) {
        prefixManager = manager;
    }

    /**
     * Use the prefix manager from the specified ontology.
     * 
     * @param ontology The ontology whose prefix manager shall be used.
     */
    public void setPrefixManager(OWLOntology ontology) {
        if ( ontology != null ) {
            OWLDocumentFormat format = ontology.getOWLOntologyManager().getOntologyFormat(ontology);
            if ( format.isPrefixOWLOntologyFormat() ) {
                prefixManager = format.asPrefixOWLOntologyFormat();
            }
        }
    }

    /**
     * Parse the KGCL program from the source.
     * 
     * @return A list of KGCL change objects.
     * @throws IOException If any I/O error occurs when parsing.
     */
    public List<Change> read() throws IOException {
        KGCLLexer lexer = new KGCLLexer(CharStreams.fromReader(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        KGCLParser parser = new KGCLParser(tokens);

        ParseTree tree = parser.changeset();
        ParseTree2ChangeVisitor visitor = new ParseTree2ChangeVisitor(prefixManager);
        visitor.visit(tree);
        return visitor.getChangeSet();
    }
}
