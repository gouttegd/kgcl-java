package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A summary statistic for a set of changes of the same type, grouped by zero or more node properties
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ChangeSetSummaryStatistic extends ChangeLanguageElement {}