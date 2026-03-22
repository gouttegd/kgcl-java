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

@LinkURI("http://w3id.org/kgcl/SubsetMembershipChange")
public class SubsetMembershipChange extends ChangeMixin {

    @SlotName("in_subset")
    @LinkURI("http://w3id.org/kgcl/in_subset")
    private OntologySubset inSubset;

    public void setInSubset(OntologySubset inSubset) {
        this.inSubset = inSubset;
    }

    public OntologySubset getInSubset() {
        return this.inSubset;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("SubsetMembershipChange(");
        if ( (o = this.getInSubset()) != null ) {
            sb.append("in_subset=");
            sb.append(o);
            sb.append(",");
        }
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
        if ( !(o instanceof SubsetMembershipChange) ) return false;
        final SubsetMembershipChange other = (SubsetMembershipChange) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$inSubset = this.getInSubset();
        final Object other$inSubset = other.getInSubset();
        if ( this$inSubset == null ? other$inSubset != null : !this$inSubset.equals(other$inSubset)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SubsetMembershipChange;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $inSubset = this.getInSubset();
        result = result * PRIME + ($inSubset == null ? 43 : $inSubset.hashCode());
        return result;
    }
}