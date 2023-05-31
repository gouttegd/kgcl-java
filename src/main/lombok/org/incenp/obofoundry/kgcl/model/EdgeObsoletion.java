package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class EdgeObsoletion extends EdgeChange {
    private Node subject;
    private Node predicate;
    private Node object;
    private Annotation annotationSet;
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}