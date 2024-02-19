package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A change that is is a composition of other changes
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ComplexChange extends Change {
    private List<Change> changeSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}