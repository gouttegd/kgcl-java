package org.incenp.obofoundry.kgcl.model;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.incenp.linkml.core.annotations.*;
import org.incenp.linkml.core.types.*;

@LinkURI("http://w3id.org/kgcl/NodeUnobsoletion")
public class NodeUnobsoletion extends NodeChange {

    @SlotName("has_direct_replacement")
    @LinkURI("http://w3id.org/kgcl/has_direct_replacement")
    private Node hasDirectReplacement;

    @SlotName("has_nondirect_replacement")
    @LinkURI("http://w3id.org/kgcl/has_nondirect_replacement")
    private List<Node> hasNondirectReplacement;

    @LinkURI("http://w3id.org/kgcl/about")
    private OntologyElement about;

    public void setHasDirectReplacement(Node hasDirectReplacement) {
        this.hasDirectReplacement = hasDirectReplacement;
    }

    public Node getHasDirectReplacement() {
        return this.hasDirectReplacement;
    }

    public void setHasNondirectReplacement(List<Node> hasNondirectReplacement) {
        this.hasNondirectReplacement = hasNondirectReplacement;
    }

    public List<Node> getHasNondirectReplacement() {
        return this.hasNondirectReplacement;
    }

    public List<Node> getHasNondirectReplacement(boolean set) {
        if ( this.hasNondirectReplacement == null && set ) {
            this.hasNondirectReplacement = new ArrayList<>();
        }
        return this.hasNondirectReplacement;
    }

    public void setAbout(OntologyElement about) {
        this.about = about;
    }

    public OntologyElement getAbout() {
        return this.about;
    }

    @Override
    public NodeObsoletion getHasUndo() {
        return (NodeObsoletion) super.getHasUndo();
    }

    public void setHasUndo(NodeObsoletion value) {
        super.setHasUndo(value);
    }

    @Override
    public void setHasUndo(Change value) {
        if ( !(value instanceof NodeObsoletion) ) {
            throw new IllegalArgumentException("Invalid hasUndo value");
        }
        super.setHasUndo(value);
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "NodeUnobsoletion(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof NodeUnobsoletion) ) return false;
        final NodeUnobsoletion other = (NodeUnobsoletion) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$hasDirectReplacement = this.getHasDirectReplacement();
        final Object other$hasDirectReplacement = other.getHasDirectReplacement();
        if ( this$hasDirectReplacement == null ? other$hasDirectReplacement != null : !this$hasDirectReplacement.equals(other$hasDirectReplacement) ) return false;
        final Object this$hasNondirectReplacement = this.getHasNondirectReplacement();
        final Object other$hasNondirectReplacement = other.getHasNondirectReplacement();
        if ( this$hasNondirectReplacement == null ? other$hasNondirectReplacement != null : !this$hasNondirectReplacement.equals(other$hasNondirectReplacement) ) return false;
        final Object this$about = this.getAbout();
        final Object other$about = other.getAbout();
        if ( this$about == null ? other$about != null : !this$about.equals(other$about) ) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NodeUnobsoletion;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $hasDirectReplacement = this.getHasDirectReplacement();
        result = result * PRIME + ($hasDirectReplacement == null ? 43 : $hasDirectReplacement.hashCode());
        final Object $hasNondirectReplacement = this.getHasNondirectReplacement();
        result = result * PRIME + ($hasNondirectReplacement == null ? 43 : $hasNondirectReplacement.hashCode());
        final Object $about = this.getAbout();
        result = result * PRIME + ($about == null ? 43 : $about.hashCode());
        return result;
    }
}