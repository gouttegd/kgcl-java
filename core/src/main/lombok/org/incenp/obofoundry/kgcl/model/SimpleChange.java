package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlining;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.CurieConverter;
import org.incenp.linkml.core.InliningMode;

/**
 * A change that is about a single ontology element
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SimpleChange extends Change {
    @SlotName("old_value")
    private String oldValue;
    @SlotName("new_value")
    private String newValue;
    @SlotName("old_value_type")
    private String oldValueType;
    @SlotName("new_value_type")
    private String newValueType;
    @SlotName("new_language")
    private String newLanguage;
    @SlotName("old_language")
    private String oldLanguage;
    @SlotName("new_datatype")
    private String newDatatype;
    @SlotName("old_datatype")
    private String oldDatatype;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}