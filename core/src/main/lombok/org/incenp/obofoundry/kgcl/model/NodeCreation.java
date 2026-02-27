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
 * a node change in which a new node is created
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NodeCreation extends NodeChange {
    @SlotName("node_id")
    private Node nodeId;
    private String name;
    @SlotName("owl_type")
    private OwlType owlType;
    @SlotName("annotation_set")
    private Annotation annotationSet;
    private OntologyElement about;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}