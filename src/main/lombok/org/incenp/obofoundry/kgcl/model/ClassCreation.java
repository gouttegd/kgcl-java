package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ClassCreation extends NodeCreation {
    private Node superclass;
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}