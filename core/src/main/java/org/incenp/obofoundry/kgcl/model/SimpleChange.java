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

@LinkURI("http://w3id.org/kgcl/SimpleChange")
@Converter(SimpleChangeConverter.class)
public abstract class SimpleChange extends Change {

    @SlotName("old_value")
    @LinkURI("http://w3id.org/kgcl/old_value")
    private String oldValue;

    @SlotName("new_value")
    @LinkURI("http://w3id.org/kgcl/new_value")
    private String newValue;

    @SlotName("old_value_type")
    @LinkURI("http://w3id.org/kgcl/old_value_type")
    private String oldValueType;

    @SlotName("new_value_type")
    @LinkURI("http://w3id.org/kgcl/new_value_type")
    private String newValueType;

    @SlotName("new_language")
    @LinkURI("http://w3id.org/kgcl/new_language")
    private String newLanguage;

    @SlotName("old_language")
    @LinkURI("http://w3id.org/kgcl/old_language")
    private String oldLanguage;

    @SlotName("new_datatype")
    @LinkURI("http://w3id.org/kgcl/new_datatype")
    private String newDatatype;

    @SlotName("old_datatype")
    @LinkURI("http://w3id.org/kgcl/old_datatype")
    private String oldDatatype;

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

    public void setOldValueType(String oldValueType) {
        this.oldValueType = oldValueType;
    }

    public String getOldValueType() {
        return this.oldValueType;
    }

    public void setNewValueType(String newValueType) {
        this.newValueType = newValueType;
    }

    public String getNewValueType() {
        return this.newValueType;
    }

    public void setNewLanguage(String newLanguage) {
        this.newLanguage = newLanguage;
    }

    public String getNewLanguage() {
        return this.newLanguage;
    }

    public void setOldLanguage(String oldLanguage) {
        this.oldLanguage = oldLanguage;
    }

    public String getOldLanguage() {
        return this.oldLanguage;
    }

    public void setNewDatatype(String newDatatype) {
        this.newDatatype = newDatatype;
    }

    public String getNewDatatype() {
        return this.newDatatype;
    }

    public void setOldDatatype(String oldDatatype) {
        this.oldDatatype = oldDatatype;
    }

    public String getOldDatatype() {
        return this.oldDatatype;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "SimpleChange(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof SimpleChange) ) return false;
        final SimpleChange other = (SimpleChange) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$oldValue = this.getOldValue();
        final Object other$oldValue = other.getOldValue();
        if ( this$oldValue == null ? other$oldValue != null : !this$oldValue.equals(other$oldValue)) return false;
        final Object this$newValue = this.getNewValue();
        final Object other$newValue = other.getNewValue();
        if ( this$newValue == null ? other$newValue != null : !this$newValue.equals(other$newValue)) return false;
        final Object this$oldValueType = this.getOldValueType();
        final Object other$oldValueType = other.getOldValueType();
        if ( this$oldValueType == null ? other$oldValueType != null : !this$oldValueType.equals(other$oldValueType)) return false;
        final Object this$newValueType = this.getNewValueType();
        final Object other$newValueType = other.getNewValueType();
        if ( this$newValueType == null ? other$newValueType != null : !this$newValueType.equals(other$newValueType)) return false;
        final Object this$newLanguage = this.getNewLanguage();
        final Object other$newLanguage = other.getNewLanguage();
        if ( this$newLanguage == null ? other$newLanguage != null : !this$newLanguage.equals(other$newLanguage)) return false;
        final Object this$oldLanguage = this.getOldLanguage();
        final Object other$oldLanguage = other.getOldLanguage();
        if ( this$oldLanguage == null ? other$oldLanguage != null : !this$oldLanguage.equals(other$oldLanguage)) return false;
        final Object this$newDatatype = this.getNewDatatype();
        final Object other$newDatatype = other.getNewDatatype();
        if ( this$newDatatype == null ? other$newDatatype != null : !this$newDatatype.equals(other$newDatatype)) return false;
        final Object this$oldDatatype = this.getOldDatatype();
        final Object other$oldDatatype = other.getOldDatatype();
        if ( this$oldDatatype == null ? other$oldDatatype != null : !this$oldDatatype.equals(other$oldDatatype)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SimpleChange;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $oldValue = this.getOldValue();
        result = result * PRIME + ($oldValue == null ? 43 : $oldValue.hashCode());
        final Object $newValue = this.getNewValue();
        result = result * PRIME + ($newValue == null ? 43 : $newValue.hashCode());
        final Object $oldValueType = this.getOldValueType();
        result = result * PRIME + ($oldValueType == null ? 43 : $oldValueType.hashCode());
        final Object $newValueType = this.getNewValueType();
        result = result * PRIME + ($newValueType == null ? 43 : $newValueType.hashCode());
        final Object $newLanguage = this.getNewLanguage();
        result = result * PRIME + ($newLanguage == null ? 43 : $newLanguage.hashCode());
        final Object $oldLanguage = this.getOldLanguage();
        result = result * PRIME + ($oldLanguage == null ? 43 : $oldLanguage.hashCode());
        final Object $newDatatype = this.getNewDatatype();
        result = result * PRIME + ($newDatatype == null ? 43 : $newDatatype.hashCode());
        final Object $oldDatatype = this.getOldDatatype();
        result = result * PRIME + ($oldDatatype == null ? 43 : $oldDatatype.hashCode());
        return result;
    }
}