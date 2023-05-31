package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NodeObsoletion extends NodeChange {
    private Node hasDirectReplacement;
    private List<Node> hasNondirectReplacement;
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}