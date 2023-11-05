package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Opposite operation of obsoletion. Rarely performed.
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Unobsoletion extends ChangeMixin {}