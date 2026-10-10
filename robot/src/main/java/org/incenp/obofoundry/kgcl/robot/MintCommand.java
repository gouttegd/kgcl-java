/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2024 Damien Goutte-Gattat
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

package org.incenp.obofoundry.kgcl.robot;

import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.compress.utils.Sets;
import org.incenp.obofoundry.dicer.IAutoIDGenerator;
import org.incenp.obofoundry.dicer.IDPolicyHelper;
import org.incenp.obofoundry.dicer.IDRange;
import org.incenp.obofoundry.dicer.OWLExistenceChecker;
import org.incenp.obofoundry.dicer.SequentialIDGenerator;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.CommandLineHelper;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

public class MintCommand implements Command {

    private static final String DEFAULT_TEMP_PREFIX = "http://purl.obolibrary.org/temp#";
    private static final String REPLACED_BY = "http://purl.obolibrary.org/obo/IAO_0100001";

    private Options options;

    public MintCommand() {
        options = CommandLineHelper.getCommonOptions();
        options.addOption("i", "input", true, "load ontology from file");
        options.addOption("o", "output", true, "save ontology to file");

        options.addOption(null, "temp-id-prefix", true, "prefix of temporary identifiers to replace");

        options.addOption(null, "keep-deprecated", false, "keep temporary terms as deprecated entities");
        options.addOption(null, "minted-from-property", true,
                "property used to link minted identifiers to temporary identifiers");

        options.addOption(null, "minted-id-prefix", true, "prefix of newly minted identifiers");
        options.addOption(null, "pad-width", true, "pad the numerical portion of minted identifiers up to this width");
        options.addOption(null, "min-id", true, "lower bound of the range for newly minted identifiers");
        options.addOption(null, "max-id", true, "upper bound of the range for newly minted identifiers");

        options.addOption(null, "id-range-file", true, "use the specified ID range file");
        options.addOption(null, "id-range-name", true, "use the specified ID range name");
    }

    @Override
    public String getName() {
        return "mint";
    }

    @Override
    public String getDescription() {
        return "replace temporary identifiers by newly minted permanent identifiers";
    }

    @Override
    public String getUsage() {
        return "robot mint -i <file> [--minted-id-prefix <prefix> --min-id <integer>|"
                + "--id-range-file <file> --id-range-name <name>] -o <file>";
    }

    @Override
    public Options getOptions() {
        return options;
    }

    @Override
    public void main(String[] args) {
        try {
            execute(null, args);
        } catch ( Exception e ) {
            CommandLineHelper.handleException(e);
        }
    }

    @Override
    public CommandState execute(CommandState state, String[] args) throws Exception {
        CommandLine line = CommandLineHelper.getCommandLine(getUsage(), options, args);
        if ( line == null ) {
            return null;
        }

        IOHelper ioHelper = CommandLineHelper.getIOHelper(line);
        state = CommandLineHelper.updateInputOntology(ioHelper, state, line);

        OWLOntology ontology = state.getOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLEntityRenamer renamer = new OWLEntityRenamer(ontology.getOWLOntologyManager(), Sets.newHashSet(ontology));

        // Annotation properties we need for --minted-from and --keep-deprecated
        OWLAnnotationProperty labelProp = factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI());
        OWLAnnotationProperty replacedByProp = factory.getOWLAnnotationProperty(IRI.create(REPLACED_BY));
        OWLAnnotationProperty deprecatedProp = factory
                .getOWLAnnotationProperty(OWLRDFVocabulary.OWL_DEPRECATED.getIRI());
        OWLAnnotationProperty mintedFromProp = null;
        if ( line.hasOption("minted-from-property") ) {
            mintedFromProp = factory.getOWLAnnotationProperty(IRI.create(line.getOptionValue("minted-from-property")));
        }

        String tempPrefix = line.getOptionValue("temp-id-prefix", DEFAULT_TEMP_PREFIX);
        IAutoIDGenerator generator = getAutoIDGenerator(line, ontology);
        if ( generator == null ) {
            throw new Exception("No ID generator set, cannot mint new IDs");
        }
        boolean keepDeprecated = line.hasOption("keep-deprecated");

