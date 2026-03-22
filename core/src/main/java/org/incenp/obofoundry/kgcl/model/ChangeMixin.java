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

@LinkURI("http://w3id.org/kgcl/ChangeMixin")
public class ChangeMixin {

    @LinkURI("http://w3id.org/kgcl/about")
    private OntologyElement about;

    @SlotName("has_undo")
    @LinkURI("http://w3id.org/kgcl/has_undo")
    private Change hasUndo;

    @SlotName("old_value")
    @LinkURI("http://w3id.org/kgcl/old_value")
    private String oldValue;

    @SlotName("new_value")
    @LinkURI("http://w3id.org/kgcl/new_value")
    private String newValue;

    public void setAbout(OntologyElement about) {
        this.about = about;
    }

    public OntologyElement getAbout() {
        return this.about;
    }

    public void setHasUndo(Change hasUndo) {
        this.hasUndo = hasUndo;
    }

    public Change getHasUndo() {
        return this.hasUndo;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getNewValue() {
        return this.newValue;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("ChangeMixin(");
        if ( (o = this.getAbout()) != null ) {
            sb.append("about=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getHasUndo()) != null ) {
            sb.append("has_undo=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getOldValue()) != null ) {
            sb.append("old_value=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getNewValue()) != null ) {
            sb.append("new_value=");
            sb.append(o);
            sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof ChangeMixin) ) return false;
        final ChangeMixin other = (ChangeMixin) o;
        if ( !other.canEqual((Object) this)) return false;
        final Object this$about = this.getAbout();
        final Object other$about = other.getAbout();
        if ( this$about == null ? other$about != null : !this$about.equals(other$about)) return false;
        final Object this$hasUndo = this.getHasUndo();
        final Object other$hasUndo = other.getHasUndo();
        if ( this$hasUndo == null ? other$hasUndo != null : !this$hasUndo.equals(other$hasUndo)) return false;
        final Object this$oldValue = this.getOldValue();
        final Object other$oldValue = other.getOldValue();
        if ( this$oldValue == null ? other$oldValue != null : !this$oldValue.equals(other$oldValue)) return false;
        final Object this$newValue = this.getNewValue();
        final Object other$newValue = other.getNewValue();
        if ( this$newValue == null ? other$newValue != null : !this$newValue.equals(other$newValue)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangeMixin;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $about = this.getAbout();
        result = result * PRIME + ($about == null ? 43 : $about.hashCode());
        final Object $hasUndo = this.getHasUndo();
        result = result * PRIME + ($hasUndo == null ? 43 : $hasUndo.hashCode());
        final Object $oldValue = this.getOldValue();
        result = result * PRIME + ($oldValue == null ? 43 : $oldValue.hashCode());
        final Object $newValue = this.getNewValue();
        result = result * PRIME + ($newValue == null ? 43 : $newValue.hashCode());
        return result;
    }
}