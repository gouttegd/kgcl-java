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

@LinkURI("http://w3id.org/kgcl/EdgeChange")
public abstract class EdgeChange extends SimpleChange {

    @SlotName("about_edge")
    @LinkURI("http://w3id.org/kgcl/about_edge")
    private Edge aboutEdge;

    @SlotName("object_type")
    @LinkURI("http://w3id.org/kgcl/object_type")
    private String objectType;

    @LinkURI("http://w3id.org/kgcl/language")
    private String language;

    @LinkURI("http://w3id.org/kgcl/datatype")
    private String datatype;

    public void setAboutEdge(Edge aboutEdge) {
        this.aboutEdge = aboutEdge;
    }

    public Edge getAboutEdge() {
        return this.aboutEdge;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDatatype() {
        return this.datatype;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "EdgeChange(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof EdgeChange) ) return false;
        final EdgeChange other = (EdgeChange) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$aboutEdge = this.getAboutEdge();
        final Object other$aboutEdge = other.getAboutEdge();
        if ( this$aboutEdge == null ? other$aboutEdge != null : !this$aboutEdge.equals(other$aboutEdge)) return false;
        final Object this$objectType = this.getObjectType();
        final Object other$objectType = other.getObjectType();
        if ( this$objectType == null ? other$objectType != null : !this$objectType.equals(other$objectType)) return false;
        final Object this$language = this.getLanguage();
        final Object other$language = other.getLanguage();
        if ( this$language == null ? other$language != null : !this$language.equals(other$language)) return false;
        final Object this$datatype = this.getDatatype();
        final Object other$datatype = other.getDatatype();
        if ( this$datatype == null ? other$datatype != null : !this$datatype.equals(other$datatype)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EdgeChange;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $aboutEdge = this.getAboutEdge();
        result = result * PRIME + ($aboutEdge == null ? 43 : $aboutEdge.hashCode());
        final Object $objectType = this.getObjectType();
        result = result * PRIME + ($objectType == null ? 43 : $objectType.hashCode());
        final Object $language = this.getLanguage();
        result = result * PRIME + ($language == null ? 43 : $language.hashCode());
        final Object $datatype = this.getDatatype();
        result = result * PRIME + ($datatype == null ? 43 : $datatype.hashCode());
        return result;
    }
}