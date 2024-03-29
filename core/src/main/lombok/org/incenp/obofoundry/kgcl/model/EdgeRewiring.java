package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An edge change where one node is replaced with another, as in the case of obsoletion with replacement
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class EdgeRewiring extends EdgeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}