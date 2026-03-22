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

@LinkURI("http://w3id.org/kgcl/om/Edge")
public class Edge extends OntologyElement {

    @LinkURI("http://w3id.org/kgcl/om/subject")
    private Node subject;

    @LinkURI("http://w3id.org/kgcl/om/predicate")
    private Node predicate;

    @LinkURI("http://w3id.org/kgcl/om/object")
    private Node object;

    @SlotName("subject_representation")
    @LinkURI("http://w3id.org/kgcl/om/subject_representation")
    private String subjectRepresentation;

    @SlotName("predicate_representation")
    @LinkURI("http://w3id.org/kgcl/om/predicate_representation")
    private String predicateRepresentation;

    @SlotName("object_representation")
    @LinkURI("http://w3id.org/kgcl/om/object_representation")
    private String objectRepresentation;

    @SlotName("annotation_set")
    @LinkURI("http://w3id.org/kgcl/om/annotation_set")
    private Annotation annotationSet;

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

    public void setSubjectRepresentation(String subjectRepresentation) {
        this.subjectRepresentation = subjectRepresentation;
    }

    public String getSubjectRepresentation() {
        return this.subjectRepresentation;
    }

    public void setPredicateRepresentation(String predicateRepresentation) {
        this.predicateRepresentation = predicateRepresentation;
    }

    public String getPredicateRepresentation() {
        return this.predicateRepresentation;
    }

    public void setObjectRepresentation(String objectRepresentation) {
        this.objectRepresentation = objectRepresentation;
    }

    public String getObjectRepresentation() {
        return this.objectRepresentation;
    }

    public void setAnnotationSet(Annotation annotationSet) {
        this.annotationSet = annotationSet;
    }

    public Annotation getAnnotationSet() {
        return this.annotationSet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("Edge(");
        if ( (o = this.getSubject()) != null ) {
            sb.append("subject=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getPredicate()) != null ) {
            sb.append("predicate=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getObject()) != null ) {
            sb.append("object=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getSubjectRepresentation()) != null ) {
            sb.append("subject_representation=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getPredicateRepresentation()) != null ) {
            sb.append("predicate_representation=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getObjectRepresentation()) != null ) {
            sb.append("object_representation=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getAnnotationSet()) != null ) {
            sb.append("annotation_set=");
            sb.append(o);
            sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Edge) ) return false;
        final Edge other = (Edge) o;
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
        final Object this$subjectRepresentation = this.getSubjectRepresentation();
        final Object other$subjectRepresentation = other.getSubjectRepresentation();
        if ( this$subjectRepresentation == null ? other$subjectRepresentation != null : !this$subjectRepresentation.equals(other$subjectRepresentation)) return false;
        final Object this$predicateRepresentation = this.getPredicateRepresentation();
        final Object other$predicateRepresentation = other.getPredicateRepresentation();
        if ( this$predicateRepresentation == null ? other$predicateRepresentation != null : !this$predicateRepresentation.equals(other$predicateRepresentation)) return false;
        final Object this$objectRepresentation = this.getObjectRepresentation();
        final Object other$objectRepresentation = other.getObjectRepresentation();
        if ( this$objectRepresentation == null ? other$objectRepresentation != null : !this$objectRepresentation.equals(other$objectRepresentation)) return false;
        final Object this$annotationSet = this.getAnnotationSet();
        final Object other$annotationSet = other.getAnnotationSet();
        if ( this$annotationSet == null ? other$annotationSet != null : !this$annotationSet.equals(other$annotationSet)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Edge;
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
        final Object $subjectRepresentation = this.getSubjectRepresentation();
        result = result * PRIME + ($subjectRepresentation == null ? 43 : $subjectRepresentation.hashCode());
        final Object $predicateRepresentation = this.getPredicateRepresentation();
        result = result * PRIME + ($predicateRepresentation == null ? 43 : $predicateRepresentation.hashCode());
        final Object $objectRepresentation = this.getObjectRepresentation();
        result = result * PRIME + ($objectRepresentation == null ? 43 : $objectRepresentation.hashCode());
        final Object $annotationSet = this.getAnnotationSet();
        result = result * PRIME + ($annotationSet == null ? 43 : $annotationSet.hashCode());
        return result;
    }
}