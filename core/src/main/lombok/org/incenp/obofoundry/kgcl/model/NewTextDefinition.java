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
 * A node change where a de-novo text definition is created
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NewTextDefinition extends NodeTextDefinitionChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}