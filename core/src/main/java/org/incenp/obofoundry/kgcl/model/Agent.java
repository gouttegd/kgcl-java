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

@LinkURI("http://www.w3.org/ns/prov#Agent")
public class Agent extends ProvElement {

    @Identifier
    @Required
    @Converter(CurieConverter.class)
    @LinkURI("https://w3id.org/kgcl/basics/id")
    private String id;

    @SlotName("acted_on_behalf_of")
    @LinkURI("http://www.w3.org/ns/prov#actedOnBehalfOf")
    private Agent actedOnBehalfOf;

    @SlotName("was_informed_by")
    @LinkURI("http://www.w3.org/ns/prov#wasInformedBy")
    private Activity wasInformedBy;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setActedOnBehalfOf(Agent actedOnBehalfOf) {
        this.actedOnBehalfOf = actedOnBehalfOf;
    }

    public Agent getActedOnBehalfOf() {
        return this.actedOnBehalfOf;
    }

    public void setWasInformedBy(Activity wasInformedBy) {
        this.wasInformedBy = wasInformedBy;
    }

    public Activity getWasInformedBy() {
        return this.wasInformedBy;
    }

    @Override
    public String toString() {
        return "Agent(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Agent) ) return false;
        final Agent other = (Agent) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if ( this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$actedOnBehalfOf = this.getActedOnBehalfOf();
        final Object other$actedOnBehalfOf = other.getActedOnBehalfOf();
        if ( this$actedOnBehalfOf == null ? other$actedOnBehalfOf != null : !this$actedOnBehalfOf.equals(other$actedOnBehalfOf)) return false;
        final Object this$wasInformedBy = this.getWasInformedBy();
        final Object other$wasInformedBy = other.getWasInformedBy();
        if ( this$wasInformedBy == null ? other$wasInformedBy != null : !this$wasInformedBy.equals(other$wasInformedBy)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Agent;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $actedOnBehalfOf = this.getActedOnBehalfOf();
        result = result * PRIME + ($actedOnBehalfOf == null ? 43 : $actedOnBehalfOf.hashCode());
        final Object $wasInformedBy = this.getWasInformedBy();
        result = result * PRIME + ($wasInformedBy == null ? 43 : $wasInformedBy.hashCode());
        return result;
    }
}