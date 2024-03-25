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
import java.util.UUID;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.incenp.obofoundry.kgcl.AutoIDAllocator;
import org.incenp.obofoundry.kgcl.IAutoIDGenerator;
import org.incenp.obofoundry.kgcl.KGCLHelper;
import org.incenp.obofoundry.kgcl.KGCLSyntaxError;
import org.incenp.obofoundry.kgcl.KGCLWriter;
import org.incenp.obofoundry.kgcl.RandomizedIDGenerator;
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

        // Auto-ID options
        options.addOption(null, "auto-id-min", true, "Lower range value for automatically assigned IDs");
        options.addOption(null, "auto-id-max", true, "Upper range value for automatically assigned IDs");
        options.addOption(null, "auto-id-width", true, "Width of automatically assigned IDs");
        options.addOption(null, "auto-id-prefix", true, "Prefix for automatically assigned IDs");
        options.addOption(null, "auto-id-temp-prefix", true, "Generate random temporary IDs in the specified prefix");
        options.addOption(null, "auto-id-range-file", true, "Assign IDs from the specified ID range file");
        options.addOption(null, "auto-id-range-name", true, "Use the specified ID range name");
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
        prefixManager.setPrefix("AUTOID:", AutoIDAllocator.AUTOID_BASE_IRI);
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
            AutoIDAllocator idAllocator = new AutoIDAllocator(getAutoIDGenerator(line, ontology));
            if ( !idAllocator.reallocate(changeset) ) {
                throw new Exception("Cannot generate automatic IDs");
            }

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

    private IAutoIDGenerator getAutoIDGenerator(CommandLine line, OWLOntology ontology) throws Exception {
        IAutoIDGenerator generator = null;

        if ( line.hasOption("auto-id-prefix") ) {
            /*
             * Manual mode; generate IDs in a range that is explicitly specified on the
             * command line.
             */
            if ( !line.hasOption("auto-id-min") ) {
                throw new Exception("Missing --auto-id-min option for auto-assigned IDs");
            }
            int lower = Integer.parseInt(line.getOptionValue("auto-id-min"));
            int upper = line.hasOption("auto-id-max") ? Integer.parseInt(line.getOptionValue("auto-id-max"))
                    : lower + 1000;
            int width = line.hasOption("auto-id-width") ? Integer.parseInt(line.getOptionValue("auto-id-width")) : 7;
            String format = String.format("%s%%0%dd", line.getOptionValue("auto-id-prefix"), width);
            generator = new RandomizedIDGenerator(ontology, format, lower, upper);
        } else if ( line.hasOption("auto-id-temp-prefix") ) {
            /*
             * Temporary ID mode; generate temporary IDs that should later be replaced by
             * permanent IDs.
             */
            String prefix = line.getOptionValue("auto-id-temp-prefix");
            generator = () -> prefix + UUID.randomUUID().toString();
        } else {
            /*
             * Range-file mode; similar to manual mode, but the range is obtained from a
             * file of ID ranges.
             */
            String rangeFile = line.getOptionValue("auto-id-range-file");
            String requestedName = line.getOptionValue("auto-id-range-name");
            String[] defaultNames = new String[] { "kgcl", "KGCL", "ontobot", "Ontobot" };

            generator = IDRangeHelper.maybeGetIDGenerator(ontology, rangeFile, requestedName, defaultNames, true);
        }

        return generator;
    }
}
