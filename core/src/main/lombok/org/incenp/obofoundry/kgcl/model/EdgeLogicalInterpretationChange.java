package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An edge change where the subject, object, and predicate are unchanged, but the logical interpretation changes
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class EdgeLogicalInterpretationChange extends EdgeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}