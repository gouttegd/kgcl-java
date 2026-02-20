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
 * A node synonym where the name NAME of an node NODE moves to a synonym, and NODE receives a new name. This change consists of compose of (1) a node rename where NAME is replaced by a different name (2) a new synonym
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NameBecomesSynonym extends NodeSynonymChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}