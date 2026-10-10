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

import java.io.PrintStream;

import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.ParseResult;

/**
 * A helper class for command line tools.
 */
public class CommandHelper implements IVersionProvider, IExecutionExceptionHandler {

    private String name = "kgcl-cli";

    @Override
    public int handleExecutionException(Exception ex, CommandLine commandLine, ParseResult fullParseResult)
            throws Exception {
        warn(ex.getMessage());
        return commandLine.getCommandSpec().exitCodeOnExecutionException();
    }

    @Override
    public String[] getVersion() throws Exception {
        return new String[] {
                "kgcl-cli (KGCL-Java " + CommandHelper.class.getPackage().getImplementationVersion() + ")",
                "Copyright ⓒ 2023–2026 Damien Goutte-Gattat", "",
                "This program is released under the terms of the 3-clause",
                "BSD license. See the COPYING file in the source distribution." };
    }

    /**
     * Prints an informative message on standard output.
     * 
     * @param format The message to print, as a format string.
     * @param args   Arguments for the format specifiers inside the message.
     */
    public void info(String format, Object... args) {
        print(System.out, format, args);
    }

    /**
     * Prints a warning message on standard output.
     * 
     * @param format The message to print, as a format string.
     * @param args   Arguments for the format specifiers inside the message.
     */
    public void warn(String format, Object... args) {
        print(System.err, format, args);
    }

    /**
     * Prints an error message on standard output.
     * 
     * @param format The message to print, as a format string.
     * @param args   Arguments for the format specifiers inside the message.
     */
    public void error(String format, Object... args) {
        // Throw an exception to interrupt the application; the exception will be caught
        // by PicoCLI and the error message will be displayed by the exception handler.
        throw new RuntimeException(String.format(format, args));
    }

    private void print(PrintStream stream, String format, Object... args) {
        stream.printf(name + ": ");
        stream.printf(format, args);
        stream.print('\n');
    }
}
