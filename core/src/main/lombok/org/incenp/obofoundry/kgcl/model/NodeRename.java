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
 * A node change where the name (aka rdfs:label) of the node changes
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeRename extends NodeChange {
    @SlotName("has_textual_diff")
    private TextualDiff hasTextualDiff;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}