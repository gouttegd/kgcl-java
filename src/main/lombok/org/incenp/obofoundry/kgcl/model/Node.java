package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Node extends OntologyElement {
    private String id;
    private String name;
    private Annotation annotationSet;
    private String owlType;}