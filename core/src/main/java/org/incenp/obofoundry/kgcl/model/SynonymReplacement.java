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

@LinkURI("http://w3id.org/kgcl/SynonymReplacement")
public class SynonymReplacement extends NodeSynonymChange {

    @LinkURI("http://w3id.org/kgcl/qualifier")
    private String qualifier;

    @SlotName("has_textual_diff")
    @LinkURI("http://w3id.org/kgcl/has_textual_diff")
    private TextualDiff hasTextualDiff;

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getQualifier() {
        return this.qualifier;
    }

    public void setHasTextualDiff(TextualDiff hasTextualDiff) {
        this.hasTextualDiff = hasTextualDiff;
    }

    public TextualDiff getHasTextualDiff() {
        return this.hasTextualDiff;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "SynonymReplacement(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof SynonymReplacement) ) return false;
        final SynonymReplacement other = (SynonymReplacement) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$qualifier = this.getQualifier();
        final Object other$qualifier = other.getQualifier();
        if ( this$qualifier == null ? other$qualifier != null : !this$qualifier.equals(other$qualifier)) return false;
        final Object this$hasTextualDiff = this.getHasTextualDiff();
        final Object other$hasTextualDiff = other.getHasTextualDiff();
        if ( this$hasTextualDiff == null ? other$hasTextualDiff != null : !this$hasTextualDiff.equals(other$hasTextualDiff)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof SynonymReplacement;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $qualifier = this.getQualifier();
        result = result * PRIME + ($qualifier == null ? 43 : $qualifier.hashCode());
        final Object $hasTextualDiff = this.getHasTextualDiff();
        result = result * PRIME + ($hasTextualDiff == null ? 43 : $hasTextualDiff.hashCode());
        return result;
    }
}