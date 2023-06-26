package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * removing an element from a subset
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RemoveFromSubset extends SubsetMembershipChange {}