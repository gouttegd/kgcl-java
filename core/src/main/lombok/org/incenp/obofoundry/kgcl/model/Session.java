package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlined;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;

/**
 * A session consists of a set of change sets bundled with the activities that generated those change sets
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Session extends ChangeLanguageElement {
    @SlotName("change_set")
    @Inlined(asList = true)
    private List<Change> changeSet;
    @SlotName("activity_set")
    private List<Activity> activitySet;}