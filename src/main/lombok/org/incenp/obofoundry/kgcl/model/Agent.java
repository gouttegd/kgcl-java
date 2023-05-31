package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Agent extends ProvElement {
    private String id;
    private Agent actedOnBehalfOf;
    private Activity wasInformedBy;}