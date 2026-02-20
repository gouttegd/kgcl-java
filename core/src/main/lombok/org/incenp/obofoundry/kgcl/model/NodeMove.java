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
 * A node move is a combination of deleting a parent edge and adding a parent edge, where the predicate is preserved and the object/parent node changes
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeMove extends EdgeChange {
    @SlotName("old_object_type")
    private String oldObjectType;
    @SlotName("new_object_type")
    private String newObjectType;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}