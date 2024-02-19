package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An edge deletion where the predicate is owl:subClassOf
  
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class RemoveUnder extends EdgeDeletion {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}