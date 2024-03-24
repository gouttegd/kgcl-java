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

package org.incenp.obofoundry.kgcl.robot;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.incenp.obofoundry.kgcl.KGCLHelper;
import org.incenp.obofoundry.kgcl.KGCLSyntaxError;
import org.incenp.obofoundry.kgcl.KGCLWriter;
import org.incenp.obofoundry.kgcl.RejectedChange;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.NodeChange;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.CommandLineHelper;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ROBOT command to apply KGCL-described changes to an ontology.
 */
public class ApplyCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(ApplyCommand.class);

    private Options options;

    public ApplyCommand() {
        options = CommandLineHelper.getCommonOptions();
        options.addOption("i", "input", true, "load ontology from file");
        options.addOption("o", "output", true, "save ontology to file");
        options.addOption("k", "kgcl", true, "apply a single change");
        options.addOption("K", "kgcl-file", true, "apply all changes in specified file");
        options.addOption(null, "no-partial-apply", false, "apply all changes or none at all");
        options.addOption("R", "reject-file", true, "write rejected change in specified file");
        options.addOption(null, "no-reject-file", false, "do no write rejected change to a file");
        options.addOption("r", "reasoner", true, "reasoner to use");
        options.addOption("p", "provisional", false, "Apply changes in a provisional manner");
        options.addOption("P", "pending", true, "Apply pending (provisional) changes older than the specified date");
        options.addOption("l", "default-new-language", true, "Use the specified new language tag by default");
    }

    @Override
    public String getName() {
        return "apply";
    }

    @Override
    public String getDescription() {
        return "apply a KGCL changeset to an ontology";
    }

    @Override
    public String getUsage() {
        return "robot apply -i <INPUT> [-k <CHANGE> | -K <FILE> ] -o <OUTPUT>";
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

        PrefixManager prefixManager = new DefaultPrefixManager();
        prefixManager.copyPrefixesFrom(ioHelper.getPrefixManager());
        OWLDocumentFormat ontologyFormat = ontology.getOWLOntologyManager().getOntologyFormat(ontology);
        if ( ontologyFormat.isPrefixOWLOntologyFormat() ) {
            prefixManager.copyPrefixesFrom(ontologyFormat.asPrefixOWLOntologyFormat());
        }

        List<Change> changeset = new ArrayList<Change>();
        List<KGCLSyntaxError> errors = new ArrayList<KGCLSyntaxError>();
        if ( line.hasOption('k') ) {
            for ( String kgcl : line.getOptionValues('k') ) {
                changeset.addAll(KGCLHelper.parse(kgcl, prefixManager, errors));
            }
        }
        if ( line.hasOption('K') ) {
            for ( String kgclFile : line.getOptionValues('K') ) {
                File f = new File(kgclFile);
                changeset.addAll(KGCLHelper.parse(f, prefixManager, errors));
            }
        }

        if ( !errors.isEmpty() ) {
            for ( KGCLSyntaxError error : errors ) {
                logger.error(String.format("KGCL syntax error: %s", error));
            }
            throw new Exception("Invalid KGCL input, aborting");
        }

        OWLReasoner reasoner = CommandLineHelper.getReasonerFactory(line).createReasoner(ontology);

        if ( line.hasOption('P') ) {
            ZonedDateTime before = null;
            String v = line.getOptionValue('P');
            if ( !v.equalsIgnoreCase("all") ) {
                try {
                    before = LocalDate.parse(v).atStartOfDay(ZoneId.systemDefault());
                } catch ( DateTimeParseException e ) {
                    throw new Exception("Invalid date for --pending option");
                }
            }
            changeset.addAll(KGCLHelper.extractPendingChanges(ontology, before));
        }

        if ( line.hasOption("default-new-language") ) {
            for ( Change change : changeset ) {
                if ( change instanceof NodeChange ) {
                    NodeChange nc = (NodeChange) change;
                    if ( nc.getNewLanguage() == null && nc.getNewDatatype() == null ) {
                        nc.setNewLanguage(line.getOptionValue("default-new-language"));
                    }
                }
            }
        }

        if ( changeset.size() > 0 ) {
            List<RejectedChange> rejects = new ArrayList<RejectedChange>();
            KGCLHelper.apply(changeset, ontology, reasoner, line.hasOption("no-partial-apply"), rejects,
                    line.hasOption('p'));
            if ( !rejects.isEmpty() ) {
                KGCLWriter writer = getRejectedWriter(line);
                if ( writer != null ) {
                    writer.setPrefixManager(prefixManager);
                }
                for ( RejectedChange rc : rejects ) {
                    logger.error(String.format("KGCL apply error: %s", rc.getReason()));
                    if ( writer != null ) {
                        writer.write(rc.getReason());
                        writer.write(rc.getChange());
                    }
                }
                if ( writer != null ) {
                    writer.close();
                }
            }
        }

        CommandLineHelper.maybeSaveOutput(line, state.getOntology());

        return state;
    }

    private KGCLWriter getRejectedWriter(CommandLine line) throws IOException {
        if ( line.hasOption("no-reject-file") ) {
            return null;
        } else if ( line.hasOption("reject-file") ) {
            return new KGCLWriter(line.getOptionValue("reject-file"));
        } else if ( line.hasOption("kgcl-file") ) {
            return new KGCLWriter(line.getOptionValues("kgcl-file")[0] + ".rej");
        } else {
            return null;
        }
    }

}
