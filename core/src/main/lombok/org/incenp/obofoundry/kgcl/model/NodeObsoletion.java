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
 * Obsoletion of a node deprecates usage of that node, but does not delete it.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeObsoletion extends NodeChange {
    @SlotName("has_direct_replacement")
    private Node hasDirectReplacement;
    @SlotName("has_nondirect_replacement")
    private List<Node> hasNondirectReplacement;
    private OntologyElement about;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}