package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node move is a combination of deleting a parent edge and adding a parent edge, where the predicate is preserved and the object/parent node changes
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeMove extends EdgeChange {
    private String oldObjectType;
    private String newObjectType;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}