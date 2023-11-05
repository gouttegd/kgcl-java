package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node change where the string value for the name is unchanged but the language tag is set
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SetLanguageForName extends NodeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}