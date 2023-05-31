package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Annotation extends PropertyValue {
    private Annotation annotationSet;
    private String propertyType;
    private String fillerType;}