package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node synonym change where the text of a synonym is changed
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SynonymReplacement extends NodeSynonymChange {
    private TextualDiff hasTextualDiff;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}