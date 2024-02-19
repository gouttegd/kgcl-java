package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Places a node inside a subset, by annotating that node
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class AddNodeToSubset extends NodeChange {
    private OntologySubset inSubset;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}