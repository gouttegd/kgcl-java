package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlining;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;
import org.incenp.linkml.core.InliningMode;

/**
 * A change that is a composition of a set of changes, where those changes are treated as a single unit. Could be a single change, or the results of an ontology diff
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class Transaction extends Change {
    @SlotName("change_set")
    @Inlining(InliningMode.LIST)
    private List<Change> changeSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}