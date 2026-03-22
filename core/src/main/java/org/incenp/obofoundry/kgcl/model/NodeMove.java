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

@LinkURI("http://w3id.org/kgcl/NodeMove")
public class NodeMove extends EdgeChange {

    @SlotName("old_object_type")
    @LinkURI("http://w3id.org/kgcl/old_object_type")
    private String oldObjectType;

    @SlotName("new_object_type")
    @LinkURI("http://w3id.org/kgcl/new_object_type")
    private String newObjectType;

    public void setOldObjectType(String oldObjectType) {
        this.oldObjectType = oldObjectType;
    }

    public String getOldObjectType() {
        return this.oldObjectType;
    }

    public void setNewObjectType(String newObjectType) {
        this.newObjectType = newObjectType;
    }

    public String getNewObjectType() {
        return this.newObjectType;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "NodeMove(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof NodeMove) ) return false;
        final NodeMove other = (NodeMove) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$oldObjectType = this.getOldObjectType();
        final Object other$oldObjectType = other.getOldObjectType();
        if ( this$oldObjectType == null ? other$oldObjectType != null : !this$oldObjectType.equals(other$oldObjectType)) return false;
        final Object this$newObjectType = this.getNewObjectType();
        final Object other$newObjectType = other.getNewObjectType();
        if ( this$newObjectType == null ? other$newObjectType != null : !this$newObjectType.equals(other$newObjectType)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NodeMove;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $oldObjectType = this.getOldObjectType();
        result = result * PRIME + ($oldObjectType == null ? 43 : $oldObjectType.hashCode());
        final Object $newObjectType = this.getNewObjectType();
        result = result * PRIME + ($newObjectType == null ? 43 : $newObjectType.hashCode());
        return result;
    }
}