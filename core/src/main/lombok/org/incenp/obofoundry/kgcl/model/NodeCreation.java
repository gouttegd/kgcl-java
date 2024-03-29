package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * a node change in which a new node is created
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeCreation extends NodeChange {
    private Node nodeId;
    private String name;
    private OwlType owlType;
    private Annotation annotationSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}
