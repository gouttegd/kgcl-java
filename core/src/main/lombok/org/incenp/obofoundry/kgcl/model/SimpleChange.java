package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A change that is about a single ontology element
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SimpleChange extends Change {
    private String oldValue;
    private String newValue;
    private String oldValueType;
    private String newValueType;
    private String newLanguage;
    private String oldLanguage;
    private String newDatatype;
    private String oldDatatype;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}