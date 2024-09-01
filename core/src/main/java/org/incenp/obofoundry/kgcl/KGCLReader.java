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
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.parser.IParseTreeErrorListener;
import org.incenp.obofoundry.kgcl.parser.KGCLLexer;
import org.incenp.obofoundry.kgcl.parser.KGCLParser;
import org.incenp.obofoundry.kgcl.parser.ParseTree2ChangeVisitor;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * A parser to read a KGCL program from a file or a file-like source.
 * <p>
 * This is the main class to deserialise KGCL objects from their textual
 * representation in the KGCL language. It abstracts most of the details of the
 * underlying parser.
 * <p>
 * Typical usage:
 * 
 * <pre>
 * KGCLReader reader = new KGCLReader("file.kgcl");
 * if ( reader.read() ) {
 *     // The file was parsed successfully
 *     List&lt;Change&gt; changeset = reader.getChangeset();
 *     // Work with KGCL changes in changeset...
 * } else {
 *     // Some syntax errors were found in the source file
 *     for ( KGCLSyntaxError error : reader.getErrors() ) {
 *         System.err.printf("KGCL syntax error: %s\n", error.toString());
 *     }
 * }
 * </pre>
 */
public class KGCLReader {
    private KGCLLexer lexer;
    private PrefixManager prefixManager;
    private ILabelResolver labelResolver;
    private ErrorListener errorListener = new ErrorListener();
    private List<Change> changeSet = new ArrayList<Change>();
    private boolean hasRead = false;

    /**
     * Creates a new instance without an input source. Use this constructor to parse
     * KGCL from something else than a file or file-like source, coupled with the
     * {@link #read(String)} method.
     * 
     * <pre>
     * KGCLReader reader = new KGCLReader();
     * reader.setPrefixManager(...);
     * reader.read("... kgcl commands ...");
     * </pre>
     */
    public KGCLReader() {
    }

    /**
     * Creates a new instance to read from a reader object.
     * 
     * @param kgclInput The reader to parse the KGCL program from.
     * @throws IOException If any non-KGCL I/O error occurs when reading from the
     *                     reader object.
     */
    public KGCLReader(Reader kgclInput) throws IOException {
        lexer = new KGCLLexer(CharStreams.fromReader(kgclInput));
    }

    /**
     * Creates a new instance to read from a stream.
     * 
     * @param kgclInput The stream to parse the KGCL program from.
     * @throws IOException If any non-KGCL I/O error occurs when reading from the
     *                     stream.
     */
    public KGCLReader(InputStream kgclInput) throws IOException {
        lexer = new KGCLLexer(CharStreams.fromStream(kgclInput));
    }

    /**
     * Creates a new instance to read from a file.
     * 
     * @param kgclFile The file to parse the KGCL program from.
     * @throws IOException If any non-KGCL I/O error occurs when reading from the
     *                     file.
     */
    public KGCLReader(File kgclFile) throws IOException {
        lexer = new KGCLLexer(CharStreams.fromFileName(kgclFile.getPath()));
    }

    /**
     * Creates a new instance to read from a file.
     * 
     * @param kgclFilename The name of the file to read from.
     * @throws IOException If any non-KGCL I/O error occurs when reading from the
     *                     file.
     */
    public KGCLReader(String kgclFilename) throws IOException {
        lexer = new KGCLLexer(CharStreams.fromFileName(kgclFilename));
    }

    /**
     * Sets the prefix manager to use to expand short identifiers. Most KGCL files
     * are expected to use short-form identifiers (commonly known as “CURIEs”) for
     * better readability. The prefix manager will convert such short-form
     * identifiers into the equivalent canonical, full-length identifier.
     * <p>
     * The prefix manager must be set before the {@link #read()} is called,
     * otherwise it will have no effect.
     * <p>
     * If no prefix manager is set, the underlying KGCL parser will leave any CURIE
     * it finds unexpanded. It will then be the caller’s responsibility to expand
     * the CURIEs in the resulting KGCL changes.
     * 
     * @param manager The OWL API prefix manager to use (may be {@code null} to
     *                leave the CURIEs in their unexpanded form).
     */
    public void setPrefixManager(PrefixManager manager) {
        prefixManager = manager;
    }

