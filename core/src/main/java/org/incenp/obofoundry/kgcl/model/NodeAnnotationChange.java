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

@LinkURI("http://w3id.org/kgcl/NodeAnnotationChange")
public class NodeAnnotationChange extends NodeChange {

    @SlotName("annotation_property")
    @LinkURI("http://w3id.org/kgcl/annotation_property")
    private String annotationProperty;

    @SlotName("annotation_property_type")
    @LinkURI("http://w3id.org/kgcl/annotation_property_type")
    private String annotationPropertyType;

    public void setAnnotationProperty(String annotationProperty) {
        this.annotationProperty = annotationProperty;
    }

    public String getAnnotationProperty() {
        return this.annotationProperty;
    }

    public void setAnnotationPropertyType(String annotationPropertyType) {
        this.annotationPropertyType = annotationPropertyType;
    }

    public String getAnnotationPropertyType() {
        return this.annotationPropertyType;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "NodeAnnotationChange(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof NodeAnnotationChange) ) return false;
        final NodeAnnotationChange other = (NodeAnnotationChange) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$annotationProperty = this.getAnnotationProperty();
        final Object other$annotationProperty = other.getAnnotationProperty();
        if ( this$annotationProperty == null ? other$annotationProperty != null : !this$annotationProperty.equals(other$annotationProperty)) return false;
        final Object this$annotationPropertyType = this.getAnnotationPropertyType();
        final Object other$annotationPropertyType = other.getAnnotationPropertyType();
        if ( this$annotationPropertyType == null ? other$annotationPropertyType != null : !this$annotationPropertyType.equals(other$annotationPropertyType)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NodeAnnotationChange;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $annotationProperty = this.getAnnotationProperty();
        result = result * PRIME + ($annotationProperty == null ? 43 : $annotationProperty.hashCode());
        final Object $annotationPropertyType = this.getAnnotationPropertyType();
        result = result * PRIME + ($annotationPropertyType == null ? 43 : $annotationPropertyType.hashCode());
        return result;
    }
}