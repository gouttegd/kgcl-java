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

@LinkURI("http://w3id.org/kgcl/EdgeCreation")
public class EdgeCreation extends EdgeChange {

    @LinkURI("http://w3id.org/kgcl/om/subject")
    private Node subject;

    @LinkURI("http://w3id.org/kgcl/om/predicate")
    private Node predicate;

    @LinkURI("http://w3id.org/kgcl/om/object")
    private Node object;

    @SlotName("subject_type")
    @LinkURI("http://w3id.org/kgcl/subject_type")
    private String subjectType;

    @SlotName("predicate_type")
    @LinkURI("http://w3id.org/kgcl/predicate_type")
    private String predicateType;

    @SlotName("annotation_set")
    @LinkURI("http://w3id.org/kgcl/om/annotation_set")
    private Annotation annotationSet;

    @LinkURI("http://w3id.org/kgcl/about")
    private OntologyElement about;

    public void setSubject(Node subject) {
        this.subject = subject;
    }

    public Node getSubject() {
        return this.subject;
    }

    public void setPredicate(Node predicate) {
        this.predicate = predicate;
    }

    public Node getPredicate() {
        return this.predicate;
    }

    public void setObject(Node object) {
        this.object = object;
    }

    public Node getObject() {
        return this.object;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getSubjectType() {
        return this.subjectType;
    }

    public void setPredicateType(String predicateType) {
        this.predicateType = predicateType;
    }

    public String getPredicateType() {
        return this.predicateType;
    }

    public void setAnnotationSet(Annotation annotationSet) {
        this.annotationSet = annotationSet;
    }

    public Annotation getAnnotationSet() {
        return this.annotationSet;
    }

    public void setAbout(OntologyElement about) {
        this.about = about;
    }

    public OntologyElement getAbout() {
        return this.about;
    }

    public <T> T accept(IChangeVisitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "EdgeCreation(id=" + this.getId() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof EdgeCreation) ) return false;
        final EdgeCreation other = (EdgeCreation) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$subject = this.getSubject();
        final Object other$subject = other.getSubject();
        if ( this$subject == null ? other$subject != null : !this$subject.equals(other$subject)) return false;
        final Object this$predicate = this.getPredicate();
        final Object other$predicate = other.getPredicate();
        if ( this$predicate == null ? other$predicate != null : !this$predicate.equals(other$predicate)) return false;
        final Object this$object = this.getObject();
        final Object other$object = other.getObject();
        if ( this$object == null ? other$object != null : !this$object.equals(other$object)) return false;
        final Object this$subjectType = this.getSubjectType();
        final Object other$subjectType = other.getSubjectType();
        if ( this$subjectType == null ? other$subjectType != null : !this$subjectType.equals(other$subjectType)) return false;
        final Object this$predicateType = this.getPredicateType();
        final Object other$predicateType = other.getPredicateType();
        if ( this$predicateType == null ? other$predicateType != null : !this$predicateType.equals(other$predicateType)) return false;
        final Object this$annotationSet = this.getAnnotationSet();
        final Object other$annotationSet = other.getAnnotationSet();
        if ( this$annotationSet == null ? other$annotationSet != null : !this$annotationSet.equals(other$annotationSet)) return false;
        final Object this$about = this.getAbout();
        final Object other$about = other.getAbout();
        if ( this$about == null ? other$about != null : !this$about.equals(other$about)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EdgeCreation;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $subject = this.getSubject();
        result = result * PRIME + ($subject == null ? 43 : $subject.hashCode());
        final Object $predicate = this.getPredicate();
        result = result * PRIME + ($predicate == null ? 43 : $predicate.hashCode());
        final Object $object = this.getObject();
        result = result * PRIME + ($object == null ? 43 : $object.hashCode());
        final Object $subjectType = this.getSubjectType();
        result = result * PRIME + ($subjectType == null ? 43 : $subjectType.hashCode());
        final Object $predicateType = this.getPredicateType();
        result = result * PRIME + ($predicateType == null ? 43 : $predicateType.hashCode());
        final Object $annotationSet = this.getAnnotationSet();
        result = result * PRIME + ($annotationSet == null ? 43 : $annotationSet.hashCode());
        final Object $about = this.getAbout();
        result = result * PRIME + ($about == null ? 43 : $about.hashCode());
        return result;
    }
}