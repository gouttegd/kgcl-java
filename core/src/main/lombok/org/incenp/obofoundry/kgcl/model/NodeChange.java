package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlining;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;
import org.incenp.linkml.core.InliningMode;

/**
 * A simple change where the change is about a node
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeChange extends SimpleChange {
    @SlotName("about_node")
    private Node aboutNode;
    @SlotName("about_node_representation")
    private String aboutNodeRepresentation;
    private String language;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}