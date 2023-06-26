package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * A summarizing of a change on a piece of text. This could be rendered in a number of different ways
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TextualDiff extends ChangeLanguageElement {}