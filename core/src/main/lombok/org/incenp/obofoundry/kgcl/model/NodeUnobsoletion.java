package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * unobsoletion of a node deprecates usage of that node. Rarely applied.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeUnobsoletion extends NodeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}