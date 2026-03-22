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

@LinkURI("http://w3id.org/kgcl/Change")
public abstract class Change extends ChangeLanguageElement {

    @Identifier
    @Converter(CurieConverter.class)
    @LinkURI("https://w3id.org/kgcl/basics/id")
    private String id;

    @TypeDesignator
    @LinkURI("rdf:type")
    private String type;

    @SlotName("was_generated_by")
    @LinkURI("http://www.w3.org/ns/prov#wasGeneratedBy")
    private Activity wasGeneratedBy;

    @SlotName("see_also")
    @LinkURI("http://www.w3.org/2000/01/rdf-schema#seeAlso")
    private String seeAlso;

    @SlotName("pull_request")
    @LinkURI("http://w3id.org/kgcl/pull_request")
    private String pullRequest;

    @SlotName("term_tracker_issue")
    @LinkURI("http://w3id.org/kgcl/term_tracker_issue")
    private String termTrackerIssue;

    @LinkURI("http://purl.org/dc/terms/creator")
    private String creator;

    @SlotName("change_date")
    @LinkURI("http://purl.org/dc/terms/date")
    private ZonedDateTime changeDate;

    @LinkURI("http://purl.org/dc/terms/creator")
    private String contributor;

    @SlotName("has_undo")
    @LinkURI("http://w3id.org/kgcl/has_undo")
    private Change hasUndo;

    @SlotName("change_description")
    @LinkURI("http://w3id.org/kgcl/change_description")
    private String changeDescription;

    @SlotName("associated_change_set")
    @Inlined(asList = true)
    @LinkURI("http://w3id.org/kgcl/associated_change_set")
    private List<Change> associatedChangeSet;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setWasGeneratedBy(Activity wasGeneratedBy) {
        this.wasGeneratedBy = wasGeneratedBy;
    }

    public Activity getWasGeneratedBy() {
        return this.wasGeneratedBy;
    }

    public void setSeeAlso(String seeAlso) {
        this.seeAlso = seeAlso;
    }

    public String getSeeAlso() {
        return this.seeAlso;
    }

    public void setPullRequest(String pullRequest) {
        this.pullRequest = pullRequest;
    }

    public String getPullRequest() {
        return this.pullRequest;
    }

    public void setTermTrackerIssue(String termTrackerIssue) {
        this.termTrackerIssue = termTrackerIssue;
    }

    public String getTermTrackerIssue() {
        return this.termTrackerIssue;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setChangeDate(ZonedDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public ZonedDateTime getChangeDate() {
        return this.changeDate;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getContributor() {
        return this.contributor;
    }

    public void setHasUndo(Change hasUndo) {
        this.hasUndo = hasUndo;
    }

    public Change getHasUndo() {
        return this.hasUndo;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public String getChangeDescription() {
        return this.changeDescription;
    }

    public void setAssociatedChangeSet(List<Change> associatedChangeSet) {
        this.associatedChangeSet = associatedChangeSet;
    }

    public List<Change> getAssociatedChangeSet() {
        return this.associatedChangeSet;
    }

    public List<Change> getAssociatedChangeSet(boolean set) {
        if ( this.associatedChangeSet == null && set ) {
            this.associatedChangeSet = new ArrayList<>();
        }
        return this.associatedChangeSet;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "Change(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Change) ) return false;
        final Change other = (Change) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if ( this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$type = this.getType();
        final Object other$type = other.getType();
        if ( this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
        final Object this$wasGeneratedBy = this.getWasGeneratedBy();
        final Object other$wasGeneratedBy = other.getWasGeneratedBy();
        if ( this$wasGeneratedBy == null ? other$wasGeneratedBy != null : !this$wasGeneratedBy.equals(other$wasGeneratedBy)) return false;
        final Object this$seeAlso = this.getSeeAlso();
        final Object other$seeAlso = other.getSeeAlso();
        if ( this$seeAlso == null ? other$seeAlso != null : !this$seeAlso.equals(other$seeAlso)) return false;
        final Object this$pullRequest = this.getPullRequest();
        final Object other$pullRequest = other.getPullRequest();
        if ( this$pullRequest == null ? other$pullRequest != null : !this$pullRequest.equals(other$pullRequest)) return false;
        final Object this$termTrackerIssue = this.getTermTrackerIssue();
        final Object other$termTrackerIssue = other.getTermTrackerIssue();
        if ( this$termTrackerIssue == null ? other$termTrackerIssue != null : !this$termTrackerIssue.equals(other$termTrackerIssue)) return false;
        final Object this$creator = this.getCreator();
        final Object other$creator = other.getCreator();
        if ( this$creator == null ? other$creator != null : !this$creator.equals(other$creator)) return false;
        final Object this$changeDate = this.getChangeDate();
        final Object other$changeDate = other.getChangeDate();
        if ( this$changeDate == null ? other$changeDate != null : !this$changeDate.equals(other$changeDate)) return false;
        final Object this$contributor = this.getContributor();
        final Object other$contributor = other.getContributor();
        if ( this$contributor == null ? other$contributor != null : !this$contributor.equals(other$contributor)) return false;
        final Object this$hasUndo = this.getHasUndo();
        final Object other$hasUndo = other.getHasUndo();
        if ( this$hasUndo == null ? other$hasUndo != null : !this$hasUndo.equals(other$hasUndo)) return false;
        final Object this$changeDescription = this.getChangeDescription();
        final Object other$changeDescription = other.getChangeDescription();
        if ( this$changeDescription == null ? other$changeDescription != null : !this$changeDescription.equals(other$changeDescription)) return false;
        final Object this$associatedChangeSet = this.getAssociatedChangeSet();
        final Object other$associatedChangeSet = other.getAssociatedChangeSet();
        if ( this$associatedChangeSet == null ? other$associatedChangeSet != null : !this$associatedChangeSet.equals(other$associatedChangeSet)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Change;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $type = this.getType();
        result = result * PRIME + ($type == null ? 43 : $type.hashCode());
        final Object $wasGeneratedBy = this.getWasGeneratedBy();
        result = result * PRIME + ($wasGeneratedBy == null ? 43 : $wasGeneratedBy.hashCode());
        final Object $seeAlso = this.getSeeAlso();
        result = result * PRIME + ($seeAlso == null ? 43 : $seeAlso.hashCode());
        final Object $pullRequest = this.getPullRequest();
        result = result * PRIME + ($pullRequest == null ? 43 : $pullRequest.hashCode());
        final Object $termTrackerIssue = this.getTermTrackerIssue();
        result = result * PRIME + ($termTrackerIssue == null ? 43 : $termTrackerIssue.hashCode());
        final Object $creator = this.getCreator();
        result = result * PRIME + ($creator == null ? 43 : $creator.hashCode());
        final Object $changeDate = this.getChangeDate();
        result = result * PRIME + ($changeDate == null ? 43 : $changeDate.hashCode());
        final Object $contributor = this.getContributor();
        result = result * PRIME + ($contributor == null ? 43 : $contributor.hashCode());
        final Object $hasUndo = this.getHasUndo();
        result = result * PRIME + ($hasUndo == null ? 43 : $hasUndo.hashCode());
        final Object $changeDescription = this.getChangeDescription();
        result = result * PRIME + ($changeDescription == null ? 43 : $changeDescription.hashCode());
        final Object $associatedChangeSet = this.getAssociatedChangeSet();
        result = result * PRIME + ($associatedChangeSet == null ? 43 : $associatedChangeSet.hashCode());
        return result;
    }
}