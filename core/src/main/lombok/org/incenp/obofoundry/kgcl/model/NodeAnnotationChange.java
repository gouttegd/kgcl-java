package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node change where the change alters node properties/annotations. TODO
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class NodeAnnotationChange extends NodeChange {
    private String annotationProperty;
    private String annotationPropertyType;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}