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

/**
 * Dummy implementation of {@link IChangeVisitor}. KGCL visitors may extend this
 * class instead of implementing the interface directly, and override only the
 * methods they need to implement.
 */
public class ChangeVisitorBase<T> implements IChangeVisitor<T> {

    /*
     * Override this method in derived classes to change the value returned by
     * unimplemented methods.
     */
    protected T doDefault(Change v) {
        return null;
    }

    @Override
    public T visit(Change v) {
        return doDefault(v);
    }

    @Override
    public T visit(SimpleChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(EdgeChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(EdgeCreation v) {
        return doDefault(v);
    }

    @Override
    public T visit(PlaceUnder v) {
        return doDefault(v);
    }

    @Override
    public T visit(MappingCreation v) {
        return doDefault(v);
    }

    @Override
    public T visit(EdgeDeletion v) {
        return doDefault(v);
    }

    @Override
    public T visit(RemoveUnder v) {
        return doDefault(v);
    }

    @Override
    public T visit(EdgeObsoletion v) {
        return doDefault(v);
    }

    @Override
    public T visit(EdgeRewiring v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeMove v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeDeepening v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeShallowing v) {
        return doDefault(v);
    }

    @Override
    public T visit(PredicateChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(EdgeLogicalInterpretationChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(LogicalAxiomChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeRename v) {
        return doDefault(v);
    }

    @Override
    public T visit(SetLanguageForName v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeAnnotationChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeAnnotationReplacement v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeSynonymChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NewSynonym v) {
        return doDefault(v);
    }

    @Override
    public T visit(NameBecomesSynonym v) {
        return doDefault(v);
    }

    @Override
    public T visit(RemoveSynonym v) {
        return doDefault(v);
    }

    @Override
    public T visit(SynonymReplacement v) {
        return doDefault(v);
    }

    @Override
    public T visit(SynonymPredicateChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeMappingChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NewMapping v) {
        return doDefault(v);
    }

    @Override
    public T visit(RemoveMapping v) {
        return doDefault(v);
    }

    @Override
    public T visit(MappingReplacement v) {
        return doDefault(v);
    }

    @Override
    public T visit(MappingPredicateChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeMetadataAssertionChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NewMetadataAssertion v) {
        return doDefault(v);
    }

    @Override
    public T visit(RemoveMetadataAssertion v) {
        return doDefault(v);
    }

    @Override
    public T visit(MetadataAssertionReplacement v) {
        return doDefault(v);
    }

    @Override
    public T visit(MetadataAssertionPredicateChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeTextDefinitionChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(NewTextDefinition v) {
        return doDefault(v);
    }

    @Override
    public T visit(RemoveTextDefinition v) {
        return doDefault(v);
    }

    @Override
    public T visit(TextDefinitionReplacement v) {
        return doDefault(v);
    }

    @Override
    public T visit(AddNodeToSubset v) {
        return doDefault(v);
    }

    @Override
    public T visit(RemoveNodeFromSubset v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeObsoletion v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeDirectMerge v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeObsoletionWithDirectReplacement v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeObsoletionWithNoDirectReplacement v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeUnobsoletion v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeCreation v) {
        return doDefault(v);
    }

    @Override
    public T visit(ClassCreation v) {
        return doDefault(v);
    }

    @Override
    public T visit(ObjectPropertyCreation v) {
        return doDefault(v);
    }

    @Override
    public T visit(NodeDeletion v) {
        return doDefault(v);
    }

    @Override
    public T visit(ComplexChange v) {
        return doDefault(v);
    }

    @Override
    public T visit(MultiNodeObsoletion v) {
        return doDefault(v);
    }

    @Override
    public T visit(Transaction v) {
        return doDefault(v);
    }
}
