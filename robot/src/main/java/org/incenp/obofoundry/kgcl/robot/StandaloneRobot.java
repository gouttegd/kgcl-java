/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023 Damien Goutte-Gattat
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

import org.obolibrary.robot.AnnotateCommand;
import org.obolibrary.robot.CollapseCommand;
import org.obolibrary.robot.CommandManager;
import org.obolibrary.robot.ConvertCommand;
import org.obolibrary.robot.DiffCommand;
import org.obolibrary.robot.ExpandCommand;
import org.obolibrary.robot.ExplainCommand;
import org.obolibrary.robot.ExportCommand;
import org.obolibrary.robot.ExportPrefixesCommand;
import org.obolibrary.robot.ExtractCommand;
import org.obolibrary.robot.FilterCommand;
import org.obolibrary.robot.MaterializeCommand;
import org.obolibrary.robot.MeasureCommand;
import org.obolibrary.robot.MergeCommand;
import org.obolibrary.robot.MirrorCommand;
import org.obolibrary.robot.PluginManager;
import org.obolibrary.robot.PythonCommand;
import org.obolibrary.robot.QueryCommand;
import org.obolibrary.robot.ReasonCommand;
import org.obolibrary.robot.ReduceCommand;
import org.obolibrary.robot.RelaxCommand;
import org.obolibrary.robot.RemoveCommand;
import org.obolibrary.robot.RenameCommand;
import org.obolibrary.robot.RepairCommand;
import org.obolibrary.robot.ReportCommand;
import org.obolibrary.robot.TemplateCommand;
import org.obolibrary.robot.UnmergeCommand;
import org.obolibrary.robot.ValidateProfileCommand;
import org.obolibrary.robot.VerifyCommand;

/**
 * This class provides a version of the ROBOT tool that includes the KGCL
 * 'apply' command.
 */
public class StandaloneRobot {

    public static void main(String[] args) {
        CommandManager m = new CommandManager();
        m.addCommand("annotate", new AnnotateCommand());
        m.addCommand("collapse", new CollapseCommand());
        m.addCommand("convert", new ConvertCommand());
        m.addCommand("diff", new DiffCommand());
        m.addCommand("expand", new ExpandCommand());
        m.addCommand("explain", new ExplainCommand());
        m.addCommand("export", new ExportCommand());
        m.addCommand("export-prefixes", new ExportPrefixesCommand());
        m.addCommand("extract", new ExtractCommand());
        m.addCommand("filter", new FilterCommand());
        m.addCommand("materialize", new MaterializeCommand());
        m.addCommand("measure", new MeasureCommand());
        m.addCommand("merge", new MergeCommand());
        m.addCommand("mirror", new MirrorCommand());
        m.addCommand("python", new PythonCommand());
        m.addCommand("query", new QueryCommand());
        m.addCommand("reason", new ReasonCommand());
        m.addCommand("reduce", new ReduceCommand());
        m.addCommand("relax", new RelaxCommand());
        m.addCommand("remove", new RemoveCommand());
        m.addCommand("rename", new RenameCommand());
        m.addCommand("repair", new RepairCommand());
        m.addCommand("report", new ReportCommand());
        m.addCommand("template", new TemplateCommand());
        m.addCommand("unmerge", new UnmergeCommand());
        m.addCommand("validate-profile", new ValidateProfileCommand());
        m.addCommand("verify", new VerifyCommand());

        m.addCommand("kgcl-apply", new ApplyCommand());
        m.addCommand("kgcl-mint", new MintCommand());

        new PluginManager().addPluggableCommands(m);

        m.main(args);
    }

}
