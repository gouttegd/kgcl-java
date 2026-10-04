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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.incenp.linkml.ext.ObjectLoader;
import org.incenp.obofoundry.kgcl.ILabelResolver;
import org.incenp.obofoundry.kgcl.KGCLHelper;
import org.incenp.obofoundry.kgcl.KGCLSyntaxError;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.owl.OWLTranslator;
import org.incenp.obofoundry.kgcl.owl.OntologyBasedLabelResolver;
import org.incenp.obofoundry.kgcl.owl.ProvisionalOWLTranslator;
import org.obolibrary.robot.Command;
import org.obolibrary.robot.CommandLineHelper;
import org.obolibrary.robot.CommandState;
import org.obolibrary.robot.IOHelper;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ROBOT command to store KGCL-described changes in an ontology, rather than
 * apply them.
 */
public class StoreCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(StoreCommand.class);

    private Options options;

    public StoreCommand() {
        options = CommandLineHelper.getCommonOptions();
        options.addOption("i", "input", true, "load ontology from file");
        options.addOption("o", "output", true, "save ontology to file");
        options.addOption("k", "kgcl", true, "apply a single change");
        options.addOption("K", "kgcl-file", true, "apply all changes in specified file");
        options.addOption("Y", "kgcl-yaml", true, "apply all changes in the specified YAML file");
        options.addOption("r", "reasoner", true, "reasoner to use");
    }

    @Override
    public String getName() {
        return "store";
    }

    @Override
    public String getDescription() {
        return "store a KGCL changeset as pending changes into an ontology";
    }

    @Override
    public String getUsage() {
        return "robot store -i <INPUT> [-k <CHANGE> | -K <FILE>] -o <OUTPUT>";
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
        ILabelResolver labelResolver = new OntologyBasedLabelResolver(ontology);

        List<Change> changeset = new ArrayList<>();
        List<KGCLSyntaxError> errors = new ArrayList<>();
        ObjectLoader loader = null;
        if ( line.hasOption("kgcl") ) {
            for ( String kgcl : line.getOptionValues("kgcl") ) {
                changeset.addAll(KGCLHelper.parse(kgcl, prefixManager, errors, labelResolver));
            }
        }
        if ( line.hasOption("kgcl-file") ) {
            for ( String filename : line.getOptionValues("kgcl-file") ) {
                File file = new File(filename);
                changeset.addAll(KGCLHelper.parse(file, prefixManager, errors, labelResolver));
            }
        }
        if ( line.hasOption("kgcl-yaml") ) {
            if ( loader == null ) {
                loader = new ObjectLoader();
                prefixManager.getPrefixName2PrefixMap().forEach(loader.getContext()::addPrefix);
            }
            for ( String filename : line.getOptionValues("kgcl-yaml") ) {
                changeset.addAll(loader.loadObjects(new File(filename), Change.class));
            }
        }

        if ( !errors.isEmpty() ) {
            for ( KGCLSyntaxError error : errors ) {
                logger.error(String.format("KGCL syntax error: %s", error));
            }
            throw new Exception("Invalid KGCL input, aborting");
        }

        if ( changeset.size() > 0 ) {
            OWLReasoner reasoner = CommandLineHelper.getReasonerFactory(line).createReasoner(ontology);
            OWLTranslator translator = new ProvisionalOWLTranslator(ontology, reasoner);
            ArrayList<OWLOntologyChange> owlChanges = new ArrayList<>();
            for ( Change change : changeset ) {
                owlChanges.addAll(change.accept(translator));
            }

            if ( owlChanges.size() > 0 ) {
                ontology.getOWLOntologyManager().applyChanges(owlChanges);
                logger.info(String.format("Applied %d change(s)", owlChanges.size()));
            }
        }

        CommandLineHelper.maybeSaveOutput(line, ontology);

        return state;
    }
}
