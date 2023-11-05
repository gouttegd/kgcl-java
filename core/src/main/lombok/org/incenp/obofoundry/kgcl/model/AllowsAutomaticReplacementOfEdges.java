package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Applies to an obsoletion in which annotations or edges pointing at the obsoleted node can be automatically rewired to point to a target
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class AllowsAutomaticReplacementOfEdges extends Obsoletion {}