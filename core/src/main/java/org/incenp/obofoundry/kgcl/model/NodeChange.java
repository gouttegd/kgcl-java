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

@LinkURI("http://w3id.org/kgcl/NodeChange")
public abstract class NodeChange extends SimpleChange {

    @SlotName("about_node")
    @LinkURI("http://w3id.org/kgcl/about_node")
    private Node aboutNode;

    @SlotName("about_node_representation")
    @LinkURI("http://w3id.org/kgcl/about_node_representation")
    private String aboutNodeRepresentation;

    @LinkURI("http://w3id.org/kgcl/language")
    private String language;

    public void setAboutNode(Node aboutNode) {
        this.aboutNode = aboutNode;
    }

    public Node getAboutNode() {
        return this.aboutNode;
    }

    public void setAboutNodeRepresentation(String aboutNodeRepresentation) {
        this.aboutNodeRepresentation = aboutNodeRepresentation;
    }

    public String getAboutNodeRepresentation() {
        return this.aboutNodeRepresentation;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return this.language;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "NodeChange(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof NodeChange) ) return false;
        final NodeChange other = (NodeChange) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$aboutNode = this.getAboutNode();
        final Object other$aboutNode = other.getAboutNode();
        if ( this$aboutNode == null ? other$aboutNode != null : !this$aboutNode.equals(other$aboutNode)) return false;
        final Object this$aboutNodeRepresentation = this.getAboutNodeRepresentation();
        final Object other$aboutNodeRepresentation = other.getAboutNodeRepresentation();
        if ( this$aboutNodeRepresentation == null ? other$aboutNodeRepresentation != null : !this$aboutNodeRepresentation.equals(other$aboutNodeRepresentation)) return false;
        final Object this$language = this.getLanguage();
        final Object other$language = other.getLanguage();
        if ( this$language == null ? other$language != null : !this$language.equals(other$language)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof NodeChange;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $aboutNode = this.getAboutNode();
        result = result * PRIME + ($aboutNode == null ? 43 : $aboutNode.hashCode());
        final Object $aboutNodeRepresentation = this.getAboutNodeRepresentation();
        result = result * PRIME + ($aboutNodeRepresentation == null ? 43 : $aboutNodeRepresentation.hashCode());
        final Object $language = this.getLanguage();
        result = result * PRIME + ($language == null ? 43 : $language.hashCode());
        return result;
    }
}