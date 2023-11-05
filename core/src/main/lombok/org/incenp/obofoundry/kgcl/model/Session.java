package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A session consists of a set of change sets bundled with the activities that generated those change sets
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Session extends ChangeLanguageElement {
    private List<Change> changeSet;
    private List<Activity> activitySet;}