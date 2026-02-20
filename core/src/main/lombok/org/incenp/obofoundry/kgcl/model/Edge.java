package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlining;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;
import org.incenp.linkml.core.InliningMode;

/**
 * A relationship between two nodes.
Currently the only kinds of edges supported in KGCL:

  * A subClassOf B &lt;==&gt; Edge(subject=A, predicate=owl:subClassOf, object=B)
  * A subClassOf P some B &lt;==&gt; Edge(subject=A, predicate=P, object=B)
  * P subPropertyOf Q &lt;==&gt; Edge(subject=P, predicate=owl:subPropertyOf, object=Q)

These represent the most common kind of pairwise relationship between classes, and classes are the dominant node type in ontologies.
In future a wider variety of OWL axiom types will be supportedn through the use of an additional edge property/slot to indicate the interpretation of the axiom, following owlstar (https://github.com/cmungall/owlstar).
For example:
 * `A subClassOf R only B &lt;==&gt; Edge(subject=A, predicate=P, object=B, interpretation=AllOnly)`
 * `A Annotation(P,B) &lt;==&gt; Edge(subject=A, predicate=P, object=B, interpretation=annotationAssertion)`

Note that not all axioms are intended to map to edges. Axioms/triples where the object is a literal would be represented as node properties. Complex OWL axioms involving nesting would have their own dedicated construct, or may be represented generically. These are out of scope for the current version of KGCL
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Edge extends OntologyElement {
    private Node subject;
    private Node predicate;
    private Node object;
    @SlotName("subject_representation")
    private String subjectRepresentation;
    @SlotName("predicate_representation")
    private String predicateRepresentation;
    @SlotName("object_representation")
    private String objectRepresentation;
    @SlotName("annotation_set")
    private Annotation annotationSet;}