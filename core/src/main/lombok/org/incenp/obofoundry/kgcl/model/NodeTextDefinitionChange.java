package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node change where the text definition is changed
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeTextDefinitionChange extends NodeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}