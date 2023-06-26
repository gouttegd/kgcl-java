package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node change where the mappings for that node are altered
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeMappingChange extends NodeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}