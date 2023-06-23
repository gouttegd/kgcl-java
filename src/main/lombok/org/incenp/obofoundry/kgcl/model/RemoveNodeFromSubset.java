package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class RemoveNodeFromSubset extends NodeChange {
    private OntologySubset inSubset;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}