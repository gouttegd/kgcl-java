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

@LinkURI("http://w3id.org/kgcl/NodeCreation")
public class NodeCreation extends NodeChange {

    @SlotName("node_id")
    @LinkURI("http://w3id.org/kgcl/node_id")
    private Node nodeId;

    @LinkURI("http://w3id.org/kgcl/om/name")
    private String name;

    @SlotName("owl_type")
    @LinkURI("http://w3id.org/kgcl/om/owl_type")
    private OwlType owlType;

    @SlotName("annotation_set")
    @LinkURI("http://w3id.org/kgcl/om/annotation_set")
    private Annotation annotationSet;

    @LinkURI("http://w3id.org/kgcl/about")
    private OntologyElement about;

    public void setNodeId(Node nodeId) {
        this.nodeId = nodeId;
    }

    public Node getNodeId() {
        return this.nodeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setOwlType(OwlType owlType) {
        this.owlType = owlType;
    }

    public OwlType getOwlType() {
        return this.owlType;
    }

    public void setAnnotationSet(Annotation annotationSet) {
        this.annotationSet = annotationSet;
    }

    public Annotation getAnnotationSet() {
        return this.annotationSet;
    }

    public void setAbout(OntologyElement about) {
        this.about = about;
    }

    public OntologyElement getAbout() {
        return this.about;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "NodeCreation(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof NodeCreation) ) return false;
        final NodeCreation other = (NodeCreation) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$nodeId = this.getNodeId();
        final Object other$nodeId = other.getNodeId();
        if ( this$nodeId == null ? other$nodeId != null : !this$nodeId.equals(other$nodeId)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if ( this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final Object this$owlType = this.getOwlType();
        final Object other$owlType = other.getOwlType();
        if ( this$owlType == null ? other$owlType != null : !this$owlType.equals(other$owlType)) return false;
        final Object this$annotationSet = this.getAnnotationSet();
        final Object other$annotationSet = other.getAnnotationSet();
        if ( this$annotationSet == null ? other$annotationSet != null : !this$annotationSet.equals(other$annotationSet)) return false;
        final Object this$about = this.getAbout();
        final Object other$about = other.getAbout();
        if ( this$about == null ? other$about != null : !this$about.equals(other$about)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NodeCreation;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $nodeId = this.getNodeId();
        result = result * PRIME + ($nodeId == null ? 43 : $nodeId.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $owlType = this.getOwlType();
        result = result * PRIME + ($owlType == null ? 43 : $owlType.hashCode());
        final Object $annotationSet = this.getAnnotationSet();
        result = result * PRIME + ($annotationSet == null ? 43 : $annotationSet.hashCode());
        final Object $about = this.getAbout();
        result = result * PRIME + ($about == null ? 43 : $about.hashCode());
        return result;
    }
}