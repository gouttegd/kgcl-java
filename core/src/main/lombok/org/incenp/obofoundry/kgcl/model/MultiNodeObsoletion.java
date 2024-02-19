package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A complex change consisting of multiple obsoletions.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class MultiNodeObsoletion extends ComplexChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}