    /**
     * Sets the prefix manager from the specified ontology. This is a convenience
     * method that automatically gets the prefix manager from a OWL API
     * {@code OWLOntology} object and sets it as the prefix manager for the
     * underlying KGCL parser.
     * 
     * @param ontology The ontology whose prefix manager shall be used. If the
     *                 ontology has been read from a {@code OWLDocumentFormat} that
     *                 does not support prefixes, it is ignored and a default prefix
     *                 manager is used instead.
     * 
     * @deprecated Using an ontology as the prefix manager is no longer recommended.
     *             Provide the reader with an explicit PrefixManager (with
     *             {@link #setPrefixManager(PrefixManager)}) instead.
     */
    @Deprecated
    public void setPrefixManager(OWLOntology ontology) {
        if ( ontology != null ) {
            OWLDocumentFormat format = ontology.getOWLOntologyManager().getOntologyFormat(ontology);
            if ( format.isPrefixOWLOntologyFormat() ) {
                prefixManager = format.asPrefixOWLOntologyFormat();
            } else {
                prefixManager = new DefaultPrefixManager();
            }
        }
    }

    /**
     * Sets the prefix map to use to resolve short identifiers.
     * <p>
     * This is equivalent to calling {@link #setPrefixManager(PrefixManager)} with a
     * PrefixManager object initialised with the provided map.
     * 
     * @param map The map of prefix names to prefixes to use to resolve short
     *            identifiers.
     */
    public void setPrefixMap(Map<String, String> map) {
        if ( prefixManager == null ) {
            prefixManager = new DefaultPrefixManager();
        }
        prefixManager.copyPrefixesFrom(map);
    }

    /**
     * Sets the label resolver.
     * <p>
     * The label resolver will be used whenever a KGCL instruction refers to an
     * entity by its label rather than by its identifier. It is the resolver’s
     * responsibility to find the entity referenced by the label.
     * <p>
     * If not set or {@code null} (which is the case by default), the reader will
     * only be able to resolve labels (resulting in a parsing error) unless they
     * have been used in a prior {@code create...} instruction, as in:
     * 
     * <pre>
     * create class EX:0001 "my label"
     * create exact synonym "my synonym" for "my label"
     * </pre>
     * 
     * @param resolver The resolver to use.
     */
    public void setLabelResolver(ILabelResolver resolver) {
        labelResolver = resolver;
    }

    /**
     * Parses the KGCL program from the underlying source. After this method returns
     * {@code true}, call the {@link #getChangeSet()} method to get the result.
     * <p>
     * This method may only be used if an input source has been specified to the
     * constructor.
     * 
     * @return {@code true} if the program was successfully parsed, or {@code false}
     *         if KGCL syntax errors were found.
     * @throws IllegalArgumentException If the method is called while no input
     *                                  source has been set.
     */
    public boolean read() {
        if ( lexer == null ) {
            throw new IllegalArgumentException("Missing input");
        }
        return doParse(lexer, true);
    }

    /**
     * Parses the KGCL program from the specified string. After this method returns
     * {@code true}, call the {@link #getChangeSet()} method to get the result.
     * <p>
     * This method does not require that an input has been set and may be called
     * repeatedly on different inputs in the lifetime of the KGCLReader object.
     * <p>
     * When this method is called repeatedly to parse several strings, parsed
     * changes are accumulated across calls and a single subsequent call to
     * {@link #getChangeSet()} will return all the accumulated changes.
     * 
     * @param text The KGCL program to parse.
     * @return {@code true} if the program was successfully parsed, or {@code false}
     *         if KGCL syntax errors were found.
     */
    public boolean read(String text) {
        return read(text, false);
    }

