package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class EdgeChange extends SimpleChange {
    private Edge aboutEdge;
    private String objectType;
    private String language;
    private String datatype;
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}