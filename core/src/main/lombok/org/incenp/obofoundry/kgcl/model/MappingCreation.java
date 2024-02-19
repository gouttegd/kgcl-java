package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A specific kind of edge creation in which the created edge is a mapping.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class MappingCreation extends EdgeCreation {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}