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

@LinkURI("http://w3id.org/kgcl/NewSynonym")
public class NewSynonym extends NodeSynonymChange {

    @LinkURI("http://w3id.org/kgcl/qualifier")
    private String qualifier;

    @LinkURI("http://w3id.org/kgcl/om/predicate")
    private Node predicate;

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getQualifier() {
        return this.qualifier;
    }

    public void setPredicate(Node predicate) {
        this.predicate = predicate;
    }

    public Node getPredicate() {
        return this.predicate;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "NewSynonym(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof NewSynonym) ) return false;
        final NewSynonym other = (NewSynonym) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$qualifier = this.getQualifier();
        final Object other$qualifier = other.getQualifier();
        if ( this$qualifier == null ? other$qualifier != null : !this$qualifier.equals(other$qualifier)) return false;
        final Object this$predicate = this.getPredicate();
        final Object other$predicate = other.getPredicate();
        if ( this$predicate == null ? other$predicate != null : !this$predicate.equals(other$predicate)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NewSynonym;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $qualifier = this.getQualifier();
        result = result * PRIME + ($qualifier == null ? 43 : $qualifier.hashCode());
        final Object $predicate = this.getPredicate();
        result = result * PRIME + ($predicate == null ? 43 : $predicate.hashCode());
        return result;
    }
}