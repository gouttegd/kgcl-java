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

package org.incenp.obofoundry.kgcl;

import java.util.ArrayList;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.parser.KGCLBaseVisitor;
import org.incenp.obofoundry.kgcl.parser.KGCLParser;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * A visitor to converts the ANTLR parse tree of a KGCL changeset into a list of
 * {@link org.incenp.obofoundry.kgcl.model.Change} objects.
 * 
 */
public class ParseTree2ChangeVisitor extends KGCLBaseVisitor<Void> {

    private PrefixManager prefixManager;
    private ArrayList<Change> changes;
    private String currentText;
    private String currentLang;
    private String currentId;

    /**
     * Creates a new visitor with the specified prefix manager..
     * 
     * @param prefixManager An OWL prefix manager that will be used to convert
     *                      CURIEs into IRIs. May be null.
     */
    public ParseTree2ChangeVisitor(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
        changes = new ArrayList<Change>();
    }

    /**
     * Get the changes obtained from converting the parse tree.
     * 
     * @return The list of change objects.
     */
    public List<Change> getChangeSet() {
        return changes;
    }

    @Override
    public Void visitRename(KGCLParser.RenameContext ctx) {
        NodeRename change = new NodeRename();

        Node aboutNode = new Node();
        ctx.id().accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        ctx.old_label.accept(this);
        change.setOldValue(currentText);
        change.setOldLanguage(currentLang);

        ctx.new_label.accept(this);
        change.setNewValue(currentText);
        change.setNewLanguage(currentLang);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitIdAsIRI(KGCLParser.IdAsIRIContext ctx) {
        currentId = unquote(ctx.IRI().getText());
        return null;
    }

    @Override
    public Void visitIdAsCURIE(KGCLParser.IdAsCURIEContext ctx) {
        currentId = expandCurie(ctx.CURIE().getText());
        return null;
    }

    @Override
    public Void visitLabel(KGCLParser.LabelContext ctx) {
        currentText = unquote(ctx.string().getText());

        currentLang = "";
        if ( ctx.lang != null ) {
            currentLang = ctx.lang.getText();
        }

        return null;
    }

    private String unquote(String s) {
        return s.substring(1, s.length() - 1);
    }

    private String expandCurie(String curie) {
        String[] parts = curie.split(":", 2);
        String prefix = parts[0] + ":";
        String expandedPrefix;

        if ( prefixManager != null && prefixManager.containsPrefixMapping(prefix) ) {
            expandedPrefix = prefixManager.getIRI(prefix).toString();
        } else {
            expandedPrefix = String.format("http://purl.obolibrary.org/obo/%s_", parts[0]);
        }

        return expandedPrefix + parts[1];
    }
}
