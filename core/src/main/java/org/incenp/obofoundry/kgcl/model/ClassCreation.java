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

@LinkURI("http://w3id.org/kgcl/ClassCreation")
public class ClassCreation extends NodeCreation {

    @LinkURI("http://w3id.org/kgcl/superclass")
    private Node superclass;

    public void setSuperclass(Node superclass) {
        this.superclass = superclass;
    }

    public Node getSuperclass() {
        return this.superclass;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "ClassCreation(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof ClassCreation) ) return false;
        final ClassCreation other = (ClassCreation) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$superclass = this.getSuperclass();
        final Object other$superclass = other.getSuperclass();
        if ( this$superclass == null ? other$superclass != null : !this$superclass.equals(other$superclass)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ClassCreation;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $superclass = this.getSuperclass();
        result = result * PRIME + ($superclass == null ? 43 : $superclass.hashCode());
        return result;
    }
}