package org.incenp.obofoundry.kgcl.model;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.incenp.linkml.core.annotations.Converter;
import org.incenp.linkml.core.annotations.ExtensionHolder;
import org.incenp.linkml.core.annotations.Identifier;
import org.incenp.linkml.core.annotations.Inlined;
import org.incenp.linkml.core.annotations.LinkURI;
import org.incenp.linkml.core.annotations.Required;
import org.incenp.linkml.core.annotations.SlotName;
import org.incenp.linkml.core.annotations.TypeDesignator;
import org.incenp.linkml.core.CurieConverter;

import org.incenp.obofoundry.kgcl.SimpleChangeConverter;

@LinkURI("http://w3id.org/kgcl/Configuration")
public class Configuration extends ChangeLanguageElement {

    @SlotName("name_predicate")
    @LinkURI("http://w3id.org/kgcl/name_predicate")
    private String namePredicate;

    @SlotName("definition_predicate")
    @LinkURI("http://w3id.org/kgcl/definition_predicate")
    private String definitionPredicate;

    @SlotName("main_synonym_predicate")
    @LinkURI("http://w3id.org/kgcl/main_synonym_predicate")
    private String mainSynonymPredicate;

    @SlotName("synonym_predicates")
    @LinkURI("http://w3id.org/kgcl/synonym_predicates")
    private String synonymPredicates;

    @SlotName("creator_predicate")
    @LinkURI("http://w3id.org/kgcl/creator_predicate")
    private String creatorPredicate;

    @SlotName("contributor_predicate")
    @LinkURI("http://w3id.org/kgcl/contributor_predicate")
    private String contributorPredicate;

    @SlotName("obsolete_node_label_prefix")
    @LinkURI("http://w3id.org/kgcl/obsolete_node_label_prefix")
    private String obsoleteNodeLabelPrefix;

    @SlotName("obsoletion_workflow")
    @LinkURI("http://w3id.org/kgcl/obsoletion_workflow")
    private String obsoletionWorkflow;

    @SlotName("obsoletion_policies")
    @LinkURI("http://w3id.org/kgcl/obsoletion_policies")
    private List<ObsoletionPolicyEnum> obsoletionPolicies;

    @SlotName("obsolete_subclass_of_shadow_property")
    @Converter(CurieConverter.class)
    @LinkURI("http://w3id.org/kgcl/obsolete_subclass_of_shadow_property")
    private String obsoleteSubclassOfShadowProperty;

    public void setNamePredicate(String namePredicate) {
        this.namePredicate = namePredicate;
    }

    public String getNamePredicate() {
        return this.namePredicate;
    }

    public void setDefinitionPredicate(String definitionPredicate) {
        this.definitionPredicate = definitionPredicate;
    }

    public String getDefinitionPredicate() {
        return this.definitionPredicate;
    }

    public void setMainSynonymPredicate(String mainSynonymPredicate) {
        this.mainSynonymPredicate = mainSynonymPredicate;
    }

    public String getMainSynonymPredicate() {
        return this.mainSynonymPredicate;
    }

    public void setSynonymPredicates(String synonymPredicates) {
        this.synonymPredicates = synonymPredicates;
    }

    public String getSynonymPredicates() {
        return this.synonymPredicates;
    }

    public void setCreatorPredicate(String creatorPredicate) {
        this.creatorPredicate = creatorPredicate;
    }

    public String getCreatorPredicate() {
        return this.creatorPredicate;
    }

    public void setContributorPredicate(String contributorPredicate) {
        this.contributorPredicate = contributorPredicate;
    }

    public String getContributorPredicate() {
        return this.contributorPredicate;
    }

    public void setObsoleteNodeLabelPrefix(String obsoleteNodeLabelPrefix) {
        this.obsoleteNodeLabelPrefix = obsoleteNodeLabelPrefix;
    }

    public String getObsoleteNodeLabelPrefix() {
        return this.obsoleteNodeLabelPrefix;
    }

    public void setObsoletionWorkflow(String obsoletionWorkflow) {
        this.obsoletionWorkflow = obsoletionWorkflow;
    }

    public String getObsoletionWorkflow() {
        return this.obsoletionWorkflow;
    }

    public void setObsoletionPolicies(List<ObsoletionPolicyEnum> obsoletionPolicies) {
        this.obsoletionPolicies = obsoletionPolicies;
    }

    public List<ObsoletionPolicyEnum> getObsoletionPolicies() {
        return this.obsoletionPolicies;
    }

    public List<ObsoletionPolicyEnum> getObsoletionPolicies(boolean set) {
        if ( this.obsoletionPolicies == null && set ) {
            this.obsoletionPolicies = new ArrayList<>();
        }
        return this.obsoletionPolicies;
    }

    public void setObsoleteSubclassOfShadowProperty(String obsoleteSubclassOfShadowProperty) {
        this.obsoleteSubclassOfShadowProperty = obsoleteSubclassOfShadowProperty;
    }

