package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node synonym change where a synonym is deleted
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class RemoveSynonym extends NodeSynonymChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}