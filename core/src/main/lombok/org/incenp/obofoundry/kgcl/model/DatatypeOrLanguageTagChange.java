package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A change in a value assertion where the value remain unchanged but either the datatype or language changes
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class DatatypeOrLanguageTagChange extends ChangeMixin {}