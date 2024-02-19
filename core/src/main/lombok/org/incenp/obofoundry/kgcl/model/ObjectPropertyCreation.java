package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node creation where the owl type is &#39;ObjectProperty&#39;
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ObjectPropertyCreation extends NodeCreation {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}