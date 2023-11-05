package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An edge creation where the predicate is owl:subClassOf
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PlaceUnder extends EdgeCreation {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}