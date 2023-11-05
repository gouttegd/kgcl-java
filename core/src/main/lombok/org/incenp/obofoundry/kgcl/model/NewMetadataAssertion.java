package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node metadata assertion change where a metadata assertion is added to a node
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NewMetadataAssertion extends NodeMetadataAssertionChange {
    private Node object;
    private Node predicate;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}