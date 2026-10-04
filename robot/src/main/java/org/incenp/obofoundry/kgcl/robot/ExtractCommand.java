/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
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

package org.incenp.obofoundry.kgcl.robot;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.incenp.linkml.ext.ObjectLoader;
import org.incenp.obofoundry.kgcl.KGCLWriter;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.owl.DirectOWLTranslator;
import org.incenp.obofoundry.kgcl.owl.OWLTranslator;
import org.incenp.obofoundry.kgcl.owl.ProvisionalOWLTranslator;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.CommandLineHelper;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A command to extract provisional changes stored in an ontology.
 */
public class ExtractCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(StoreCommand.class);

    private Options options;

    public ExtractCommand() {
        options = CommandLineHelper.getCommonOptions();
        options.addOption("i", "input", true, "load ontology from file");
        options.addOption("o", "output", true, "save ontology to file");
        options.addOption(null, "older-than", true, "only extract changes older than the specified date");
        options.addOption("K", "kgcl-file", true, "save extracted changes to a KGCL file");
        options.addOption("Y", "yaml-file", true, "save extracted changes to a YAML file");
        options.addOption(null, "apply", false, "apply the extracted changes to the ontology");
        options.addOption("r", "reasoner", true, "reasoner to use");
    }

    @Override
    public String getName() {
        return "extract";
    }

    @Override
    public String getDescription() {
        return "extract KGCL changes provisionally stored in an ontology";
    }

    @Override
    public String getUsage() {
        return "robot extract -i <INPUT> [-K <FILE> | --apply]";
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

        if ( state == null ) {
            state = new CommandState();
        }

        IOHelper ioHelper = CommandLineHelper.getIOHelper(line);
        state = CommandLineHelper.updateInputOntology(ioHelper, state, line);
        OWLOntology ontology = state.getOntology();

        ZonedDateTime before = ZonedDateTime.now();
        if ( line.hasOption("older-than") ) {
            int olderThan = Integer.parseInt(CommandLineHelper.getOptionalValue(line, "older-than"));
            before = before.minusDays(olderThan);
        }

        ProvisionalOWLTranslator extractor = new ProvisionalOWLTranslator(ontology, null);
        List<Change> changeset = extractor.extractProvisionalChanges(true, before);

        if ( changeset.size() > 0 ) {
            logger.info(String.format("Extracted %d pending change(s)", changeset.size()));

            if ( line.hasOption("kgcl-file") ) {
                KGCLWriter writer = new KGCLWriter(new File(line.getOptionValue("kgcl-file")));
                writer.write(changeset);
                writer.close();
            }

            if ( line.hasOption("yaml-file") ) {
                ObjectLoader loader = new ObjectLoader();
                ioHelper.getPrefixManager().getPrefixName2PrefixMap().forEach(loader.getContext()::addPrefix);
                OWLDocumentFormat fmt = ontology.getOWLOntologyManager().getOntologyFormat(ontology);
                if ( fmt.isPrefixOWLOntologyFormat() ) {
                    fmt.asPrefixOWLOntologyFormat().getPrefixName2PrefixMap().forEach(loader.getContext()::addPrefix);
                }
                int i = 1;
                for ( Change change : changeset ) {
                    change.setId(String.format("CHANGE:%07d", i++));
                }
                loader.dumpObjects(new File(line.getOptionValue("yaml-file")), changeset);
            }

            if ( line.hasOption("apply") ) {
                OWLReasoner reasoner = CommandLineHelper.getReasonerFactory(line).createReasoner(ontology);
                OWLTranslator translator = new DirectOWLTranslator(ontology, reasoner);
                ArrayList<OWLOntologyChange> owlChanges = new ArrayList<>();
                for ( Change change : changeset ) {
                    owlChanges.addAll(change.accept(translator));
                }

                if ( owlChanges.size() > 0 ) {
                    ontology.getOWLOntologyManager().applyChanges(owlChanges);
                    logger.info(String.format("Applied %d change(s)", owlChanges.size()));
                }
            }
        }

        CommandLineHelper.maybeSaveOutput(line, ontology);

        return state;
    }
}
