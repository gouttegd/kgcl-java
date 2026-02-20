package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlining;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.CurieConverter;
import org.incenp.linkml.core.InliningMode;

/**
 * A node synonym change where the predicate of a synonym is changed. Background: synonyms can be represented by a variety of predicates. For example, many OBO ontologies make use of predicates such as oio:hasExactSynonym, oio:hasRelatedSynonym, etc
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SynonymPredicateChange extends NodeSynonymChange {
    @SlotName("has_textual_diff")
    private TextualDiff hasTextualDiff;
    private String target;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}