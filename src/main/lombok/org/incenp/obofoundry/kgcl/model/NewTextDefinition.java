package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node change where a de-novo text definition is created
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NewTextDefinition extends NodeTextDefinitionChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}