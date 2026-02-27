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
 * Removes a node from a subset, by removing an annotation
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class RemoveNodeFromSubset extends NodeChange {
    @SlotName("in_subset")
    private OntologySubset inSubset;
    private OntologyElement about;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}