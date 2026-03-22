package org.incenp.obofoundry.kgcl.model;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.ExtensionHolder;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlined;
import org.incenp.linkml.core.annotations.LinkURI;
import org.incenp.linkml.core.annotations.Required;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;

import org.incenp.obofoundry.kgcl.SimpleChangeConverter;

@LinkURI("http://w3id.org/kgcl/om/Node")
public class Node extends OntologyElement {

    @Identifier
    @Converter(CurieConverter.class)
    @LinkURI("https://w3id.org/kgcl/basics/id")
    private String id;

    @LinkURI("http://w3id.org/kgcl/om/name")
    private String name;

    @SlotName("annotation_set")
    @LinkURI("http://w3id.org/kgcl/om/annotation_set")
    private Annotation annotationSet;

    @SlotName("owl_type")
    @LinkURI("http://w3id.org/kgcl/om/owl_type")
    private OwlType owlType;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setAnnotationSet(Annotation annotationSet) {
        this.annotationSet = annotationSet;
    }

    public Annotation getAnnotationSet() {
        return this.annotationSet;
    }

    public void setOwlType(OwlType owlType) {
        this.owlType = owlType;
    }

    public OwlType getOwlType() {
        return this.owlType;
    }

    @Override
    public String toString() {
        return "Node(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Node) ) return false;
        final Node other = (Node) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if ( this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if ( this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$annotationSet = this.getAnnotationSet();
        final Object other$annotationSet = other.getAnnotationSet();
        if ( this$annotationSet == null ? other$annotationSet != null : !this$annotationSet.equals(other$annotationSet)) return false;
        final Object this$owlType = this.getOwlType();
        final Object other$owlType = other.getOwlType();
        if ( this$owlType == null ? other$owlType != null : !this$owlType.equals(other$owlType)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Node;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $annotationSet = this.getAnnotationSet();
        result = result * PRIME + ($annotationSet == null ? 43 : $annotationSet.hashCode());
        final Object $owlType = this.getOwlType();
        result = result * PRIME + ($owlType == null ? 43 : $owlType.hashCode());
        return result;
    }
}