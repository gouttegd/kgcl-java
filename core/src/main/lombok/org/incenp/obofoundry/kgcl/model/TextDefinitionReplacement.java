package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node change where a text definition is modified
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class TextDefinitionReplacement extends NodeTextDefinitionChange {
    private TextualDiff hasTextualDiff;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}