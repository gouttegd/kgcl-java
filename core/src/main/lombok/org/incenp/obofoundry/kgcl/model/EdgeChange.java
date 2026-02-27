package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlined;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;

/**
 * A change in which the element that is the focus of the change is an edge.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class EdgeChange extends SimpleChange {
    @SlotName("about_edge")
    private Edge aboutEdge;
    @SlotName("object_type")
    private String objectType;
    private String language;
    private String datatype;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}