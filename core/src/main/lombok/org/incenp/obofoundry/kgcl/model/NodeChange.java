package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A simple change where the change is about a node
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeChange extends SimpleChange {
    private Node aboutNode;
    private String aboutNodeRepresentation;
    private String language;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}