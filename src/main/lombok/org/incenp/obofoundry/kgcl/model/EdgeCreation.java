package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class EdgeCreation extends EdgeChange {
    private Node subject;
    private Node predicate;
    private Node object;
    private String subjectType;
    private String predicateType;
    private Annotation annotationSet;
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}