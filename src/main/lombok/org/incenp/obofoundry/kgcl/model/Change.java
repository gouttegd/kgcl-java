package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Change extends ChangeLanguageElement {
    private String id;
    private String type;
    private Activity wasGeneratedBy;
    private String seeAlso;
    private String pullRequest;
    private String creator;
    private String changeDate;
    private String contributor;
    private Change hasUndo;
    public void accept(IChangeVisitor v) {
        v.visit(this);
    }
}