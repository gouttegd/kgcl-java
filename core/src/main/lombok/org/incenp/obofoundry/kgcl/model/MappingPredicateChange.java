package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node mapping change where the predicate of a mapping is changed.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class MappingPredicateChange extends NodeMappingChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}