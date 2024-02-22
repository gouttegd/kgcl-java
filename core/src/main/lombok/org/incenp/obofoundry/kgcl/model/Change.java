package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Any change perform on an ontology or knowledge graph
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Change extends ChangeLanguageElement {
    private String id;
    private String type;
    private Activity wasGeneratedBy;
    private String seeAlso;
    private String pullRequest;
    private String creator;
    private ZonedDateTime changeDate;
    private String contributor;
    private Change hasUndo;
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}