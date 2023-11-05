package org.incenp.obofoundry.kgcl.model;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The meaning of operations can be configured
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Configuration extends ChangeLanguageElement {
    private String namePredicate;
    private String definitionPredicate;
    private String mainSynonymPredicate;
    private String synonymPredicates;
    private String creatorPredicate;
    private String contributorPredicate;
    private String obsoleteNodeLabelPrefix;
    private String obsoletionWorkflow;
    private List<String> obsoletionPolicies;
    private String obsoleteSubclassOfShadowProperty;}