package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * a property-value pair
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class PropertyValue extends OntologyElement {
    private Node property;
    private String filler;}