/*
 * KGCL-Java - KGCL library for Java
 * Copyright © 2023,2024 Damien Goutte-Gattat
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
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.Edge;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationChange;
import org.incenp.obofoundry.kgcl.model.NodeDeepening;
import org.incenp.obofoundry.kgcl.model.NodeDeletion;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.NodeShallowing;
import org.incenp.obofoundry.kgcl.model.NodeUnobsoletion;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.incenp.obofoundry.kgcl.parser.KGCLBaseVisitor;
import org.incenp.obofoundry.kgcl.parser.KGCLParser;
import org.incenp.obofoundry.kgcl.parser.KGCLParser.IdContext;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * A visitor to converts the ANTLR parse tree of a KGCL changeset into a list of
 * {@link org.incenp.obofoundry.kgcl.model.Change} objects.
 * <p>
 * This class is intended to be used internally by {@link KGCLReader} objects.
 * 
 */
public class ParseTree2ChangeVisitor extends KGCLBaseVisitor<Void> {

    private PrefixManager prefixManager;
    private List<Change> changes;
    private String currentText;
    private String currentLang;
    private String currentId;

    /**
     * Creates a new visitor with the specified prefix manager.
     * 
     * @param prefixManager An OWL API prefix manager that will be used to convert
     *                      short identifiers (“CURIEs”) into their corresponding
     *                      full-length, canonical forms. May be {@code null}, in
     *                      which case short identifiers will all be assumed to be
     *                      OBO-style CURIEs in the
     *                      {@code http://purl.obolibrary.org/obo/} namespace.
     */
    public ParseTree2ChangeVisitor(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
        changes = new ArrayList<Change>();
    }

    /**
     * Creates a new visitor with the specified prefix manager and list to store the
     * changes.
     * 
     * @param prefixManager An OWL API prefix manager to convert short identifiers
     *                      into their full-length forms. May be {@code null}.
     * @param changes       The list where changes built from the parse tree will be
     *                      accumulated.
     */
    public ParseTree2ChangeVisitor(PrefixManager prefixManager, List<Change> changes) {
        this.prefixManager = prefixManager;
        this.changes = changes;
    }

    /**
     * Gets the changes obtained from converting the parse tree. Call this function
     * after having visited the entire parse tree to get the entire set of changes.
     * 
     * @return The list of change objects.
     */
    public List<Change> getChangeSet() {
        return changes;
    }

    @Override
    public Void visitRename(KGCLParser.RenameContext ctx) {
        NodeRename change = new NodeRename();

        change.setAboutNode(getNode(ctx.id()));

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
        change.setAboutNode(getNode(ctx.old_id));
        changes.add(change);

        return null;
    }

    @Override
    public Void visitObsoleteWithReplacement(KGCLParser.ObsoleteWithReplacementContext ctx) {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();
        change.setAboutNode(getNode(ctx.old_id));
        change.setHasDirectReplacement(getNode(ctx.new_id));
        changes.add(change);

        return null;
    }

