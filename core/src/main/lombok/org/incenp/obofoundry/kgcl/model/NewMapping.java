package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node mapping change where a mapping is added to a node
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NewMapping extends NodeMappingChange {
    private Node object;
    private Node predicate;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}