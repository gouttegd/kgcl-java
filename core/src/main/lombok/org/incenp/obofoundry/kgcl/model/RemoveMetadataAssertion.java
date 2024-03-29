package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node metadata assertion change where a metadata assertion is deleted
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class RemoveMetadataAssertion extends NodeMetadataAssertionChange {
    private Node object;
    private Node predicate;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}