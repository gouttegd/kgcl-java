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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.incenp.obofoundry.kgcl.KGCLHelper;
import org.incenp.obofoundry.kgcl.KGCLSyntaxError;
import org.incenp.obofoundry.kgcl.KGCLWriter;
import org.incenp.obofoundry.kgcl.RejectedChange;
import org.incenp.obofoundry.kgcl.model.Change;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.CommandLineHelper;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
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

        List<Change> changeset = new ArrayList<Change>();
        List<KGCLSyntaxError> errors = new ArrayList<KGCLSyntaxError>();
        if ( line.hasOption('k') ) {
            for ( String kgcl : line.getOptionValues('k') ) {
                changeset.addAll(KGCLHelper.parse(kgcl, state.getOntology(), errors));
            }
        }
        if ( line.hasOption('K') ) {
            for ( String kgclFile : line.getOptionValues('K') ) {
                File f = new File(kgclFile);
                changeset.addAll(KGCLHelper.parse(f, state.getOntology(), errors));
            }
        }

        if ( !errors.isEmpty() ) {
            for ( KGCLSyntaxError error : errors ) {
                logger.error(String.format("KGCL syntax error: %s", error));
            }
            throw new Exception("Invalid KGCL input, aborting");
        }

        if ( changeset != null && changeset.size() > 0 ) {
            List<RejectedChange> rejects = new ArrayList<RejectedChange>();
            KGCLHelper.apply(changeset, state.getOntology(), line.hasOption("no-partial-apply"), rejects);
            KGCLWriter writer = null;
            if ( line.hasOption('K') ) {
                // Write rejected changes to a file, if a file was originally provided
                writer = new KGCLWriter(line.getOptionValue('K') + ".rej");
                writer.setPrefixManager(state.getOntology());
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

        CommandLineHelper.maybeSaveOutput(line, state.getOntology());

        return state;
    }

}
