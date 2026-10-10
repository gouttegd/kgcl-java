/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023 Damien Goutte-Gattat
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   (1) Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 *   (2) Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 *   (3) Neither the name of the copyright holder nor the names its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
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
import java.util.Map;

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
     * Sets the prefix manager from the specified ontology. This is a convenience
     * method that automatically gets the prefix manager from a OWL API
     * {@code OWLOntology} object and sets it as the prefix manager for the writer.
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
     * Sets the prefix map to use to compact identifiers.
     * <p>
     * This is equivalent to calling {@link #setPrefixManager(PrefixManager)} with a
     * PrefixManager object initialised with the provided map.
     * 
     * @param map The map of prefix names to prefixes to use to compact identifiers.
     */
    public void setPrefixMap(Map<String, String> map) {
        if ( prefixManager == null ) {
            prefixManager = new DefaultPrefixManager();
        }
        prefixManager.copyPrefixesFrom(map);
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
