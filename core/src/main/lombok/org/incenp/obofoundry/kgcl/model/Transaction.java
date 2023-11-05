package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A change that is a composition of a set of changes, where those changes are treated as a single unit. Could be a single change, or the results of an ontology diff
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Transaction extends Change {
    private List<Change> changeSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}