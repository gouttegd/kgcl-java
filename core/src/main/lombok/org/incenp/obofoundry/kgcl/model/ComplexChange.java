package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlined;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;

/**
 * A change that is is a composition of other changes
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class ComplexChange extends Change {
    @SlotName("change_set")
    @Inlined(asList = true)
    private List<Change> changeSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}