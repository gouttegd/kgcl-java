/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2026 Damien Goutte-Gattat
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.incenp.linkml.ext.ObjectLoader;
import org.incenp.obofoundry.kgcl.ILabelResolver;
import org.incenp.obofoundry.kgcl.KGCLHelper;
import org.incenp.obofoundry.kgcl.KGCLSyntaxError;
import org.incenp.obofoundry.kgcl.Normalizer;
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
                changeset.addAll(Normalizer.normalize(loader.loadObjects(new File(filename), Change.class)));
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
