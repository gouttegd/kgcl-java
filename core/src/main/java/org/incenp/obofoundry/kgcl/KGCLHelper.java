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
import java.time.ZonedDateTime;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

/**
 * A class providing static helper methods to work with KGCL.
 */
public class KGCLHelper {

    /**
     * Parses KGCL from a string.
     * 
     * @param kgcl     The KGCL instructions to parse.
     * @param ontology An ontology that may provide a prefix manager to allow the
     *                 parser to expand CURIEs into IRIs. May be null.
     * @return A KGCL changeset.
     * @throws IOException If any non-KGCL I/O error occurs.
     */
    public static List<Change> parse(String kgcl, OWLOntology ontology) throws IOException {
        return doParse(new KGCLReader(new StringReader(kgcl)), ontology, null);
    }

    /**
     * Parses KGCL from a string and collect syntax errors.
     * 
     * @param kgcl     The KGCL instructions to parse.
     * @param ontology An ontology that may provide a prefix manager to allow the
     *                 parser to expand CURIEs into IRIs. May be null.
     * @param errors   A list that will collect any syntax error encountered when
     *                 parsing. If {@code null}, errors will be ignored.
     * @return A KGCL changeset.
     * @throws IOException If any non-KGCL I/O error occurs.
     */
    public static List<Change> parse(String kgcl, OWLOntology ontology, List<KGCLSyntaxError> errors)
            throws IOException {
        return doParse(new KGCLReader(new StringReader(kgcl)), ontology, errors);
    }

    /**
     * Parses KGCL from a file.
     * 
     * @param kgcl     The file to parse.
     * @param ontology An ontology that may provide a prefix manager to allow the
     *                 parser to expand CURIEs into IRIs. May be null.
     * @return A KGCL changeset.
     * @throws IOException If any non-KGCL I/O error occurs.
     */
    public static List<Change> parse(File kgcl, OWLOntology ontology) throws IOException {
        return doParse(new KGCLReader(kgcl), ontology, null);
    }

    /**
     * Parses KGCL from a file and collect syntax errors.
     * 
     * @param kgcl     The file to parse.
     * @param ontology An ontology that may provide a prefix manager to allow the
     *                 parser to expand CURIEs into IRIs. May be null.
     * @param errors   A list that will collect any syntax error encountered when
     *                 parsing. If {@code null}, errors will be ignored.
     * @return A KGCL changeset.
     * @throws IOException If any non-KGCL I/O error occurs.
     */
    public static List<Change> parse(File kgcl, OWLOntology ontology, List<KGCLSyntaxError> errors) throws IOException {
        return doParse(new KGCLReader(kgcl), ontology, errors);
    }

    private static List<Change> doParse(KGCLReader reader, OWLOntology ontology, List<KGCLSyntaxError> errors)
            throws IOException {
        reader.setPrefixManager(ontology);

        if ( !reader.read() ) {
            if ( errors != null ) {
                errors.addAll(reader.getErrors());
            }
        }

        return reader.getChangeSet();
    }

    /**
     * Gets the "pending" (provisional) changes that are stored as KGCL annotations
     * in the ontology,
     * <p>
     * Note that the annotations are removed during the process.
     * 
     * @param ontology The ontology to extract pending changes from.
     * @param before   If not {@code null}, only changes older than the specified
     *                 date are extracted.
     * @return The list of pending changes.
     */
    public static List<Change> extractPendingChanges(OWLOntology ontology, ZonedDateTime before) {
        ProvisionalOWLTranslator extractor = new ProvisionalOWLTranslator(ontology, null);
        return extractor.extractProvisionalChanges(true, before);
    }

    /**
     * Applies a KGCL changeset to an ontology.
     * 
     * @param changeset      The changeset to apply.
     * @param ontology       The ontology to apply it to.
     * @param reasoner       The reasoner to use.
     * @param noPartialApply If {@code true}, changes will only be applied if they
     *                       all can be applied.
     */
    public static void apply(List<Change> changeset, OWLOntology ontology, OWLReasoner reasoner,
            boolean noPartialApply) {
        apply(changeset, ontology, reasoner, noPartialApply, null, false);
    }

    /**
     * Applies a KGCL changeset to an ontology.
     * 
     * @param changeset      The changeset to apply.
     * @param ontology       The ontology to apply it to.
     * @param reasoner       The reasoner to use.
     * @param noPartialApply If {@code true}, changes will only be applied if they
     *                       can all be applied.
     * @param rejects        A list that will collect the changes that cannot be
     *                       applied. May be {@code null}.
     */
    public static void apply(List<Change> changeset, OWLOntology ontology, OWLReasoner reasoner, boolean noPartialApply,
            List<RejectedChange> rejects) {
        apply(changeset, ontology, reasoner, noPartialApply, rejects, false);
    }

    /**
     * Applies a KGCL changeset to an ontology.
     * 
     * @param changeset      The changeset to apply.
     * @param ontology       The ontology to apply it to.
     * @param reasoner       The reasoner to use,
     * @param noPartialApply If {@code true}, changes will only be applied if they
     *                       can all be applied.
     * @param rejects        A list that will collect the changes that cannot be
     *                       applied. May be {@code null}.
     * @param provisional    If {@code true}, changes will be recorded in the
     *                       ontology for later application, rather than applied
     *                       directly.
     */
    public static void apply(List<Change> changeset, OWLOntology ontology, OWLReasoner reasoner, boolean noPartialApply,
            List<RejectedChange> rejects, boolean provisional) {
        OntologyPatcher patcher = new OntologyPatcher(ontology, reasoner);
        patcher.setProvisional(provisional);
        if ( !patcher.apply(changeset, noPartialApply) && rejects != null ) {
            rejects.addAll(patcher.getRejectedChanges());
        }
    }
}
