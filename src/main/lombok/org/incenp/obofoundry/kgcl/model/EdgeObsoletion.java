package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An edge change in which an edge is obsoleted.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class EdgeObsoletion extends EdgeChange {
    private Node subject;
    private Node predicate;
    private Node object;
    private Annotation annotationSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}