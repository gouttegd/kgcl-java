package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NodeRename extends NodeChange {
    private TextualDiff hasTextualDiff;
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}