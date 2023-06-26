package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node annotation change where the change replaces a particular property value. TODO
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeAnnotationReplacement extends NodeAnnotationChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}