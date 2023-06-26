package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * None
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeSynonymChange extends NodeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}