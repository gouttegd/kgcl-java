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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

/**
 * A writer to serialise KGCL change objects into a KGCL program that is written
 * to a file or file-like sink.
 */
public class KGCLWriter {
    private BufferedWriter output;
    private PrefixManager prefixManager;
    private KGCLTextTranslator visitor;

    /**
     * Creates a new instance to write to a stream.
     * 
     * @param kgclOutput The stream to write to.
     */
    public KGCLWriter(OutputStream kgclOutput) {
        output = new BufferedWriter(new OutputStreamWriter(kgclOutput));
    }

    /**
     * Creates a new instance to write to a character stream writer.
     * 
     * @param kgclOutput The character stream to write to.
     */
    public KGCLWriter(Writer kgclOutput) {
        output = new BufferedWriter(kgclOutput);
    }

    /**
     * Creates a new instance to write to a file.
     * 
     * @param kgclFile The file to write to.
     * @throws IOException If the file cannot be found or written to.
     */
    public KGCLWriter(File kgclFile) throws IOException {
        output = new BufferedWriter(new FileWriter(kgclFile));
    }

    /**
     * Creates a new instance to write to a file.
     * 
     * @param kgclFilename The name of the file to write to.
     * @throws IOException If the file cannot be found or written to.
     */
    public KGCLWriter(String kgclFilename) throws IOException {
        this(new File(kgclFilename));
    }

    /**
     * Sets the prefix manager to use to compact identifiers. The prefix manager in
     * this class will perform the opposite task to the prefix manager in
     * {@link KGCLReader#setPrefixManager(PrefixManager)}. Given a full-length
     * identifier, it will convert it into a short-form (“CURIEfied”) identifier.
     * <p>
     * The prefix manager should be set prior to any call to the {@link #write}
     * methods.
     * <p>
     * If no prefix manager is set, no default compaction is performed and all
     * identifiers will be written as they are.
     * 
     * @param manager The OWL API prefix manager to use (may be {@code null}).
     */
    public void setPrefixManager(PrefixManager manager) {
        prefixManager = manager;
    }

    /**
     * Sets the pefix manager from the specified ontology. This is a convenience
     * method that automatically gets the prefix manager from a OWL API
     * {@code OWLOntology} object and sets it as the prefix manager for the writer.
     * 
     * @param ontology The ontology whose prefix manager shall be used. If the
     *                 ontology has been read from a {@code OWLDocumentFormat} that
     *                 does not support prefixes, it is ignored and a default prefix
     *                 manager is used instead.
     */
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
     * Serialises and writes a KGCL changeset to the underlying sink.
     * 
     * @param changes The list of KGCL changes to serialise.
     * @throws IOException If any I/O error occurs when writing.
     */
    public void write(List<Change> changes) throws IOException {
        KGCLTextTranslator visitor = getVisitor();
        for ( Change change : changes ) {
            String kgcl = change.accept(visitor);
            if ( kgcl != null ) {
                output.write(kgcl);
                output.newLine();
            }
        }
    }

    /**
     * Serialise and writes a single KGCL change to the underlying sink.
     * 
     * @param change The KGCL change to serialise.
     * @throws IOException If any I/O error occurs when writing.
     */
    public void write(Change change) throws IOException {
        String kgcl = change.accept(getVisitor());
        if ( kgcl != null ) {
            output.write(kgcl);
            output.newLine();
        }
    }

    /**
     * Writes a comment line to the underlying sink. This method writes its argument
     * preceded by a hash character ({@code #}), so that it would be ignored if the
     * file is later read by a {@link KGCLReader} object.
     * <p>
     * Note that the KGCL specification says nothing about comments in a KGCL file.
     * A file containing such comments may not be successfully parsed by other KGCL
     * implementations.
     * 
     * @param comment The comment to write.
     * @throws IOException If any I/O error occurs when writing.
     */
    public void write(String comment) throws IOException {
        output.write("# ");
        output.write(comment);
        output.newLine();
    }

    /**
     * Closes the underlying writer.
     * 
     * @throws IOException If any I/O error occurs.
     */
    public void close() throws IOException {
        output.close();
    }

    private KGCLTextTranslator getVisitor() {
        if ( visitor == null ) {
            visitor = new KGCLTextTranslator(prefixManager);
        }
        return visitor;
    }
}
