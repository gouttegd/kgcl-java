package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node synonym change where a de-novo synonym is created
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NewSynonym extends NodeSynonymChange {
    private String qualifier;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}