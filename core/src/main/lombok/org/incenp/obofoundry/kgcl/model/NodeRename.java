package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node change where the name (aka rdfs:label) of the node changes
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeRename extends NodeChange {
    private TextualDiff hasTextualDiff;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}