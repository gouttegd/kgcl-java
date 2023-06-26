package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Removes a node from a subset, by removing an annotation
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RemoveNodeFromSubset extends NodeChange {
    private OntologySubset inSubset;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}