package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NodeCreation extends NodeChange {
    private Node nodeId;
    private String name;
    private String owlType;
    private Annotation annotationSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}