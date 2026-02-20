package org.incenp.obofoundry.kgcl.model;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlining;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.CurieConverter;
import org.incenp.linkml.core.InliningMode;

/**
 * The meaning of operations can be configured
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class Configuration extends ChangeLanguageElement {
    @SlotName("name_predicate")
    private String namePredicate;
    @SlotName("definition_predicate")
    private String definitionPredicate;
    @SlotName("main_synonym_predicate")
    private String mainSynonymPredicate;
    @SlotName("synonym_predicates")
    private String synonymPredicates;
    @SlotName("creator_predicate")
    private String creatorPredicate;
    @SlotName("contributor_predicate")
    private String contributorPredicate;
    @SlotName("obsolete_node_label_prefix")
    private String obsoleteNodeLabelPrefix;
    @SlotName("obsoletion_workflow")
    private String obsoletionWorkflow;
    @SlotName("obsoletion_policies")
    private List<ObsoletionPolicyEnum> obsoletionPolicies;
    @SlotName("obsolete_subclass_of_shadow_property")
    @Converter(CurieConverter.class)
    private String obsoleteSubclassOfShadowProperty;}