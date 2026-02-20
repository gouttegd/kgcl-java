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
 * Any named entity in an ontology. May be a class, individual, property
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Node extends OntologyElement {
    @Identifier
    @Converter(CurieConverter.class)
    private String id;
    private String name;
    @SlotName("annotation_set")
    private Annotation annotationSet;
    @SlotName("owl_type")
    private OwlType owlType;}