package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node synonym change where the predicate of a synonym is changed. Background: synonyms can be represented by a variety of predicates. For example, many OBO ontologies make use of predicates such as oio:hasExactSynonym, oio:hasRelatedSynonym, etc
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SynonymPredicateChange extends NodeSynonymChange {
    private TextualDiff hasTextualDiff;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}