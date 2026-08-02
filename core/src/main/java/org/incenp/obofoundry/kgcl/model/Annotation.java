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

@LinkURI("http://w3id.org/kgcl/om/Annotation")
public class Annotation extends PropertyValue {

    @SlotName("annotation_set")
    @LinkURI("http://w3id.org/kgcl/om/annotation_set")
    private Annotation annotationSet;

    @SlotName("property_type")
    @LinkURI("http://w3id.org/kgcl/om/property_type")
    private String propertyType;

    @SlotName("filler_type")
    @LinkURI("http://w3id.org/kgcl/om/filler_type")
    private String fillerType;

    public void setAnnotationSet(Annotation annotationSet) {
        this.annotationSet = annotationSet;
    }

    public Annotation getAnnotationSet() {
        return this.annotationSet;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPropertyType() {
        return this.propertyType;
    }

    public void setFillerType(String fillerType) {
        this.fillerType = fillerType;
    }

    public String getFillerType() {
        return this.fillerType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("Annotation(");
        if ( (o = this.getProperty()) != null ) {
            sb.append("property=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getFiller()) != null ) {
            sb.append("filler=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getAnnotationSet()) != null ) {
            sb.append("annotation_set=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getPropertyType()) != null ) {
            sb.append("property_type=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getFillerType()) != null ) {
            sb.append("filler_type=");
            sb.append(o);
            sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Annotation) ) return false;
        final Annotation other = (Annotation) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$annotationSet = this.getAnnotationSet();
        final Object other$annotationSet = other.getAnnotationSet();
        if ( this$annotationSet == null ? other$annotationSet != null : !this$annotationSet.equals(other$annotationSet)) return false;
        final Object this$propertyType = this.getPropertyType();
        final Object other$propertyType = other.getPropertyType();
        if ( this$propertyType == null ? other$propertyType != null : !this$propertyType.equals(other$propertyType)) return false;
        final Object this$fillerType = this.getFillerType();
        final Object other$fillerType = other.getFillerType();
        if ( this$fillerType == null ? other$fillerType != null : !this$fillerType.equals(other$fillerType)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Annotation;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $annotationSet = this.getAnnotationSet();
        result = result * PRIME + ($annotationSet == null ? 43 : $annotationSet.hashCode());
        final Object $propertyType = this.getPropertyType();
        result = result * PRIME + ($propertyType == null ? 43 : $propertyType.hashCode());
        final Object $fillerType = this.getFillerType();
        result = result * PRIME + ($fillerType == null ? 43 : $fillerType.hashCode());
        return result;
    }
}