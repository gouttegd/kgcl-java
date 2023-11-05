package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node creation where the owl type is &#39;owl:Class&#39;
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ClassCreation extends NodeCreation {
    private Node superclass;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}