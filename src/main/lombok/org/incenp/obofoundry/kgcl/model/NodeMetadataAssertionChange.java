package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node change where the metadata assertion (OWL annotations) for that node are altered
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeMetadataAssertionChange extends NodeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}