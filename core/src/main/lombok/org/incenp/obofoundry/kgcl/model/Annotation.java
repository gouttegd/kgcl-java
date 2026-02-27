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
 * owl annotations. Not to be confused with annotations sensu GO
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class Annotation extends PropertyValue {
    @SlotName("annotation_set")
    private Annotation annotationSet;
    @SlotName("property_type")
    private String propertyType;
    @SlotName("filler_type")
    private String fillerType;}