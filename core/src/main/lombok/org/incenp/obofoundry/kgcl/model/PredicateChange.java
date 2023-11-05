package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An edge change where the predicate (relationship type) is modified.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PredicateChange extends EdgeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}