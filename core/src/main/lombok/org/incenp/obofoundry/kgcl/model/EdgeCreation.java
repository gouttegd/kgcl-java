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
 * An edge change in which a de-novo edge is created. The edge is potentially annotated in the same action.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class EdgeCreation extends EdgeChange {
    private Node subject;
    private Node predicate;
    private Node object;
    @SlotName("subject_type")
    private String subjectType;
    @SlotName("predicate_type")
    private String predicateType;
    @SlotName("annotation_set")
    private Annotation annotationSet;
    private OntologyElement about;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}