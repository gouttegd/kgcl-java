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

@LinkURI("http://w3id.org/kgcl/Session")
public class Session extends ChangeLanguageElement {

    @SlotName("change_set")
    @Inlined(asList = true)
    @LinkURI("http://w3id.org/kgcl/change_set")
    private List<Change> changeSet;

    @SlotName("activity_set")
    @Inlined(asList = true)
    @LinkURI("http://www.w3.org/ns/prov#activity_set")
    private List<Activity> activitySet;

    public void setChangeSet(List<Change> changeSet) {
        this.changeSet = changeSet;
    }

    public List<Change> getChangeSet() {
        return this.changeSet;
    }

    public List<Change> getChangeSet(boolean set) {
        if ( this.changeSet == null && set ) {
            this.changeSet = new ArrayList<>();
        }
        return this.changeSet;
    }

    public void setActivitySet(List<Activity> activitySet) {
        this.activitySet = activitySet;
    }

    public List<Activity> getActivitySet() {
        return this.activitySet;
    }

    public List<Activity> getActivitySet(boolean set) {
        if ( this.activitySet == null && set ) {
            this.activitySet = new ArrayList<>();
        }
        return this.activitySet;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("Session(");
        if ( (o = this.getChangeSet()) != null ) {
            sb.append("change_set=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getActivitySet()) != null ) {
            sb.append("activity_set=");
            sb.append(o);
            sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Session) ) return false;
        final Session other = (Session) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$changeSet = this.getChangeSet();
        final Object other$changeSet = other.getChangeSet();
        if ( this$changeSet == null ? other$changeSet != null : !this$changeSet.equals(other$changeSet)) return false;
        final Object this$activitySet = this.getActivitySet();
        final Object other$activitySet = other.getActivitySet();
        if ( this$activitySet == null ? other$activitySet != null : !this$activitySet.equals(other$activitySet)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Session;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $changeSet = this.getChangeSet();
        result = result * PRIME + ($changeSet == null ? 43 : $changeSet.hashCode());
        final Object $activitySet = this.getActivitySet();
        result = result * PRIME + ($activitySet == null ? 43 : $activitySet.hashCode());
        return result;
    }
}