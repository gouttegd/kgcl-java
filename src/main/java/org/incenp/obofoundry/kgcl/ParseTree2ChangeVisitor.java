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
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
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
    public Void visitObsoleteNoReplacement(KGCLParser.ObsoleteNoReplacementContext ctx) {
        NodeObsoletion change = new NodeObsoletion();

        Node aboutNode = new Node();
        ctx.old_id.accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        changes.add(change);
        return null;
    }

    @Override
    public Void visitObsoleteWithReplacement(KGCLParser.ObsoleteWithReplacementContext ctx) {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();

        Node aboutNode = new Node();
        ctx.old_id.accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        Node replacementNode = new Node();
        ctx.new_id.accept(this);
        replacementNode.setId(currentId);
        change.setHasDirectReplacement(replacementNode);

        changes.add(change);
        return null;
    }

    @Override
    public Void visitObsoleteWithAlternative(KGCLParser.ObsoleteWithAlternativeContext ctx) {
        NodeObsoletionWithNoDirectReplacement change = new NodeObsoletionWithNoDirectReplacement();

        Node aboutNode = new Node();
        ctx.old_id.accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        ArrayList<Node> alternatives = new ArrayList<Node>();
        for ( KGCLParser.IdContext alt : ctx.alt_id.id() ) {
            Node altNode = new Node();
            alt.accept(this);
            altNode.setId(currentId);
            alternatives.add(altNode);
        }
        change.setHasNondirectReplacement(alternatives);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitNewSynonym(KGCLParser.NewSynonymContext ctx) {
        NewSynonym change = new NewSynonym();

        Node aboutNode = new Node();
        ctx.id().accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        ctx.synonym.accept(this);
        change.setNewValue(currentText);
        change.setNewLanguage(currentLang);

        if ( ctx.qualifier() != null ) {
            change.setQualifier(ctx.qualifier().getText());
        }

        changes.add(change);

        return null;
    }

    @Override
    public Void visitRemoveSynonym(KGCLParser.RemoveSynonymContext ctx) {
        RemoveSynonym change = new RemoveSynonym();

        Node aboutNode = new Node();
        ctx.id().accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        ctx.synonym.accept(this);
        change.setOldValue(currentText);
        change.setOldLanguage(currentLang);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitChangeSynonym(KGCLParser.ChangeSynonymContext ctx) {
        SynonymReplacement change = new SynonymReplacement();

        Node aboutNode = new Node();
        ctx.id().accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        ctx.old_synonym.accept(this);
        change.setOldValue(currentText);
        change.setOldLanguage(currentLang);

        ctx.new_synonym.accept(this);
        change.setNewValue(currentText);
        change.setNewLanguage(currentLang);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitNewDefinition(KGCLParser.NewDefinitionContext ctx) {
        NewTextDefinition change = new NewTextDefinition();

        Node aboutNode = new Node();
        ctx.id().accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        ctx.new_definition.accept(this);
        change.setNewValue(currentText);
        change.setNewLanguage(currentLang);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitRemoveDefinition(KGCLParser.RemoveDefinitionContext ctx) {
        RemoveTextDefinition change = new RemoveTextDefinition();

        Node aboutNode = new Node();
        ctx.id().accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitChangeDefinition(KGCLParser.ChangeDefinitionContext ctx) {
        TextDefinitionReplacement change = new TextDefinitionReplacement();

        Node aboutNode = new Node();
        ctx.id().accept(this);
        aboutNode.setId(currentId);
        change.setAboutNode(aboutNode);

        if ( ctx.old_definition != null ) {
            ctx.old_definition.accept(this);
            change.setOldValue(currentText);
            change.setOldLanguage(currentLang);
        }

        ctx.new_definition.accept(this);
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
    public Void visitText(KGCLParser.TextContext ctx) {
        currentText = unquote(ctx.string().getText());

        currentLang = "";
        if ( ctx.lang != null ) {
            currentLang = ctx.lang.getText().substring(1);
        }

        return null;
    }

    private String unquote(String s) {
        StringBuilder sb = new StringBuilder();
        for ( int i = 1, n = s.length(); i < n - 1; i++ ) {
            char c = s.charAt(i);
            if ( c != '\\' ) {
                sb.append(c);
            }
        }

        return sb.toString();
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
