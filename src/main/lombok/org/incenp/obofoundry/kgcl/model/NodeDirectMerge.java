package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An obsoletion change in which all metadata (including name/label) from the source node is deleted and added to the target node, and edges can automatically be rewired to point to the target node
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeDirectMerge extends NodeObsoletion {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}