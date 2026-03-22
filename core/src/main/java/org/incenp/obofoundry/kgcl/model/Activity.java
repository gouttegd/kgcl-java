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

@LinkURI("http://www.w3.org/ns/prov#Activity")
public class Activity extends ProvElement {

    @Identifier
    @Converter(CurieConverter.class)
    @LinkURI("https://w3id.org/kgcl/basics/id")
    private String id;

    @SlotName("started_at_time")
    @LinkURI("http://www.w3.org/ns/prov#startedAtTime")
    private String startedAtTime;

    @SlotName("ended_at_time")
    @LinkURI("http://www.w3.org/ns/prov#endedAtTime")
    private String endedAtTime;

    @SlotName("was_informed_by")
    @LinkURI("http://www.w3.org/ns/prov#wasInformedBy")
    private Activity wasInformedBy;

    @SlotName("was_associated_with")
    @LinkURI("http://www.w3.org/ns/prov#wasAssociatedWith")
    private Agent wasAssociatedWith;

    @LinkURI("http://www.w3.org/ns/prov#used")
    private String used;

    @LinkURI("http://purl.org/dc/terms/description")
    private String description;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setStartedAtTime(String startedAtTime) {
        this.startedAtTime = startedAtTime;
    }

    public String getStartedAtTime() {
        return this.startedAtTime;
    }

    public void setEndedAtTime(String endedAtTime) {
        this.endedAtTime = endedAtTime;
    }

    public String getEndedAtTime() {
        return this.endedAtTime;
    }

    public void setWasInformedBy(Activity wasInformedBy) {
        this.wasInformedBy = wasInformedBy;
    }

    public Activity getWasInformedBy() {
        return this.wasInformedBy;
    }

    public void setWasAssociatedWith(Agent wasAssociatedWith) {
        this.wasAssociatedWith = wasAssociatedWith;
    }

    public Agent getWasAssociatedWith() {
        return this.wasAssociatedWith;
    }

    public void setUsed(String used) {
        this.used = used;
    }

    public String getUsed() {
        return this.used;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return "Activity(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Activity) ) return false;
        final Activity other = (Activity) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if ( this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$startedAtTime = this.getStartedAtTime();
        final Object other$startedAtTime = other.getStartedAtTime();
        if ( this$startedAtTime == null ? other$startedAtTime != null : !this$startedAtTime.equals(other$startedAtTime)) return false;
        final Object this$endedAtTime = this.getEndedAtTime();
        final Object other$endedAtTime = other.getEndedAtTime();
        if ( this$endedAtTime == null ? other$endedAtTime != null : !this$endedAtTime.equals(other$endedAtTime)) return false;
        final Object this$wasInformedBy = this.getWasInformedBy();
        final Object other$wasInformedBy = other.getWasInformedBy();
        if ( this$wasInformedBy == null ? other$wasInformedBy != null : !this$wasInformedBy.equals(other$wasInformedBy)) return false;
        final Object this$wasAssociatedWith = this.getWasAssociatedWith();
        final Object other$wasAssociatedWith = other.getWasAssociatedWith();
        if ( this$wasAssociatedWith == null ? other$wasAssociatedWith != null : !this$wasAssociatedWith.equals(other$wasAssociatedWith)) return false;
        final Object this$used = this.getUsed();
        final Object other$used = other.getUsed();
        if ( this$used == null ? other$used != null : !this$used.equals(other$used)) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if ( this$description == null ? other$description != null : !this$description.equals(other$description)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Activity;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $startedAtTime = this.getStartedAtTime();
        result = result * PRIME + ($startedAtTime == null ? 43 : $startedAtTime.hashCode());
        final Object $endedAtTime = this.getEndedAtTime();
        result = result * PRIME + ($endedAtTime == null ? 43 : $endedAtTime.hashCode());
        final Object $wasInformedBy = this.getWasInformedBy();
        result = result * PRIME + ($wasInformedBy == null ? 43 : $wasInformedBy.hashCode());
        final Object $wasAssociatedWith = this.getWasAssociatedWith();
        result = result * PRIME + ($wasAssociatedWith == null ? 43 : $wasAssociatedWith.hashCode());
        final Object $used = this.getUsed();
        result = result * PRIME + ($used == null ? 43 : $used.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        return result;
    }
}