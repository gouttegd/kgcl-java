package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class Activity extends ProvElement {
    private String id;
    private String startedAtTime;
    private String endedAtTime;
    private Activity wasInformedBy;
    private Agent wasAssociatedWith;
    private String used;
    private String description;}