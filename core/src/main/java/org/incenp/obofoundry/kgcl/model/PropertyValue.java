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

@LinkURI("http://w3id.org/kgcl/om/PropertyValue")
public class PropertyValue extends OntologyElement {

    @LinkURI("http://w3id.org/kgcl/om/property")
    private Node property;

    @LinkURI("http://w3id.org/kgcl/om/filler")
    private String filler;

    public void setProperty(Node property) {
        this.property = property;
    }

    public Node getProperty() {
        return this.property;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    public String getFiller() {
        return this.filler;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("PropertyValue(");
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
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof PropertyValue) ) return false;
        final PropertyValue other = (PropertyValue) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$property = this.getProperty();
        final Object other$property = other.getProperty();
        if ( this$property == null ? other$property != null : !this$property.equals(other$property)) return false;
        final Object this$filler = this.getFiller();
        final Object other$filler = other.getFiller();
        if ( this$filler == null ? other$filler != null : !this$filler.equals(other$filler)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof PropertyValue;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $property = this.getProperty();
        result = result * PRIME + ($property == null ? 43 : $property.hashCode());
        final Object $filler = this.getFiller();
        result = result * PRIME + ($filler == null ? 43 : $filler.hashCode());
        return result;
    }
}