        // Collect IDs that needs to be replaced.
        HashMap<String, String> ids = new HashMap<String, String>();
        HashMap<String, OWLLiteral> savedLabels = new HashMap<String, OWLLiteral>();
        HashSet<OWLAxiom> savedAxioms = new HashSet<OWLAxiom>();
        for ( OWLEntity entity : ontology.getSignature() ) {
            String origIRI = entity.getIRI().toString();
            if ( origIRI.startsWith(tempPrefix) ) {
                ids.put(origIRI, generator.nextID());

                if ( keepDeprecated ) {
                    // Keep aside the original declaration axioms and labels
                    savedAxioms.addAll(ontology.getDeclarationAxioms(entity));
                    for ( OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(entity.getIRI()) ) {
                        if ( ax.getProperty().isLabel() && ax.getValue().isLiteral() ) {
                            savedLabels.put(origIRI, ax.getValue().asLiteral().get());
                        }
                    }
                }
            }
        }

        // Proceed with replacement for the IDs we found
        for ( String oldID : ids.keySet() ) {
            IRI oldIRI = IRI.create(oldID);
            IRI newIRI = IRI.create(ids.get(oldID));

            // Actual renaming
            manager.applyChanges(renamer.changeIRI(oldIRI, newIRI));

            // Add a "minted-from" annotation?
            if ( mintedFromProp != null ) {
                manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(mintedFromProp, newIRI, oldIRI));
            }

            // Keep the original entities as deprecated entities?
            if ( keepDeprecated ) {
                manager.addAxiom(ontology,
                        factory.getOWLAnnotationAssertionAxiom(deprecatedProp, oldIRI, factory.getOWLLiteral(true)));
                manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(replacedByProp, oldIRI, newIRI));
                OWLLiteral label = savedLabels.get(oldID);
                if ( label != null ) {
                    // Prepend "obsolete " if the label is English or language-neutral
                    if ( label.getLang().isEmpty() || label.getLang().equalsIgnoreCase("en") ) {
                        label = factory.getOWLLiteral("obsolete " + label.getLiteral(), label.getLang());
                    }
                    manager.addAxiom(ontology, factory.getOWLAnnotationAssertionAxiom(labelProp, oldIRI, label));
                }
            }
        }

        // --keep-deprecated: restore the declaration axioms for the deprecated entities
        if ( !savedAxioms.isEmpty() ) {
            manager.addAxioms(ontology, savedAxioms);
        }

        CommandLineHelper.maybeSaveOutput(line, ontology);

        return state;
    }

    private IAutoIDGenerator getAutoIDGenerator(CommandLine line, OWLOntology ontology)
            throws Exception {
        if ( line.hasOption("minted-id-prefix") ) {
            /*
             * Manual mode; generate IDs in a range that is explicitly specified on the
             * command line.
             */
            if ( !line.hasOption("min-id") ) {
                throw new Exception("Missing --min-id option for auto-assigned IDs");
            }
            int lower = Integer.parseInt(line.getOptionValue("min-id"));
            int upper = line.hasOption("max-id") ? Integer.parseInt(line.getOptionValue("max-id")) : lower + 1000;
            int width = line.hasOption("pad-width") ? Integer.parseInt(line.getOptionValue("pad-width")) : 7;
            String format = String.format("%s%%0%dd", line.getOptionValue("minted-id-prefix"), width);
            return new SequentialIDGenerator(format, lower, upper, new OWLExistenceChecker(ontology));
        } else {
            /*
             * Range-file mode; similar, but the range is obtained from a file of ID ranges.
             */
            String rangeFile = line.getOptionValue("id-range-file");
            String requestedName = line.getOptionValue("id-range-name");
            String[] defaultNames = new String[] { "auto-minter" };

            IDRange range = IDPolicyHelper.getRange(requestedName, defaultNames, rangeFile);
            return new SequentialIDGenerator(range, new OWLExistenceChecker(ontology));
        }
    }

}
