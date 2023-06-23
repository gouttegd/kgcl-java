package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NodeObsoletionWithDirectReplacement extends NodeObsoletion {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}