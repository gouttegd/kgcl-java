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
import java.util.HashMap;
import java.util.List;

import org.incenp.obofoundry.kgcl.model.AddNodeToSubset;
import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.ComplexChange;
import org.incenp.obofoundry.kgcl.model.EdgeChange;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.EdgeLogicalInterpretationChange;
import org.incenp.obofoundry.kgcl.model.EdgeObsoletion;
import org.incenp.obofoundry.kgcl.model.EdgeRewiring;
import org.incenp.obofoundry.kgcl.model.IChangeVisitor;
import org.incenp.obofoundry.kgcl.model.LogicalAxiomChange;
import org.incenp.obofoundry.kgcl.model.MappingCreation;
import org.incenp.obofoundry.kgcl.model.MappingPredicateChange;
import org.incenp.obofoundry.kgcl.model.MappingReplacement;
import org.incenp.obofoundry.kgcl.model.MetadataAssertionPredicateChange;
import org.incenp.obofoundry.kgcl.model.MetadataAssertionReplacement;
import org.incenp.obofoundry.kgcl.model.MultiNodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NameBecomesSynonym;
import org.incenp.obofoundry.kgcl.model.NewMapping;
import org.incenp.obofoundry.kgcl.model.NewMetadataAssertion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationChange;
import org.incenp.obofoundry.kgcl.model.NodeAnnotationReplacement;
import org.incenp.obofoundry.kgcl.model.NodeChange;
import org.incenp.obofoundry.kgcl.model.NodeCreation;
import org.incenp.obofoundry.kgcl.model.NodeDeepening;
import org.incenp.obofoundry.kgcl.model.NodeDeletion;
import org.incenp.obofoundry.kgcl.model.NodeDirectMerge;
import org.incenp.obofoundry.kgcl.model.NodeMappingChange;
import org.incenp.obofoundry.kgcl.model.NodeMetadataAssertionChange;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.NodeShallowing;
import org.incenp.obofoundry.kgcl.model.NodeSynonymChange;
import org.incenp.obofoundry.kgcl.model.NodeTextDefinitionChange;
import org.incenp.obofoundry.kgcl.model.NodeUnobsoletion;
import org.incenp.obofoundry.kgcl.model.ObjectPropertyCreation;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveMapping;
import org.incenp.obofoundry.kgcl.model.RemoveMetadataAssertion;
import org.incenp.obofoundry.kgcl.model.RemoveNodeFromSubset;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;
import org.incenp.obofoundry.kgcl.model.SetLanguageForName;
import org.incenp.obofoundry.kgcl.model.SimpleChange;
import org.incenp.obofoundry.kgcl.model.SynonymPredicateChange;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.incenp.obofoundry.kgcl.model.Transaction;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * A visitor to convert a list of KGCL
 * {@link org.incenp.obofoundry.kgcl.model.Change} objects into their textual
 * representation in the KGCL language.
 */
public class Change2TextVisitor implements IChangeVisitor {

    private ArrayList<String> commands = new ArrayList<String>();
    private HashMap<String, String> shortIdentifierCache = new HashMap<String, String>();
    private PrefixManager prefixManager;

    /**
     * Create a new instance with the specified prefix manager.
     * 
     * @param prefixManager The prefix manager to be used for shortening
     *                      identifiers.
     */
    public Change2TextVisitor(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    /**
     * Create a new instance without a prefix manager.
     */
    public Change2TextVisitor() {
        this(null);
    }

    /**
     * Get all the converted commands made by the visitor so far.
     * 
     * @return A list of KGCL instructions.
     */
    public List<String> getCommands() {
        return commands;
    }

    /*
     * Format a text value for inclusion into a KGCL command. This deals with
     * escaping internal quote characters and appending a language tag if needed.
     */
    private String renderText(String value, String lang) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for ( int i = 0, n = value.length(); i < n; i++ ) {
            char c = value.charAt(i);
            if ( c == '"' ) {
                sb.append("\\");
            }
            sb.append(c);
        }
        sb.append('"');
        if ( lang != null && lang.length() > 0 ) {
            sb.append('@');
            sb.append(lang);
        }

        return sb.toString();
    }

    /*
     * Convenience method to render the "new" value of a change.
     */
    private String renderNewValue(SimpleChange v) {
        return renderText(v.getNewValue(), v.getNewLanguage());
    }

    /*
     * Convenience method to render the "old" value of a change.
     */
    private String renderOldValue(SimpleChange v) {
        return renderText(v.getOldValue(), v.getOldLanguage());
    }

    /*
     * Format a node into a representation of its identifier.
     */
    private String renderNode(Node node) {
        String identifier = node.getId();

        String shortIdentifier = getShortIdentifier(identifier);
        if ( shortIdentifier != null ) {
            return shortIdentifier;
        }
        
        return String.format("<%s>", identifier);
    }

