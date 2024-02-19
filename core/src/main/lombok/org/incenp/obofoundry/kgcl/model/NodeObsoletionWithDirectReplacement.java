package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * An obsoletion change in which information from the obsoleted node is selectively copied to a single target, and edges can automatically be rewired to point to the target node
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeObsoletionWithDirectReplacement extends NodeObsoletion {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}