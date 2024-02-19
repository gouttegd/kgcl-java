package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An edge change in which an edge is removed. All edge annotations/properies are removed in the same action.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class EdgeDeletion extends EdgeChange {
    private Node subject;
    private Node predicate;
    private Node object;
    private String subjectType;
    private String predicateType;
    private Annotation annotationSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}