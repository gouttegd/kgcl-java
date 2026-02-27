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
 * a provence-generating agent
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Agent extends ProvElement {
    @Identifier
    @Converter(CurieConverter.class)
    private String id;
    @SlotName("acted_on_behalf_of")
    private Agent actedOnBehalfOf;
    @SlotName("was_informed_by")
    private Activity wasInformedBy;}