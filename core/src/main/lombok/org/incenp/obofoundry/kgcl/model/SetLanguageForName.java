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
 * A node change where the string value for the name is unchanged but the language tag is set
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class SetLanguageForName extends NodeChange {
    private OntologyElement about;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}