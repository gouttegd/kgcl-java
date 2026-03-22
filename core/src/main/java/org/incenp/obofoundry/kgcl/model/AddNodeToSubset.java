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

@LinkURI("http://w3id.org/kgcl/AddNodeToSubset")
public class AddNodeToSubset extends NodeChange {

    @SlotName("in_subset")
    @LinkURI("http://w3id.org/kgcl/in_subset")
    private OntologySubset inSubset;

    @LinkURI("http://w3id.org/kgcl/about")
    private OntologyElement about;

    public void setInSubset(OntologySubset inSubset) {
        this.inSubset = inSubset;
    }

    public OntologySubset getInSubset() {
        return this.inSubset;
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
        return "AddNodeToSubset(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof AddNodeToSubset) ) return false;
        final AddNodeToSubset other = (AddNodeToSubset) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$inSubset = this.getInSubset();
        final Object other$inSubset = other.getInSubset();
        if ( this$inSubset == null ? other$inSubset != null : !this$inSubset.equals(other$inSubset)) return false;
        final Object this$about = this.getAbout();
        final Object other$about = other.getAbout();
        if ( this$about == null ? other$about != null : !this$about.equals(other$about)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AddNodeToSubset;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $inSubset = this.getInSubset();
        result = result * PRIME + ($inSubset == null ? 43 : $inSubset.hashCode());
        final Object $about = this.getAbout();
        result = result * PRIME + ($about == null ? 43 : $about.hashCode());
        return result;
    }
}