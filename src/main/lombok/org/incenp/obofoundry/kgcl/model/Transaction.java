package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Transaction extends Change {
    private List<Change> changeSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}