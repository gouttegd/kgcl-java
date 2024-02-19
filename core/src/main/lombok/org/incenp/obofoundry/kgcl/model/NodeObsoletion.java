package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Obsoletion of a node deprecates usage of that node, but does not delete it.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeObsoletion extends NodeChange {
    private Node hasDirectReplacement;
    private List<Node> hasNondirectReplacement;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}