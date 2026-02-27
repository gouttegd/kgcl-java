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
 * a provence-generating activity
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Activity extends ProvElement {
    @Identifier
    @Converter(CurieConverter.class)
    private String id;
    @SlotName("started_at_time")
    private String startedAtTime;
    @SlotName("ended_at_time")
    private String endedAtTime;
    @SlotName("was_informed_by")
    private Activity wasInformedBy;
    @SlotName("was_associated_with")
    private Agent wasAssociatedWith;
    private String used;
    private String description;}