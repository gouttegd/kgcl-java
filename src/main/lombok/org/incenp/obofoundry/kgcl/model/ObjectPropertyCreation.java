package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ObjectPropertyCreation extends NodeCreation {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}