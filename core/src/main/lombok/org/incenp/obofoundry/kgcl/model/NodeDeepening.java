package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node move in which a node where the destination is a proper descendant of the original location. Note that here descendant applied not just to subclass, but edges of any predicate in the relational graph
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeDeepening extends NodeMove {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}