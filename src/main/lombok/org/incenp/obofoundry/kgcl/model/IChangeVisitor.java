package org.incenp.obofoundry.kgcl.model;

/**
 * A visitor interface for {@link Change} objects. Implement that
 * interface to apply arbitrary processing to all types of KGCL changes.
 */
public interface IChangeVisitor<T> {
    T visit(Change v);
    T visit(SimpleChange v);
    T visit(EdgeChange v);
    T visit(EdgeCreation v);
    T visit(PlaceUnder v);
    T visit(MappingCreation v);
    T visit(EdgeDeletion v);
    T visit(RemoveUnder v);
    T visit(EdgeObsoletion v);
    T visit(EdgeRewiring v);
    T visit(NodeMove v);
    T visit(NodeDeepening v);
    T visit(NodeShallowing v);
    T visit(PredicateChange v);
    T visit(EdgeLogicalInterpretationChange v);
    T visit(LogicalAxiomChange v);
    T visit(NodeChange v);
    T visit(NodeRename v);
    T visit(SetLanguageForName v);
    T visit(NodeAnnotationChange v);
    T visit(NodeAnnotationReplacement v);
    T visit(NodeSynonymChange v);
    T visit(NewSynonym v);
    T visit(NameBecomesSynonym v);
    T visit(RemoveSynonym v);
    T visit(SynonymReplacement v);
    T visit(SynonymPredicateChange v);
    T visit(NodeMappingChange v);
    T visit(NewMapping v);
    T visit(RemoveMapping v);
    T visit(MappingReplacement v);
    T visit(MappingPredicateChange v);
    T visit(NodeMetadataAssertionChange v);
    T visit(NewMetadataAssertion v);
    T visit(RemoveMetadataAssertion v);
    T visit(MetadataAssertionReplacement v);
    T visit(MetadataAssertionPredicateChange v);
    T visit(NodeTextDefinitionChange v);
    T visit(NewTextDefinition v);
    T visit(RemoveTextDefinition v);
    T visit(TextDefinitionReplacement v);
    T visit(AddNodeToSubset v);
    T visit(RemoveNodeFromSubset v);
    T visit(NodeObsoletion v);
    T visit(NodeDirectMerge v);
    T visit(NodeObsoletionWithDirectReplacement v);
    T visit(NodeObsoletionWithNoDirectReplacement v);
    T visit(NodeUnobsoletion v);
    T visit(NodeCreation v);
    T visit(ClassCreation v);
    T visit(ObjectPropertyCreation v);
    T visit(NodeDeletion v);
    T visit(ComplexChange v);
    T visit(MultiNodeObsoletion v);
    T visit(Transaction v);
}