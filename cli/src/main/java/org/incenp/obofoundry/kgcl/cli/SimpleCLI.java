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

package org.incenp.obofoundry.kgcl.cli;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.incenp.linkml.core.LinkMLRuntimeException;
import org.incenp.linkml.ext.DataFormat;
import org.incenp.linkml.ext.ObjectLoader;
import org.incenp.obofoundry.kgcl.KGCLReader;
import org.incenp.obofoundry.kgcl.KGCLSyntaxError;
import org.incenp.obofoundry.kgcl.KGCLWriter;
import org.incenp.obofoundry.kgcl.Normalizer;
import org.incenp.obofoundry.kgcl.model.Change;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

/**
 * A command-line interface to manipulate KGCL changesets.
 */
@Command(name = "kgcl-cli",
        mixinStandardHelpOptions = true,
        versionProvider = CommandHelper.class,
        description = "Read and write KGCL changesets.",
        footer = "Report bugs to <dgouttegattat@incenp.org>.",
        optionListHeading = "%nGeneral options:%n",
        footerHeading = "%n")
public class SimpleCLI implements Runnable {

    /*
     * Command line options
     */

    @Spec
    private static CommandSpec spec;

    enum Format {
        KGCL,
        YAML,
        JSON
    }

    @ArgGroup(validate = false, heading = "%nInput options:%n")
    private InputOptions inputOpts = new InputOptions();

    private static class InputOptions {
        private List<String> files = new ArrayList<>();

        @Parameters(index = "0..*",
                paramLabel = "CHANGESET",
                description = "Load a changeset from the specified file(s). Default is to read from standard input.")
        private void addInputFile(String[] args) {
            files.add(args[args.length - 1]);
        }

        @Option(names = "--input-format", paramLabel = "FMT",
                description = "Expect input in the specified format. Allowed values: ${COMPLETION-CANDIDATES}. Default is inferred from extensions when possible.")
        Format format = null;
    }

    @ArgGroup(validate = false, heading = "%nOutput options:%n")
    private OutputOptions outputOpts = new OutputOptions();

    private static class OutputOptions {
        @Option(names = { "-o", "--output" },
                paramLabel = "FILE",
                description = "Write the changeset to FILE. Default is to write to standard output.",
                defaultValue = "-")
        String file;

        @Option(names = { "-f", "--output-format" },
                paramLabel = "FMT",
                description = "Write output in the specified format. Allowed values: ${COMPLETION-CANDIDATES}. Default is KGCL.")
        Format format = null;
    }

    @Option(names = { "-p", "--prefix" },
            paramLabel = "NAME: PREFIX",
            description = "Declares a IRI prefix")
    private void addPrefix(String[] decls) {
        String decl = decls[decls.length - 1];
        String[] parts = decl.split(":? ", 2);
        if ( parts.length == 2 ) {
            prefixMap.put(parts[0], parts[1]);
        } else {
            throw new ParameterException(spec.commandLine(), String.format("Invalid --prefix argument: %s", decl));
        }
    }

    private CommandHelper helper = new CommandHelper();
    private ObjectLoader loader;
    private Map<String, String> prefixMap = new HashMap<>();

    public static void main(String[] args) {
        System.exit(run(args));
    }

    /*
     * The real entry point. It is separate from the main method so that it can be
     * called from the test suite.
     */
    public static int run(String[] args) {
        SimpleCLI cli = new SimpleCLI();
        int rc = new picocli.CommandLine(cli).setExecutionExceptionHandler(cli.helper)
                .setCaseInsensitiveEnumValuesAllowed(true).setUsageHelpLongOptionsMaxWidth(23)
                .setUsageHelpAutoWidth(true).execute(args);
        return rc;
    }

    @Override
    public void run() {
        List<Change> changeset = loadInputs();
        normalize(changeset);
        writeOutput(changeset);
    }

    private List<Change> loadInputs() {
        List<Change> changeset = new ArrayList<>();
        if ( inputOpts.files.isEmpty() ) {
            inputOpts.files.add("-");
        }

        for ( String input : inputOpts.files ) {
            Format fmt = inputOpts.format != null ? inputOpts.format : inferFormat(input);
            try {
                InputStream stream = input.equals("-") ? System.in : new FileInputStream(input);
                switch ( fmt ) {
                case KGCL:
                    KGCLReader reader = new KGCLReader(stream);
                    reader.setPrefixMap(prefixMap);
                    if ( reader.read() ) {
                        changeset.addAll(reader.getChangeSet());
                    } else {
                        for ( KGCLSyntaxError error : reader.getErrors() ) {
                            helper.warn("KGCL error:%s:%d,%d:%s", input, error.getLine(), error.getPosition(),
                                    error.getMessage());
                        }
                        helper.error("Cannot parse file %s", input);
                    }
                    break;

                case YAML:
                    changeset.addAll(getLoader().loadObjects(stream, Change.class));
                    break;

                case JSON:
                    changeset.addAll(getLoader().loadObjects(stream, Change.class, DataFormat.JSON));
                    break;
                }
            } catch ( IOException | LinkMLRuntimeException e ) {
                helper.error("Cannot read file %s: %s", input, e.getMessage());
            }
        }

        return changeset;
    }

    private void normalize(List<Change> changeset) {
        int i = 1;
        Normalizer normalizer = new Normalizer();
        for ( Change change : changeset ) {
            if ( change.getId() == null ) {
                // Changes obtained from KGCL do not have an ID
                change.setId(String.format("CHANGE:%07d", i++));
            }
            change.accept(normalizer);
        }
    }

    private void writeOutput(List<Change> changeset) {
        Format fmt = outputOpts.format != null ? outputOpts.format : inferFormat(outputOpts.file);
        try {
            OutputStream stream = outputOpts.file.equals("-") ? System.out : new FileOutputStream(outputOpts.file);
            switch (fmt) {
            case KGCL:
                KGCLWriter writer = new KGCLWriter(stream);
                writer.setPrefixMap(prefixMap);
                writer.write(changeset);
                writer.close();
                break;

            case YAML:
                getLoader().dumpObjects(stream, changeset);
                break;

            case JSON:
                getLoader().dumpObjects(stream, changeset, DataFormat.JSON);
                break;
            }
        } catch ( IOException | LinkMLRuntimeException e ) {
            helper.error("Cannot write: %s\n", e.getMessage());
        }
    }

    private Format inferFormat(String filename) {
        if ( filename.endsWith(".kgcl") ) {
            return Format.KGCL;
        } else if ( filename.endsWith(".yaml") ) {
            return Format.YAML;
        } else if ( filename.endsWith(".json") ) {
            return Format.JSON;
        }
        return Format.KGCL;
    }

    private ObjectLoader getLoader() {
        if ( loader == null ) {
            loader = new ObjectLoader();
            PrefixManager pm = new DefaultPrefixManager();
            pm.getPrefixName2PrefixMap().forEach(loader.getContext()::addPrefix);
            prefixMap.forEach(loader.getContext()::addPrefix);
        }
        return loader;
    }
}
