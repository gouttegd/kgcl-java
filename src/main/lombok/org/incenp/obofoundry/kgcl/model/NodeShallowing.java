package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The opposite of node deepening
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeShallowing extends NodeMove {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}