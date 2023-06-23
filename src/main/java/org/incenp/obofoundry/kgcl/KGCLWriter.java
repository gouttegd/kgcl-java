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
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * A writer to serialise KGCL change objects into a KGCL program.
 */
public class KGCLWriter {
    private BufferedWriter output;
    private PrefixManager prefixManager;

    /**
     * Create a new instance to write to a stream.
     * 
     * @param kgclOutput The stream to write to.
     */
    public KGCLWriter(OutputStream kgclOutput) {
        output = new BufferedWriter(new OutputStreamWriter(kgclOutput));
    }

    /**
     * Create a new instance to write to a file.
     * 
     * @param kgclFile The file to write to.
     * @throws IOException If the file cannot be found or written to.
     */
    public KGCLWriter(File kgclFile) throws IOException {
        output = new BufferedWriter(new FileWriter(kgclFile));
    }

    /**
     * Create a new instance to write to a file.
     * 
     * @param kgclFilename The name of the file to write to.
     * @throws IOException If the file cannot be found or written to.
     */
    public KGCLWriter(String kgclFilename) throws IOException {
        this(new File(kgclFilename));
    }

    /**
     * Set the prefix manager to use to compact identifiers.
     * 
     * @param manager The prefix manager (may be {@code null}).
     */
    public void setPrefixManager(PrefixManager manager) {
        prefixManager = manager;
    }

    /**
     * Use the profix manager from the specified ontology.
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
     * Write the KGCL changes to the underlying writer.
     * 
     * @param changes The list of KGCL changes to serialise.
     * @throws IOException If any I/O error occurs when writing.
     */
    public void write(List<Change> changes) throws IOException {
        Change2TextVisitor visitor = new Change2TextVisitor(prefixManager);
        for ( Change change : changes ) {
            String kgcl = change.accept(visitor);
            if ( kgcl != null ) {
                output.write(kgcl);
                output.newLine();
            }
        }
    }

    /**
     * Close the underlying writer.
     * 
     * @throws IOException If any I/O error occurs.
     */
    public void close() throws IOException {
        output.close();
    }
}