    public String getObsoleteSubclassOfShadowProperty() {
        return this.obsoleteSubclassOfShadowProperty;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("Configuration(");
        if ( (o = this.getNamePredicate()) != null ) {
            sb.append("name_predicate=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getDefinitionPredicate()) != null ) {
            sb.append("definition_predicate=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getMainSynonymPredicate()) != null ) {
            sb.append("main_synonym_predicate=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getSynonymPredicates()) != null ) {
            sb.append("synonym_predicates=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getCreatorPredicate()) != null ) {
            sb.append("creator_predicate=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getContributorPredicate()) != null ) {
            sb.append("contributor_predicate=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getObsoleteNodeLabelPrefix()) != null ) {
            sb.append("obsolete_node_label_prefix=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getObsoletionWorkflow()) != null ) {
            sb.append("obsoletion_workflow=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getObsoletionPolicies()) != null ) {
            sb.append("obsoletion_policies=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getObsoleteSubclassOfShadowProperty()) != null ) {
            sb.append("obsolete_subclass_of_shadow_property=");
            sb.append(o);
            sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof Configuration) ) return false;
        final Configuration other = (Configuration) o;
        if ( !other.canEqual((Object) this)) return false;
        if ( !super.equals(o) ) return false;

        final Object this$namePredicate = this.getNamePredicate();
        final Object other$namePredicate = other.getNamePredicate();
        if ( this$namePredicate == null ? other$namePredicate != null : !this$namePredicate.equals(other$namePredicate)) return false;
        final Object this$definitionPredicate = this.getDefinitionPredicate();
        final Object other$definitionPredicate = other.getDefinitionPredicate();
        if ( this$definitionPredicate == null ? other$definitionPredicate != null : !this$definitionPredicate.equals(other$definitionPredicate)) return false;
        final Object this$mainSynonymPredicate = this.getMainSynonymPredicate();
        final Object other$mainSynonymPredicate = other.getMainSynonymPredicate();
        if ( this$mainSynonymPredicate == null ? other$mainSynonymPredicate != null : !this$mainSynonymPredicate.equals(other$mainSynonymPredicate)) return false;
        final Object this$synonymPredicates = this.getSynonymPredicates();
        final Object other$synonymPredicates = other.getSynonymPredicates();
        if ( this$synonymPredicates == null ? other$synonymPredicates != null : !this$synonymPredicates.equals(other$synonymPredicates)) return false;
        final Object this$creatorPredicate = this.getCreatorPredicate();
        final Object other$creatorPredicate = other.getCreatorPredicate();
        if ( this$creatorPredicate == null ? other$creatorPredicate != null : !this$creatorPredicate.equals(other$creatorPredicate)) return false;
        final Object this$contributorPredicate = this.getContributorPredicate();
        final Object other$contributorPredicate = other.getContributorPredicate();
        if ( this$contributorPredicate == null ? other$contributorPredicate != null : !this$contributorPredicate.equals(other$contributorPredicate)) return false;
        final Object this$obsoleteNodeLabelPrefix = this.getObsoleteNodeLabelPrefix();
        final Object other$obsoleteNodeLabelPrefix = other.getObsoleteNodeLabelPrefix();
        if ( this$obsoleteNodeLabelPrefix == null ? other$obsoleteNodeLabelPrefix != null : !this$obsoleteNodeLabelPrefix.equals(other$obsoleteNodeLabelPrefix)) return false;
        final Object this$obsoletionWorkflow = this.getObsoletionWorkflow();
        final Object other$obsoletionWorkflow = other.getObsoletionWorkflow();
        if ( this$obsoletionWorkflow == null ? other$obsoletionWorkflow != null : !this$obsoletionWorkflow.equals(other$obsoletionWorkflow)) return false;
        final Object this$obsoletionPolicies = this.getObsoletionPolicies();
        final Object other$obsoletionPolicies = other.getObsoletionPolicies();
        if ( this$obsoletionPolicies == null ? other$obsoletionPolicies != null : !this$obsoletionPolicies.equals(other$obsoletionPolicies)) return false;
        final Object this$obsoleteSubclassOfShadowProperty = this.getObsoleteSubclassOfShadowProperty();
        final Object other$obsoleteSubclassOfShadowProperty = other.getObsoleteSubclassOfShadowProperty();
        if ( this$obsoleteSubclassOfShadowProperty == null ? other$obsoleteSubclassOfShadowProperty != null : !this$obsoleteSubclassOfShadowProperty.equals(other$obsoleteSubclassOfShadowProperty)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Configuration;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $namePredicate = this.getNamePredicate();
        result = result * PRIME + ($namePredicate == null ? 43 : $namePredicate.hashCode());
        final Object $definitionPredicate = this.getDefinitionPredicate();
        result = result * PRIME + ($definitionPredicate == null ? 43 : $definitionPredicate.hashCode());
        final Object $mainSynonymPredicate = this.getMainSynonymPredicate();
        result = result * PRIME + ($mainSynonymPredicate == null ? 43 : $mainSynonymPredicate.hashCode());
        final Object $synonymPredicates = this.getSynonymPredicates();
        result = result * PRIME + ($synonymPredicates == null ? 43 : $synonymPredicates.hashCode());
        final Object $creatorPredicate = this.getCreatorPredicate();
        result = result * PRIME + ($creatorPredicate == null ? 43 : $creatorPredicate.hashCode());
        final Object $contributorPredicate = this.getContributorPredicate();
        result = result * PRIME + ($contributorPredicate == null ? 43 : $contributorPredicate.hashCode());
        final Object $obsoleteNodeLabelPrefix = this.getObsoleteNodeLabelPrefix();
        result = result * PRIME + ($obsoleteNodeLabelPrefix == null ? 43 : $obsoleteNodeLabelPrefix.hashCode());
        final Object $obsoletionWorkflow = this.getObsoletionWorkflow();
        result = result * PRIME + ($obsoletionWorkflow == null ? 43 : $obsoletionWorkflow.hashCode());
        final Object $obsoletionPolicies = this.getObsoletionPolicies();
        result = result * PRIME + ($obsoletionPolicies == null ? 43 : $obsoletionPolicies.hashCode());
        final Object $obsoleteSubclassOfShadowProperty = this.getObsoleteSubclassOfShadowProperty();
        result = result * PRIME + ($obsoleteSubclassOfShadowProperty == null ? 43 : $obsoleteSubclassOfShadowProperty.hashCode());
        return result;
    }
}