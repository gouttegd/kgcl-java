package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A change in the membership status of a node with respect to a subset (view)
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class SubsetMembershipChange extends ChangeMixin {
    private OntologySubset inSubset;}