package org.incenp.obofoundry.kgcl.model;

/**
 * Visitor interface for Change objects.
 */
public interface IChangeVisitor<T> {
    public T visit(Change visited);
    public T visit(SimpleChange visited);
    public T visit(EdgeChange visited);
    public T visit(EdgeCreation visited);
    public T visit(PlaceUnder visited);
    public T visit(MappingCreation visited);
    public T visit(EdgeDeletion visited);
    public T visit(RemoveUnder visited);
    public T visit(EdgeObsoletion visited);
    public T visit(EdgeRewiring visited);
    public T visit(NodeMove visited);
    public T visit(NodeDeepening visited);
    public T visit(NodeShallowing visited);
    public T visit(PredicateChange visited);
    public T visit(EdgeLogicalInterpretationChange visited);
    public T visit(LogicalAxiomChange visited);
    public T visit(NodeChange visited);
    public T visit(NodeRename visited);
    public T visit(SetLanguageForName visited);
    public T visit(NodeAnnotationChange visited);
    public T visit(NodeAnnotationReplacement visited);
    public T visit(NodeSynonymChange visited);
    public T visit(NewSynonym visited);
    public T visit(NameBecomesSynonym visited);
    public T visit(RemoveSynonym visited);
    public T visit(SynonymReplacement visited);
    public T visit(SynonymPredicateChange visited);
    public T visit(NodeMappingChange visited);
    public T visit(NewMapping visited);
    public T visit(RemoveMapping visited);
    public T visit(MappingReplacement visited);
    public T visit(MappingPredicateChange visited);
    public T visit(NodeMetadataAssertionChange visited);
    public T visit(NewMetadataAssertion visited);
    public T visit(RemoveMetadataAssertion visited);
    public T visit(MetadataAssertionReplacement visited);
    public T visit(MetadataAssertionPredicateChange visited);
    public T visit(NodeTextDefinitionChange visited);
    public T visit(NewTextDefinition visited);
    public T visit(RemoveTextDefinition visited);
    public T visit(TextDefinitionReplacement visited);
    public T visit(AddNodeToSubset visited);
    public T visit(RemoveNodeFromSubset visited);
    public T visit(NodeObsoletion visited);
    public T visit(NodeDirectMerge visited);
    public T visit(NodeObsoletionWithDirectReplacement visited);
    public T visit(NodeObsoletionWithNoDirectReplacement visited);
    public T visit(NodeUnobsoletion visited);
    public T visit(NodeCreation visited);
    public T visit(ClassCreation visited);
    public T visit(ObjectPropertyCreation visited);
    public T visit(NodeDeletion visited);
    public T visit(ComplexChange visited);
    public T visit(MultiNodeObsoletion visited);
    public T visit(Transaction visited);
}