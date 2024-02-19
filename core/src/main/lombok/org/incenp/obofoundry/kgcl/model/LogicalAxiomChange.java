package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A simple change where a logical axiom is changed, where the logical axiom cannot be represented as an edge
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class LogicalAxiomChange extends SimpleChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}