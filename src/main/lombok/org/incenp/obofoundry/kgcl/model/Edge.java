package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A relationship between two nodes.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Edge extends OntologyElement {
    private Node subject;
    private Node predicate;
    private Node object;
    private String subjectRepresentation;
    private String predicateRepresentation;
    private String objectRepresentation;
    private Annotation annotationSet;}