    /*
     * Condense an identifier into a short form if possible.
     */
    private String getShortIdentifier(String iri) {
        String shortId = shortIdentifierCache.getOrDefault(iri, null);

        if ( shortId == null && prefixManager != null ) {
            String bestPrefix = null;
            int bestLength = 0;

            // Get the best (longest) prefix that matches the identifier
            for ( String prefixName : prefixManager.getPrefixNames() ) {
                String prefix = prefixManager.getPrefix(prefixName);
                if ( iri.startsWith(prefix) && prefix.length() > bestLength ) {
                    bestPrefix = prefixName;
                    bestLength = prefix.length();
                }
            }

            if ( bestPrefix != null ) {
                shortId = bestPrefix + iri.substring(bestLength);
                shortIdentifierCache.put(iri, shortId);
            }
        }

        return shortId;
    }

    @Override
    public void visit(Change v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(SimpleChange v) {
        // TODO Auto-generated method stub
    }

    @Override
    public void visit(EdgeChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(PlaceUnder v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MappingCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeDeletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveUnder v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeObsoletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeRewiring v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeMove v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeDeepening v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeShallowing v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(PredicateChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(EdgeLogicalInterpretationChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(LogicalAxiomChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeRename v) {
        commands.add(String.format("rename %s from %s to %s", renderNode(v.getAboutNode()), renderOldValue(v),
                renderNewValue(v)));
    }

    @Override
    public void visit(SetLanguageForName v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeAnnotationChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeAnnotationReplacement v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeSynonymChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NewSynonym v) {
        StringBuilder sb = new StringBuilder();
        sb.append("create ");

        if ( v.getQualifier() != null ) {
            sb.append(v.getQualifier());
            sb.append(' ');
        }

        sb.append("synonym ");
        sb.append(renderNewValue(v));
        sb.append(" for ");
        sb.append(renderNode(v.getAboutNode()));

        commands.add(sb.toString());
    }

    @Override
    public void visit(NameBecomesSynonym v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveSynonym v) {
        commands.add(String.format("remove synonym %s for %s", renderOldValue(v), renderNode(v.getAboutNode())));
    }

    @Override
    public void visit(SynonymReplacement v) {
        commands.add(String.format("change synonym from %s to %s for %s", renderOldValue(v), renderNewValue(v),
                renderNode(v.getAboutNode())));
    }

    @Override
    public void visit(SynonymPredicateChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeMappingChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NewMapping v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveMapping v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MappingReplacement v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MappingPredicateChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeMetadataAssertionChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NewMetadataAssertion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveMetadataAssertion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MetadataAssertionReplacement v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MetadataAssertionPredicateChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeTextDefinitionChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NewTextDefinition v) {
        commands.add(String.format("add definition %s for %s", renderNewValue(v), renderNode(v.getAboutNode())));
    }

    @Override
    public void visit(RemoveTextDefinition v) {
        commands.add(String.format("remove definition for %s", renderNode(v.getAboutNode())));
    }

    @Override
    public void visit(TextDefinitionReplacement v) {
        StringBuilder sb = new StringBuilder();
        sb.append("change definition of ");
        sb.append(renderNode(v.getAboutNode()));

        if ( v.getOldValue() != null ) {
            sb.append(" from ");
            sb.append(renderOldValue(v));
        }

        sb.append(" to ");
        sb.append(renderNewValue(v));

        commands.add(sb.toString());
    }

    @Override
    public void visit(AddNodeToSubset v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(RemoveNodeFromSubset v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeObsoletion v) {
        StringBuilder sb = new StringBuilder();
        sb.append("obsolete ");
        sb.append(renderNode(v.getAboutNode()));

        if ( v.getHasDirectReplacement() != null ) {
            sb.append(" with replacement ");
            sb.append(renderNode(v.getHasDirectReplacement()));
        } else if ( v.getHasNondirectReplacement() != null ) {
            sb.append(" with alternative ");
            boolean first = true;
            for ( Node consider : v.getHasNondirectReplacement() ) {
                if ( !first ) {
                    sb.append(',');
                }
                sb.append(renderNode(consider));
                first = false;
            }
        }

        commands.add(sb.toString());
    }

    @Override
    public void visit(NodeDirectMerge v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeObsoletionWithDirectReplacement v) {
        visit((NodeObsoletion) v);
    }

    @Override
    public void visit(NodeObsoletionWithNoDirectReplacement v) {
        visit((NodeObsoletion) v);
    }

    @Override
    public void visit(NodeUnobsoletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ClassCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ObjectPropertyCreation v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(NodeDeletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(ComplexChange v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(MultiNodeObsoletion v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void visit(Transaction v) {
        // TODO Auto-generated method stub

    }

}
