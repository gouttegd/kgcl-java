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
 * A node change where the change alters node properties/annotations. TODO
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeAnnotationChange extends NodeChange {
    @SlotName("annotation_property")
    private String annotationProperty;
    @SlotName("annotation_property_type")
    private String annotationPropertyType;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}