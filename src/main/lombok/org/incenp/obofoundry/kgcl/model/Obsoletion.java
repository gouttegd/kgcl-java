package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Obsoletion of an element deprecates usage of that element, but does not delete that element.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Obsoletion extends ChangeMixin {}