package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Deletion of a node from the graph. Note it is recommended nodes are obsoleted and never merged, but this operation exists to represent deletions in ontologies, accidental or otherwise
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeDeletion extends NodeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}