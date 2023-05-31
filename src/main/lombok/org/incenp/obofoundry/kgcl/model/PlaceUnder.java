package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class PlaceUnder extends EdgeCreation {
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}