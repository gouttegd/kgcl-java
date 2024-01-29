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

import java.util.HashMap;

import org.incenp.obofoundry.kgcl.model.Change;
import org.incenp.obofoundry.kgcl.model.ClassCreation;
import org.incenp.obofoundry.kgcl.model.EdgeCreation;
import org.incenp.obofoundry.kgcl.model.EdgeDeletion;
import org.incenp.obofoundry.kgcl.model.NewSynonym;
import org.incenp.obofoundry.kgcl.model.NewTextDefinition;
import org.incenp.obofoundry.kgcl.model.Node;
import org.incenp.obofoundry.kgcl.model.NodeDeepening;
import org.incenp.obofoundry.kgcl.model.NodeMove;
import org.incenp.obofoundry.kgcl.model.NodeObsoletion;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeObsoletionWithNoDirectReplacement;
import org.incenp.obofoundry.kgcl.model.NodeRename;
import org.incenp.obofoundry.kgcl.model.NodeShallowing;
import org.incenp.obofoundry.kgcl.model.NodeUnobsoletion;
import org.incenp.obofoundry.kgcl.model.PlaceUnder;
import org.incenp.obofoundry.kgcl.model.PredicateChange;
import org.incenp.obofoundry.kgcl.model.RemoveSynonym;
import org.incenp.obofoundry.kgcl.model.RemoveTextDefinition;
import org.incenp.obofoundry.kgcl.model.RemoveUnder;
import org.incenp.obofoundry.kgcl.model.SimpleChange;
import org.incenp.obofoundry.kgcl.model.SynonymReplacement;
import org.incenp.obofoundry.kgcl.model.TextDefinitionReplacement;
import org.semanticweb.owlapi.model.PrefixManager;

/**
 * A visitor to convert a list of KGCL {@link Change} objects into their textual
 * representation in the KGCL language.
 * <p>
 * This class is primarily intended for internal use by {@link KGCLWriter}
 * objects. However it may also be used directly by client code to obtain the
 * string representation of a KGCL change without writing to a file or file-like
 * object:
 * 
 * <pre>
 * Change change = ... ;
 * Change2TextVisitor visitor = new Change2TextVisitor();
 * String changeAsText = change.accept(visitor);
 * </pre>
 */
public class Change2TextVisitor extends ChangeVisitorBase<String> {

    private HashMap<String, String> shortIdentifierCache = new HashMap<String, String>();
    private PrefixManager prefixManager;

    /**
     * Creates a new instance with the specified prefix manager.
     * 
     * @param prefixManager The prefix manager to be used for shortening
     *                      identifiers. May be {@code null}, in which case
     *                      identifiers will never be shortened.
     */
    public Change2TextVisitor(PrefixManager prefixManager) {
        this.prefixManager = prefixManager;
    }

    /**
     * Creates a new instance without a prefix manager. Identifiers will never be
     * shortened and will appear in the resulting KGCL text representation in their
     * full-length form surrounded by angled brackets ({@code <...>}).
     */
    public Change2TextVisitor() {
        this(null);
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

    private String renderId(String identifier) {
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
    public String visit(NodeRename v) {
        return String.format("rename %s from %s to %s", renderNode(v.getAboutNode()), renderOldValue(v),
                renderNewValue(v));
    }

    @Override
    public String visit(NewSynonym v) {
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

        return sb.toString();
    }

    @Override
    public String visit(RemoveSynonym v) {
        return String.format("remove synonym %s for %s", renderOldValue(v), renderNode(v.getAboutNode()));
    }

    @Override
    public String visit(SynonymReplacement v) {
        return String.format("change synonym from %s to %s for %s", renderOldValue(v), renderNewValue(v),
                renderNode(v.getAboutNode()));
    }

    @Override
    public String visit(NewTextDefinition v) {
        return String.format("add definition %s for %s", renderNewValue(v), renderNode(v.getAboutNode()));
    }

    @Override
    public String visit(RemoveTextDefinition v) {
        return String.format("remove definition for %s", renderNode(v.getAboutNode()));
    }

    @Override
    public String visit(TextDefinitionReplacement v) {
        StringBuilder sb = new StringBuilder();
        sb.append("change definition of ");
        sb.append(renderNode(v.getAboutNode()));

        if ( v.getOldValue() != null ) {
            sb.append(" from ");
            sb.append(renderOldValue(v));
        }

        sb.append(" to ");
        sb.append(renderNewValue(v));

        return sb.toString();
    }

    @Override
    public String visit(NodeObsoletion v) {
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

        return sb.toString();
    }

    @Override
    public String visit(NodeObsoletionWithDirectReplacement v) {
        return visit((NodeObsoletion) v);
    }

    @Override
    public String visit(NodeObsoletionWithNoDirectReplacement v) {
        return visit((NodeObsoletion) v);
    }

    @Override
    public String visit(NodeUnobsoletion v) {
        return String.format("unobsolete %s", renderNode(v.getAboutNode()));
    }

    @Override
    public String visit(ClassCreation v) {
        return String.format("create class %s %s", renderNode(v.getAboutNode()), renderNewValue(v));
    }

    @Override
    public String visit(EdgeCreation v) {
        return String.format("create edge %s %s %s", renderNode(v.getSubject()), renderNode(v.getPredicate()),
                renderNode(v.getObject()));
    }

    @Override
    public String visit(EdgeDeletion v) {
        return String.format("delete edge %s %s %s", renderNode(v.getSubject()), renderNode(v.getPredicate()),
                renderNode(v.getObject()));
    }

    @Override
    public String visit(PlaceUnder v) {
        return String.format("create edge %s rdfs:subClassOf %s", renderNode(v.getSubject()),
                renderNode(v.getObject()));
    }

    @Override
    public String visit(RemoveUnder v) {
        return String.format("delete edge %s rdfs:subClassOf %s", renderNode(v.getSubject()),
                renderNode(v.getObject()));
    }

    @Override
    public String visit(NodeMove v) {
        return String.format("move %s from %s to %s", renderNode(v.getAboutEdge().getSubject()),
                renderId(v.getOldValue()), renderId(v.getNewValue()));
    }

    @Override
    public String visit(NodeDeepening v) {
        return String.format("deepen %s from %s to %s", renderNode(v.getAboutEdge().getSubject()),
                renderId(v.getOldValue()), renderId(v.getNewValue()));
    }

    @Override
    public String visit(NodeShallowing v) {
        return String.format("shallow %s from %s to %s", renderNode(v.getAboutEdge().getSubject()),
                renderId(v.getOldValue()), renderId(v.getNewValue()));
    }

    @Override
    public String visit(PredicateChange v) {
        return String.format("change relationship between %s and %s from %s to %s",
                renderNode(v.getAboutEdge().getSubject()), renderNode(v.getAboutEdge().getObject()),
                renderId(v.getOldValue()), renderId(v.getNewValue()));
    }
}
