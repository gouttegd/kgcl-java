package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlined;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;

/**
 * Any change perform on an ontology or knowledge graph
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Change extends ChangeLanguageElement {
    @Identifier
    @Converter(CurieConverter.class)
    private String id;
    @TypeDesignator
    private String type;
    @SlotName("was_generated_by")
    private Activity wasGeneratedBy;
    @SlotName("see_also")
    private String seeAlso;
    @SlotName("pull_request")
    private String pullRequest;
    @SlotName("term_tracker_issue")
    private String termTrackerIssue;
    private String creator;
    @SlotName("change_date")
    private ZonedDateTime changeDate;
    private String contributor;
    @SlotName("has_undo")
    private Change hasUndo;
    @SlotName("change_description")
    private String changeDescription;
    @SlotName("associated_change_set")
    @Inlined(asList = true)
    private List<Change> associatedChangeSet;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}