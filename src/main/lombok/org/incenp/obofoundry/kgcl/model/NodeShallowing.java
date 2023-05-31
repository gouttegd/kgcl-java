package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NodeShallowing extends NodeMove {
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}