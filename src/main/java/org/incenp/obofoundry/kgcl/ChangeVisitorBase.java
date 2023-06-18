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
public class ChangeVisitorBase implements IChangeVisitor {

    @Override
    public void visit(Change v) {
    }

    @Override
    public void visit(SimpleChange v) {
    }

    @Override
    public void visit(EdgeChange v) {
    }

    @Override
    public void visit(EdgeCreation v) {
    }

    @Override
    public void visit(PlaceUnder v) {
    }

    @Override
    public void visit(MappingCreation v) {
    }

    @Override
    public void visit(EdgeDeletion v) {
    }

    @Override
    public void visit(RemoveUnder v) {
    }

    @Override
    public void visit(EdgeObsoletion v) {
    }

    @Override
    public void visit(EdgeRewiring v) {
    }

    @Override
    public void visit(NodeMove v) {
    }

    @Override
    public void visit(NodeDeepening v) {
    }

    @Override
    public void visit(NodeShallowing v) {
    }

    @Override
    public void visit(PredicateChange v) {
    }

    @Override
    public void visit(EdgeLogicalInterpretationChange v) {
    }

    @Override
    public void visit(LogicalAxiomChange v) {
    }

    @Override
    public void visit(NodeChange v) {
    }

    @Override
    public void visit(NodeRename v) {
    }

    @Override
    public void visit(SetLanguageForName v) {
    }

    @Override
    public void visit(NodeAnnotationChange v) {
    }

    @Override
    public void visit(NodeAnnotationReplacement v) {
    }

    @Override
    public void visit(NodeSynonymChange v) {
    }

    @Override
    public void visit(NewSynonym v) {
    }

    @Override
    public void visit(NameBecomesSynonym v) {
    }

    @Override
    public void visit(RemoveSynonym v) {
    }

    @Override
    public void visit(SynonymReplacement v) {
    }

    @Override
    public void visit(SynonymPredicateChange v) {
    }

    @Override
    public void visit(NodeMappingChange v) {
    }

    @Override
    public void visit(NewMapping v) {
    }

    @Override
    public void visit(RemoveMapping v) {
    }

    @Override
    public void visit(MappingReplacement v) {
    }

    @Override
    public void visit(MappingPredicateChange v) {
    }

    @Override
    public void visit(NodeMetadataAssertionChange v) {
    }

    @Override
    public void visit(NewMetadataAssertion v) {
    }

    @Override
    public void visit(RemoveMetadataAssertion v) {
    }

    @Override
    public void visit(MetadataAssertionReplacement v) {
    }

    @Override
    public void visit(MetadataAssertionPredicateChange v) {
    }

    @Override
    public void visit(NodeTextDefinitionChange v) {
    }

    @Override
    public void visit(NewTextDefinition v) {
    }

    @Override
    public void visit(RemoveTextDefinition v) {
    }

    @Override
    public void visit(TextDefinitionReplacement v) {
    }

    @Override
    public void visit(AddNodeToSubset v) {
    }

    @Override
    public void visit(RemoveNodeFromSubset v) {
    }

    @Override
    public void visit(NodeObsoletion v) {
    }

    @Override
    public void visit(NodeDirectMerge v) {
    }

    @Override
    public void visit(NodeObsoletionWithDirectReplacement v) {
    }

    @Override
    public void visit(NodeObsoletionWithNoDirectReplacement v) {
    }

    @Override
    public void visit(NodeUnobsoletion v) {
    }

    @Override
    public void visit(NodeCreation v) {
    }

    @Override
    public void visit(ClassCreation v) {
    }

    @Override
    public void visit(ObjectPropertyCreation v) {
    }

    @Override
    public void visit(NodeDeletion v) {
    }

    @Override
    public void visit(ComplexChange v) {
    }

    @Override
    public void visit(MultiNodeObsoletion v) {
    }

    @Override
    public void visit(Transaction v) {
    }
}
