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
 * An edge change where one node is replaced with another, as in the case of obsoletion with replacement
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class EdgeRewiring extends EdgeChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}