    @Override
    public Void visitObsoleteWithAlternative(KGCLParser.ObsoleteWithAlternativeContext ctx) {
        NodeObsoletionWithNoDirectReplacement change = new NodeObsoletionWithNoDirectReplacement();

        change.setAboutNode(getNode(ctx.old_id));

        ArrayList<Node> alternatives = new ArrayList<Node>();
        for ( KGCLParser.IdContext alt : ctx.alt_id.id() ) {
            alternatives.add(getNode(alt));
        }
        change.setHasNondirectReplacement(alternatives);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitUnobsolete(KGCLParser.UnobsoleteContext ctx) {
        NodeUnobsoletion change = new NodeUnobsoletion();
        change.setAboutNode(getNode(ctx.id()));
        changes.add(change);

        return null;
    }

    @Override
    public Void visitDelete(KGCLParser.DeleteContext ctx) {
        NodeDeletion change = new NodeDeletion();
        change.setAboutNode(getNode(ctx.id()));
        changes.add(change);

        return null;
    }

    @Override
    public Void visitNewSynonym(KGCLParser.NewSynonymContext ctx) {
        NewSynonym change = new NewSynonym();

        change.setAboutNode(getNode(ctx.id()));

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

        change.setAboutNode(getNode(ctx.id()));

        ctx.synonym.accept(this);
        change.setOldValue(currentText);
        change.setOldLanguage(currentLang);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitChangeSynonym(KGCLParser.ChangeSynonymContext ctx) {
        SynonymReplacement change = new SynonymReplacement();

        change.setAboutNode(getNode(ctx.id()));

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

        change.setAboutNode(getNode(ctx.id()));

        ctx.new_definition.accept(this);
        change.setNewValue(currentText);
        change.setNewLanguage(currentLang);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitRemoveDefinition(KGCLParser.RemoveDefinitionContext ctx) {
        RemoveTextDefinition change = new RemoveTextDefinition();
        change.setAboutNode(getNode(ctx.id()));
        changes.add(change);

        return null;
    }

    @Override
    public Void visitChangeDefinition(KGCLParser.ChangeDefinitionContext ctx) {
        TextDefinitionReplacement change = new TextDefinitionReplacement();

        change.setAboutNode(getNode(ctx.id()));

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
    public Void visitNewClass(KGCLParser.NewClassContext ctx) {
        ClassCreation change = new ClassCreation();

        change.setAboutNode(getNode(ctx.id()));

        ctx.label.accept(this);
        change.setNewValue(currentText);
        change.setNewLanguage(currentLang);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitNewEdge(KGCLParser.NewEdgeContext ctx) {
        EdgeCreation change = new EdgeCreation();
        change.setSubject(getNode(ctx.subject_id));
        change.setPredicate(getNode(ctx.predicate_id));
        change.setObject(getNode(ctx.object_id));
        changes.add(change);

        return null;
    }

    @Override
    public Void visitDeleteEdge(KGCLParser.DeleteEdgeContext ctx) {
        EdgeDeletion change = new EdgeDeletion();
        change.setSubject(getNode(ctx.subject_id));
        change.setPredicate(getNode(ctx.predicate_id));
        change.setObject(getNode(ctx.object_id));
        changes.add(change);

        return null;
    }

    @Override
    public Void visitMove(KGCLParser.MoveContext ctx) {
        NodeMove change;
        switch ( ctx.getStart().getText() ) {
        case "move":
            change = new NodeMove();
            break;
        case "deepen":
            change = new NodeDeepening();
            break;
        case "shallow":
            change = new NodeShallowing();
            break;
        default: // Should never happen
            change = new NodeMove();
            break;
        }

        Edge edge = new Edge();
        edge.setSubject(getNode(ctx.subject_id));
        change.setAboutEdge(edge);

        ctx.old_parent.accept(this);
        change.setOldValue(currentId);

        ctx.new_parent.accept(this);
        change.setNewValue(currentId);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitChangePredicate(KGCLParser.ChangePredicateContext ctx) {
        PredicateChange change = new PredicateChange();

        Edge edge = new Edge();
        edge.setSubject(getNode(ctx.subject_id));
        edge.setObject(getNode(ctx.object_id));
        change.setAboutEdge(edge);

        ctx.old_predicate_id.accept(this);
        change.setOldValue(currentId);

        ctx.new_predicate_id.accept(this);
        change.setNewValue(currentId);

        changes.add(change);

        return null;
    }

    @Override
    public Void visitChangeAnnotation(KGCLParser.ChangeAnnotationContext ctx) {
        NodeAnnotationChange change = new NodeAnnotationChange();

        change.setAboutNode(getNode(ctx.subject_id));

        ctx.predicate_id.accept(this);
        change.setAnnotationProperty(currentId);

        ctx.old_annotation.accept(this);
        change.setOldValue(currentText);
        change.setOldLanguage(currentLang);

        ctx.new_annotation.accept(this);
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

    private Node getNode(IdContext ctx) {
        ctx.accept(this);
        Node node = new Node();
        node.setId(currentId);
        return node;
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
