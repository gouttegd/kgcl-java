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
 * A node metadata assertion change where the predicate of a metadata assertion is changed.
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class MetadataAssertionPredicateChange extends NodeMetadataAssertionChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}