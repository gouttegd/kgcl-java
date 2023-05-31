package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NodeChange extends SimpleChange {
    private Node aboutNode;
    private String aboutNodeRepresentation;
    private String language;
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}