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

package org.incenp.obofoundry.kgcl.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.incenp.obofoundry.kgcl.ILabelResolver;
import org.incenp.obofoundry.kgcl.KGCLReader;
import org.incenp.obofoundry.kgcl.model.AddNodeToSubset;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.Edge;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationChange;
import org.incenp.obofoundry.kgcl.model.NodeChange;
import org.incenp.obofoundry.kgcl.model.NodeCreation;
import org.incenp.obofoundry.kgcl.model.NodeDeepening;
import org.incenp.obofoundry.kgcl.model.NodeDeletion;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.NodeShallowing;
import org.incenp.obofoundry.kgcl.model.NodeUnobsoletion;
import org.incenp.obofoundry.kgcl.model.ObjectPropertyCreation;
import org.incenp.obofoundry.kgcl.model.OntologySubset;
import org.incenp.obofoundry.kgcl.model.OwlType;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveNodeFromSubset;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.incenp.obofoundry.kgcl.parser.KGCLParser.IdContext;
import org.incenp.obofoundry.kgcl.parser.KGCLParser.TextContext;
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
    private ILabelResolver labelResolver;
    private List<Change> changes;
    private List<IParseTreeErrorListener> errorListeners = new ArrayList<IParseTreeErrorListener>();
    private String currentId;
    private boolean isBogus = false;

    /**
     * Creates a new visitor with the specified prefix manager and list to store the
     * changes.
     * 
     * @param prefixManager An OWL API prefix manager to convert short identifiers
     *                      into their full-length forms. May be {@code null}.
     * @param labelResolver The resolver to use to resolve labels into identifiers.
     * @param changes       The list where changes built from the parse tree will be
     *                      accumulated.
     */
    public ParseTree2ChangeVisitor(PrefixManager prefixManager, ILabelResolver labelResolver,
            List<Change> changes) {
        this.prefixManager = prefixManager;
        this.labelResolver = labelResolver;
        this.changes = changes;
    }

    /**
     * Adds a listener for errors that may occur when processing the parse tree.
     * 
     * @param listener The listener to add.
     */
    public void addErrorListener(IParseTreeErrorListener listener) {
        errorListeners.add(listener);
    }

    @Override
    public Void visitRename(KGCLParser.RenameContext ctx) {
        NodeRename change = new NodeRename();

        change.setAboutNode(getNode(ctx.id()));
        setOldValue(ctx.old_label, change);
        setNewValue(ctx.new_label, change);

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitObsoleteNoReplacement(KGCLParser.ObsoleteNoReplacementContext ctx) {
        NodeObsoletion change = new NodeObsoletion();
        change.setAboutNode(getNode(ctx.old_id));
        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitObsoleteWithReplacement(KGCLParser.ObsoleteWithReplacementContext ctx) {
        NodeObsoletionWithDirectReplacement change = new NodeObsoletionWithDirectReplacement();
        change.setAboutNode(getNode(ctx.old_id));
        change.setHasDirectReplacement(getNode(ctx.new_id));
        maybeAddChange(change);

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

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitUnobsolete(KGCLParser.UnobsoleteContext ctx) {
        NodeUnobsoletion change = new NodeUnobsoletion();
        change.setAboutNode(getNode(ctx.id()));
        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitDelete(KGCLParser.DeleteContext ctx) {
        NodeDeletion change = new NodeDeletion();
        change.setAboutNode(getNode(ctx.id()));
        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitNewSynonym(KGCLParser.NewSynonymContext ctx) {
        NewSynonym change = new NewSynonym();

        change.setAboutNode(getNode(ctx.id()));
        setNewValue(ctx.synonym, change);

        if ( ctx.qualifier() != null ) {
            change.setQualifier(ctx.qualifier().getText());
        }

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitRemoveSynonym(KGCLParser.RemoveSynonymContext ctx) {
        RemoveSynonym change = new RemoveSynonym();

        change.setAboutNode(getNode(ctx.id()));
        setOldValue(ctx.synonym, change);

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitChangeSynonym(KGCLParser.ChangeSynonymContext ctx) {
        SynonymReplacement change = new SynonymReplacement();

        change.setAboutNode(getNode(ctx.id()));
        setOldValue(ctx.old_synonym, change);
        setNewValue(ctx.new_synonym, change);

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitNewDefinition(KGCLParser.NewDefinitionContext ctx) {
        NewTextDefinition change = new NewTextDefinition();

        change.setAboutNode(getNode(ctx.id()));
        setNewValue(ctx.new_definition, change);

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitRemoveDefinition(KGCLParser.RemoveDefinitionContext ctx) {
        RemoveTextDefinition change = new RemoveTextDefinition();
        change.setAboutNode(getNode(ctx.id()));
        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitChangeDefinition(KGCLParser.ChangeDefinitionContext ctx) {
        TextDefinitionReplacement change = new TextDefinitionReplacement();

        change.setAboutNode(getNode(ctx.id()));

        if ( ctx.old_definition != null ) {
            setOldValue(ctx.old_definition, change);
        }

        setNewValue(ctx.new_definition, change);

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitNewNode(KGCLParser.NewNodeContext ctx) {
        NodeCreation change = null;
        OwlType type = OwlType.fromString(ctx.nodeType().getText());

        switch ( type ) {
        case CLASS:
            change = new ClassCreation();
            break;
        case OBJECT_PROPERTY:
            change = new ObjectPropertyCreation();
            break;
        default:
            /* Other types don't have their own change object. */
            change = new NodeCreation();
            break;
        }

        if ( ctx.id() != null ) {
            change.setAboutNode(getNode(ctx.id()));
            labelResolver.add(unquote(ctx.label.string().getText()), currentId);
        } else {
            String newId = labelResolver.getNewId(unquote(ctx.label.string().getText()));
            Node aboutNode = new Node();
            aboutNode.setId(newId);
            change.setAboutNode(aboutNode);
        }

        change.getAboutNode().setOwlType(type);
        setNewValue(ctx.label, change);

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitNewEdge(KGCLParser.NewEdgeContext ctx) {
        EdgeCreation change = new EdgeCreation();
        change.setSubject(getNode(ctx.subject_id));
        change.setPredicate(getNode(ctx.predicate_id));
        change.setObject(getNode(ctx.object_id));
        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitDeleteEdge(KGCLParser.DeleteEdgeContext ctx) {
        EdgeDeletion change = new EdgeDeletion();
        change.setSubject(getNode(ctx.subject_id));
        change.setPredicate(getNode(ctx.predicate_id));
        change.setObject(getNode(ctx.object_id));
        maybeAddChange(change);

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

        maybeAddChange(change);

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

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitChangeAnnotation(KGCLParser.ChangeAnnotationContext ctx) {
        NodeAnnotationChange change = new NodeAnnotationChange();

        change.setAboutNode(getNode(ctx.subject_id));

        ctx.predicate_id.accept(this);
        change.setAnnotationProperty(currentId);

        setOldValue(ctx.old_annotation, change);
        setNewValue(ctx.new_annotation, change);

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitAddSubset(KGCLParser.AddSubsetContext ctx) {
        AddNodeToSubset change = new AddNodeToSubset();

        change.setAboutNode(getNode(ctx.node_id));
        change.setInSubset(getSubset(ctx.subset_id));

        maybeAddChange(change);

        return null;
    }

    @Override
    public Void visitRemoveSubset(KGCLParser.RemoveSubsetContext ctx) {
        RemoveNodeFromSubset change = new RemoveNodeFromSubset();

        change.setAboutNode(getNode(ctx.node_id));
        change.setInSubset(getSubset(ctx.subset_id));

        maybeAddChange(change);

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
    public Void visitIdAsLabel(KGCLParser.IdAsLabelContext ctx) {
        String label = unquote(ctx.string().getText());

        currentId = labelResolver.resolve(label);
        if ( currentId == null ) {
            onParseTreeError(ctx.getStart(), String.format("Unresolved label %s", label));
        }

        return null;
    }

    private void setOldValue(TextContext ctx, NodeChange change) {
        change.setOldValue(unquote(ctx.string().getText()));
        if ( ctx.lang != null ) {
            change.setOldLanguage(ctx.lang.getText().substring(1));
        }
        if ( ctx.type != null ) {
            change.setOldDatatype(expandCurie(ctx.type.datatype.getText()));
        }
    }

    private void setNewValue(TextContext ctx, NodeChange change) {
        change.setNewValue(unquote(ctx.string().getText()));
        if ( ctx.lang != null ) {
            change.setNewLanguage(ctx.lang.getText().substring(1));
        }
        if ( ctx.type != null ) {
            change.setNewDatatype(expandCurie(ctx.type.datatype.getText()));
        }
    }

    private Node getNode(IdContext ctx) {
        ctx.accept(this);
        Node node = new Node();
        node.setId(currentId);
        return node;
    }

    private OntologySubset getSubset(IdContext ctx) {
        ctx.accept(this);
        OntologySubset subset = new OntologySubset();
        subset.setId(currentId);
        return subset;
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
        if ( prefixManager == null ) {
            return curie;
        }

        String[] parts = curie.split(":", 2);
        String prefix = parts[0] + ":";
        if ( prefixManager.containsPrefixMapping(prefix) ) {
            return prefixManager.getIRI(prefix).toString() + parts[1];
        }

        return curie;
    }

    private void onParseTreeError(Token token, String message) {
        isBogus = true;
        for ( IParseTreeErrorListener listener : errorListeners ) {
            listener.parseTreeError(token.getLine(), token.getCharPositionInLine(), message);
        }
    }

    private void maybeAddChange(Change change) {
        if ( !isBogus ) {
            changes.add(change);
        }
        isBogus = false;
    }
}
