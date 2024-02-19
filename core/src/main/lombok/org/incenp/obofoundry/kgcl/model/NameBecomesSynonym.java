package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A node synonym where the name NAME of an node NODE moves to a synonym, and NODE receives a new name. This change consists of compose of (1) a node rename where NAME is replaced by a different name (2) a new synonym
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class NameBecomesSynonym extends NodeSynonymChange {
    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }
}