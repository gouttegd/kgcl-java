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
 * A simple change where a logical axiom is changed, where the logical axiom cannot be represented as an edge
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class LogicalAxiomChange extends SimpleChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}