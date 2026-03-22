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

@LinkURI("http://w3id.org/kgcl/Transaction")
public class Transaction extends Change {

    @SlotName("change_set")
    @Inlined(asList = true)
    @LinkURI("http://w3id.org/kgcl/change_set")
    private List<Change> changeSet;

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

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "Transaction(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Transaction) ) return false;
        final Transaction other = (Transaction) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$changeSet = this.getChangeSet();
        final Object other$changeSet = other.getChangeSet();
        if ( this$changeSet == null ? other$changeSet != null : !this$changeSet.equals(other$changeSet)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Transaction;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $changeSet = this.getChangeSet();
        result = result * PRIME + ($changeSet == null ? 43 : $changeSet.hashCode());
        return result;
    }
}