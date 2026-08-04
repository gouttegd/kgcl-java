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

@LinkURI("http://www.w3.org/ns/prov#ProvElement")
public abstract class ProvElement {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("ProvElement(");
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof ProvElement) ) return false;
        final ProvElement other = (ProvElement) o;
        if ( !other.canEqual((Object) this)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ProvElement;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        return result;
    }
}