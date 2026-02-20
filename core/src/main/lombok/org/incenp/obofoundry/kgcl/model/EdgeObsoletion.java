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
 * An edge change in which an edge is obsoleted.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class EdgeObsoletion extends EdgeChange {
    private Node subject;
    private Node predicate;
    private Node object;
    @SlotName("annotation_set")
    private Annotation annotationSet;
    private OntologyElement about;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}