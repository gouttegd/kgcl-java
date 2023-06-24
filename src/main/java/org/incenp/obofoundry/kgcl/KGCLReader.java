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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
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
    private ErrorListener errorListener = new ErrorListener();
    public List<Change> changeSet;

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
        input = new BufferedReader(new InputStreamReader(kgclInput));
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
     * Parse the KGCL program from the source. After this method returns true, call
     * the {@link getChangeSet} method to get the result.
     * 
     * @return {@code true} if the program was successfully parsed, {@code false} if
     *         syntax errors were found.
     * @throws IOException If any non-KGCL I/O error occurs when parsing.
     */
    public boolean read() throws IOException {
        KGCLLexer lexer = new KGCLLexer(CharStreams.fromReader(input));
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        KGCLParser parser = new KGCLParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        ParseTree tree = parser.changeset();
        if ( !hasErrors() ) {
            ParseTree2ChangeVisitor visitor = new ParseTree2ChangeVisitor(prefixManager);
            visitor.visit(tree);
            changeSet = visitor.getChangeSet();
        }

        return !hasErrors();
    }

    /**
     * Get the KGCL changeset read from the source. This method should be called
     * after calling {@link read} and checking that it returned {@code true}. As a
     * convenience, if the {@code read} method has not been called, this method will
     * automatically it; however in that case it will not possible to get the syntax
     * errors that may have been encountered.
     * 
     * @return The KGCL changeset. May be {@code null} if syntax errors occurred
     *         when parsing.
     * @throws IOException If any non-KGCL I/O error occurs when parsing.
     */
    public List<Change> getChangeSet() throws IOException {
        if ( changeSet == null && !hasErrors() ) {
            read();
        }
        return changeSet;
    }

    /**
     * Indicate whether parsing errors occurred.
     * 
     * @return {@code true} if at least one parsing error occurred, otherwise
     *         {@code false}.
     */
    public boolean hasErrors() {
        return !errorListener.errors.isEmpty();
    }

    /**
     * Get all parsing errors that occurred, if any.
     * 
     * @return A list of objects representing the syntax errors (empty if no errors
     *         occurred).
     */
    public List<KGCLSyntaxError> getErrors() {
        return errorListener.errors;
    }

    private class ErrorListener extends BaseErrorListener {

        private ArrayList<KGCLSyntaxError> errors = new ArrayList<KGCLSyntaxError>();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object symbol, int line, int column,
                String msg, RecognitionException e) {
            errors.add(new KGCLSyntaxError(line, column, msg));
        }
    }
}