    /**
     * Parses the KGCL program from the specified string. After this method returns
     * {@code true}, call the {@link #getChangeSet()} method to get the result.
     * <p>
     * If the {@code reset} parameter is set to {@code true}, then any changes that
     * have been parsed previously are cleared, so that any subsequent call to
     * {@link #getChangeSet()} will only return the change(s) parsed by the current
     * call.
     * 
     * @param text  The KGCL program to parse.
     * @param reset If {@code true}, clear any previously parsed changes and errors.
     * @return {@code true} if the program was successfully parsed, or {@code false}
     *         if KGCL syntax errors were found.
     */
    public boolean read(String text, boolean reset) {
        KGCLLexer lexer = new KGCLLexer(CharStreams.fromString(text));
        return doParse(lexer, reset);
    }

    /*
     * Helper method to do the actual parsing from the provided source.
     */
    private boolean doParse(KGCLLexer lexer, boolean reset) {
        if ( !errorListener.errors.isEmpty() && reset ) {
            errorListener.errors.clear();
        }
        if ( !changeSet.isEmpty() && reset ) {
            changeSet.clear();
        }

        int nErrors = errorListener.errors.size();

        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        KGCLParser parser = new KGCLParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);

        ParseTree tree = parser.changeset();
        if ( errorListener.errors.size() == nErrors ) {
            ParseTree2ChangeVisitor visitor = new ParseTree2ChangeVisitor(prefixManager, getLabelResolver(), changeSet);
            visitor.addErrorListener(errorListener);
            visitor.visit(tree);
        }

        hasRead = true;

        return errorListener.errors.size() == nErrors;
    }

    /**
     * Gets the KGCL changeset that has been parsed from the underlying source. This
     * method should be called after calling {@link #read()} and checking that it
     * returned {@code true}, indicating that parsing was successful.
     * <p>
     * As a convenience, this method will call {@link #read()} automatically if
     * needed, if an input has been set. The caller should then use
     * {@link #hasErrors()} to check whether syntax errors were found.
     * 
     * @return The KGCL changeset. May be an empty list if nothing has been parsed
     *         or if syntax errors were found.
     */
    public List<Change> getChangeSet() {
        if ( !hasRead && lexer != null ) {
            read();
        }
        return changeSet;
    }

    /**
     * Indicates whether parsing errors occurred. Calling this method after
     * {@link #read()} is another way of checking whether syntax errors were found
     * when parsing.
     * 
     * @return {@code true} if at least one parsing error occurred, otherwise
     *         {@code false}.
     */
    public boolean hasErrors() {
        return !errorListener.errors.isEmpty();
    }

    /**
     * Gets all syntax errors that were found when parsing, if any.
     * <p>
     * The parser does not throw any exception upon encountering a KGCL syntax error
     * (it only throws {@link java.io.IOException} upon I/O errors unrelated to
     * KGCL). Instead, all syntax errors are collected in the form of
     * {@link KGCLSyntaxError} objects, which may be retrieved with this method.
     * 
     * @return A list of objects representing the syntax errors (empty if no errors
     *         occurred).
     */
    public List<KGCLSyntaxError> getErrors() {
        return errorListener.errors;
    }

    private ILabelResolver getLabelResolver() {
        if ( labelResolver == null ) {
            labelResolver = new SimpleLabelResolver();
        }
        return labelResolver;
    }

    private class ErrorListener extends BaseErrorListener implements IParseTreeErrorListener {

        private ArrayList<KGCLSyntaxError> errors = new ArrayList<KGCLSyntaxError>();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object symbol, int line, int column,
                String msg, RecognitionException e) {
            errors.add(new KGCLSyntaxError(line, column, msg));
        }

        @Override
        public void parseTreeError(int line, int column, String message) {
            errors.add(new KGCLSyntaxError(line, column, message));
        }
    }
}
