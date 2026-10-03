package org.incenp.obofoundry.kgcl.model;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.incenp.linkml.core.annotations.*;
import org.incenp.linkml.core.types.*;

@LinkURI("https://schemas.incenp.org/kgcl/deprecation#DeprecationProfile")
public class DeprecationProfile {

    @Required
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#name")
    private String name;

    @Required
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#description")
    private String description;

    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#activatedBy")
    private URI activatedBy;

    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#removeLogicalDefinition")
    private Boolean removeLogicalDefinition;

    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#removeAnnotationAssertions")
    private Boolean removeAnnotationAssertions;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#replacedByAnnotationPropertyIri")
    private String replacedByAnnotationPropertyIri;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#alternateEntityAnnotationPropertyIri")
    private String alternateEntityAnnotationPropertyIri;

    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#labelPrefix")
    private String labelPrefix;

    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#annotationValuePrefix")
    private String annotationValuePrefix;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#preservedAnnotationAssertionPropertyIris")
    private List<String> preservedAnnotationAssertionPropertyIris;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#deprecatedClassParentIri")
    private String deprecatedClassParentIri;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#deprecatedObjectPropertyParentIri")
    private String deprecatedObjectPropertyParentIri;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#deprecatedDataPropertyParentIri")
    private String deprecatedDataPropertyParentIri;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#deprecatedAnnotationPropertyParentIri")
    private String deprecatedAnnotationPropertyParentIri;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#deprecatedIndivdualParentClassIri")
    private String deprecatedIndivdualParentClassIri;

    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#onlyLanguage")
    private String onlyLanguage;

    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#rewireAxioms")
    private Boolean rewireAxioms;

    @TypeURI("https://w3id.org/linkml/Uriorcurie")
    @LinkURI("https://schemas.incenp.org/kgcl/deprecation#labelPropertyIris")
    private List<String> labelPropertyIris;

    @ExtensionHolder
    private Map<String, Object> extraSlots;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setActivatedBy(URI activatedBy) {
        this.activatedBy = activatedBy;
    }

    public URI getActivatedBy() {
        return this.activatedBy;
    }

    public void setRemoveLogicalDefinition(Boolean removeLogicalDefinition) {
        this.removeLogicalDefinition = removeLogicalDefinition;
    }

    public Boolean getRemoveLogicalDefinition() {
        return this.removeLogicalDefinition;
    }

    public void setRemoveAnnotationAssertions(Boolean removeAnnotationAssertions) {
        this.removeAnnotationAssertions = removeAnnotationAssertions;
    }

    public Boolean getRemoveAnnotationAssertions() {
        return this.removeAnnotationAssertions;
    }

    public void setReplacedByAnnotationPropertyIri(String replacedByAnnotationPropertyIri) {
        this.replacedByAnnotationPropertyIri = replacedByAnnotationPropertyIri;
    }

    public String getReplacedByAnnotationPropertyIri() {
        return this.replacedByAnnotationPropertyIri;
    }

    public void setAlternateEntityAnnotationPropertyIri(String alternateEntityAnnotationPropertyIri) {
        this.alternateEntityAnnotationPropertyIri = alternateEntityAnnotationPropertyIri;
    }

    public String getAlternateEntityAnnotationPropertyIri() {
        return this.alternateEntityAnnotationPropertyIri;
    }

    public void setLabelPrefix(String labelPrefix) {
        this.labelPrefix = labelPrefix;
    }

    public String getLabelPrefix() {
        return this.labelPrefix;
    }

    public void setAnnotationValuePrefix(String annotationValuePrefix) {
        this.annotationValuePrefix = annotationValuePrefix;
    }

    public String getAnnotationValuePrefix() {
        return this.annotationValuePrefix;
    }

    public void setPreservedAnnotationAssertionPropertyIris(List<String> preservedAnnotationAssertionPropertyIris) {
        this.preservedAnnotationAssertionPropertyIris = preservedAnnotationAssertionPropertyIris;
    }

    public List<String> getPreservedAnnotationAssertionPropertyIris() {
        return this.preservedAnnotationAssertionPropertyIris;
    }

    public List<String> getPreservedAnnotationAssertionPropertyIris(boolean set) {
        if ( this.preservedAnnotationAssertionPropertyIris == null && set ) {
            this.preservedAnnotationAssertionPropertyIris = new ArrayList<>();
        }
        return this.preservedAnnotationAssertionPropertyIris;
    }

    public void setDeprecatedClassParentIri(String deprecatedClassParentIri) {
        this.deprecatedClassParentIri = deprecatedClassParentIri;
    }

    public String getDeprecatedClassParentIri() {
        return this.deprecatedClassParentIri;
    }

    public void setDeprecatedObjectPropertyParentIri(String deprecatedObjectPropertyParentIri) {
        this.deprecatedObjectPropertyParentIri = deprecatedObjectPropertyParentIri;
    }

    public String getDeprecatedObjectPropertyParentIri() {
        return this.deprecatedObjectPropertyParentIri;
    }

    public void setDeprecatedDataPropertyParentIri(String deprecatedDataPropertyParentIri) {
        this.deprecatedDataPropertyParentIri = deprecatedDataPropertyParentIri;
    }

    public String getDeprecatedDataPropertyParentIri() {
        return this.deprecatedDataPropertyParentIri;
    }

    public void setDeprecatedAnnotationPropertyParentIri(String deprecatedAnnotationPropertyParentIri) {
        this.deprecatedAnnotationPropertyParentIri = deprecatedAnnotationPropertyParentIri;
    }

    public String getDeprecatedAnnotationPropertyParentIri() {
        return this.deprecatedAnnotationPropertyParentIri;
    }

    public void setDeprecatedIndivdualParentClassIri(String deprecatedIndivdualParentClassIri) {
        this.deprecatedIndivdualParentClassIri = deprecatedIndivdualParentClassIri;
    }

    public String getDeprecatedIndivdualParentClassIri() {
        return this.deprecatedIndivdualParentClassIri;
    }

    public void setOnlyLanguage(String onlyLanguage) {
        this.onlyLanguage = onlyLanguage;
    }

    public String getOnlyLanguage() {
        return this.onlyLanguage;
    }

    public void setRewireAxioms(Boolean rewireAxioms) {
        this.rewireAxioms = rewireAxioms;
    }

    public Boolean getRewireAxioms() {
        return this.rewireAxioms;
    }

    public void setLabelPropertyIris(List<String> labelPropertyIris) {
        this.labelPropertyIris = labelPropertyIris;
    }

    public List<String> getLabelPropertyIris() {
        return this.labelPropertyIris;
    }

    public List<String> getLabelPropertyIris(boolean set) {
        if ( this.labelPropertyIris == null && set ) {
            this.labelPropertyIris = new ArrayList<>();
        }
        return this.labelPropertyIris;
    }

    public void setExtraSlots(Map<String,Object> extraSlots) {
        this.extraSlots = extraSlots;
    }

    public Map<String,Object> getExtraSlots() {
        return this.extraSlots;
    }

    public Map<String,Object> getExtraSlots(boolean set) {
        if ( this.extraSlots == null && set ) {
            this.extraSlots = new HashMap<>();
        }
        return this.extraSlots;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Object o;
        sb.append("DeprecationProfile(");
        if ( (o = this.getName()) != null ) {
            sb.append("name=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getDescription()) != null ) {
            sb.append("description=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getActivatedBy()) != null ) {
            sb.append("activatedBy=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getRemoveLogicalDefinition()) != null ) {
            sb.append("removeLogicalDefinition=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getRemoveAnnotationAssertions()) != null ) {
            sb.append("removeAnnotationAssertions=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getReplacedByAnnotationPropertyIri()) != null ) {
            sb.append("replacedByAnnotationPropertyIri=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getAlternateEntityAnnotationPropertyIri()) != null ) {
            sb.append("alternateEntityAnnotationPropertyIri=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getLabelPrefix()) != null ) {
            sb.append("labelPrefix=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getAnnotationValuePrefix()) != null ) {
            sb.append("annotationValuePrefix=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getPreservedAnnotationAssertionPropertyIris()) != null ) {
            sb.append("preservedAnnotationAssertionPropertyIris=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getDeprecatedClassParentIri()) != null ) {
            sb.append("deprecatedClassParentIri=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getDeprecatedObjectPropertyParentIri()) != null ) {
            sb.append("deprecatedObjectPropertyParentIri=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getDeprecatedDataPropertyParentIri()) != null ) {
            sb.append("deprecatedDataPropertyParentIri=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getDeprecatedAnnotationPropertyParentIri()) != null ) {
            sb.append("deprecatedAnnotationPropertyParentIri=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getDeprecatedIndivdualParentClassIri()) != null ) {
            sb.append("deprecatedIndivdualParentClassIri=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getOnlyLanguage()) != null ) {
            sb.append("onlyLanguage=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getRewireAxioms()) != null ) {
            sb.append("rewireAxioms=");
            sb.append(o);
            sb.append(",");
        }
        if ( (o = this.getLabelPropertyIris()) != null ) {
            sb.append("labelPropertyIris=");
            sb.append(o);
            sb.append(",");
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public boolean equals(final Object o) {
        if ( o == this ) return true;
        if ( !(o instanceof DeprecationProfile) ) return false;
        final DeprecationProfile other = (DeprecationProfile) o;
        if ( !other.canEqual((Object) this)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if ( this$name == null ? other$name != null : !this$name.equals(other$name) ) return false;
        final Object this$description = this.getDescription();
        final Object other$description = other.getDescription();
        if ( this$description == null ? other$description != null : !this$description.equals(other$description) ) return false;
        final Object this$activatedBy = this.getActivatedBy();
        final Object other$activatedBy = other.getActivatedBy();
        if ( this$activatedBy == null ? other$activatedBy != null : !this$activatedBy.equals(other$activatedBy) ) return false;
        final Object this$removeLogicalDefinition = this.getRemoveLogicalDefinition();
        final Object other$removeLogicalDefinition = other.getRemoveLogicalDefinition();
        if ( this$removeLogicalDefinition == null ? other$removeLogicalDefinition != null : !this$removeLogicalDefinition.equals(other$removeLogicalDefinition) ) return false;
        final Object this$removeAnnotationAssertions = this.getRemoveAnnotationAssertions();
        final Object other$removeAnnotationAssertions = other.getRemoveAnnotationAssertions();
        if ( this$removeAnnotationAssertions == null ? other$removeAnnotationAssertions != null : !this$removeAnnotationAssertions.equals(other$removeAnnotationAssertions) ) return false;
        final Object this$replacedByAnnotationPropertyIri = this.getReplacedByAnnotationPropertyIri();
        final Object other$replacedByAnnotationPropertyIri = other.getReplacedByAnnotationPropertyIri();
        if ( this$replacedByAnnotationPropertyIri == null ? other$replacedByAnnotationPropertyIri != null : !this$replacedByAnnotationPropertyIri.equals(other$replacedByAnnotationPropertyIri) ) return false;
        final Object this$alternateEntityAnnotationPropertyIri = this.getAlternateEntityAnnotationPropertyIri();
        final Object other$alternateEntityAnnotationPropertyIri = other.getAlternateEntityAnnotationPropertyIri();
        if ( this$alternateEntityAnnotationPropertyIri == null ? other$alternateEntityAnnotationPropertyIri != null : !this$alternateEntityAnnotationPropertyIri.equals(other$alternateEntityAnnotationPropertyIri) ) return false;
        final Object this$labelPrefix = this.getLabelPrefix();
        final Object other$labelPrefix = other.getLabelPrefix();
        if ( this$labelPrefix == null ? other$labelPrefix != null : !this$labelPrefix.equals(other$labelPrefix) ) return false;
        final Object this$annotationValuePrefix = this.getAnnotationValuePrefix();
        final Object other$annotationValuePrefix = other.getAnnotationValuePrefix();
        if ( this$annotationValuePrefix == null ? other$annotationValuePrefix != null : !this$annotationValuePrefix.equals(other$annotationValuePrefix) ) return false;
        final Object this$preservedAnnotationAssertionPropertyIris = this.getPreservedAnnotationAssertionPropertyIris();
        final Object other$preservedAnnotationAssertionPropertyIris = other.getPreservedAnnotationAssertionPropertyIris();
        if ( this$preservedAnnotationAssertionPropertyIris == null ? other$preservedAnnotationAssertionPropertyIris != null : !this$preservedAnnotationAssertionPropertyIris.equals(other$preservedAnnotationAssertionPropertyIris) ) return false;
        final Object this$deprecatedClassParentIri = this.getDeprecatedClassParentIri();
        final Object other$deprecatedClassParentIri = other.getDeprecatedClassParentIri();
        if ( this$deprecatedClassParentIri == null ? other$deprecatedClassParentIri != null : !this$deprecatedClassParentIri.equals(other$deprecatedClassParentIri) ) return false;
        final Object this$deprecatedObjectPropertyParentIri = this.getDeprecatedObjectPropertyParentIri();
        final Object other$deprecatedObjectPropertyParentIri = other.getDeprecatedObjectPropertyParentIri();
        if ( this$deprecatedObjectPropertyParentIri == null ? other$deprecatedObjectPropertyParentIri != null : !this$deprecatedObjectPropertyParentIri.equals(other$deprecatedObjectPropertyParentIri) ) return false;
        final Object this$deprecatedDataPropertyParentIri = this.getDeprecatedDataPropertyParentIri();
        final Object other$deprecatedDataPropertyParentIri = other.getDeprecatedDataPropertyParentIri();
        if ( this$deprecatedDataPropertyParentIri == null ? other$deprecatedDataPropertyParentIri != null : !this$deprecatedDataPropertyParentIri.equals(other$deprecatedDataPropertyParentIri) ) return false;
        final Object this$deprecatedAnnotationPropertyParentIri = this.getDeprecatedAnnotationPropertyParentIri();
        final Object other$deprecatedAnnotationPropertyParentIri = other.getDeprecatedAnnotationPropertyParentIri();
        if ( this$deprecatedAnnotationPropertyParentIri == null ? other$deprecatedAnnotationPropertyParentIri != null : !this$deprecatedAnnotationPropertyParentIri.equals(other$deprecatedAnnotationPropertyParentIri) ) return false;
        final Object this$deprecatedIndivdualParentClassIri = this.getDeprecatedIndivdualParentClassIri();
        final Object other$deprecatedIndivdualParentClassIri = other.getDeprecatedIndivdualParentClassIri();
        if ( this$deprecatedIndivdualParentClassIri == null ? other$deprecatedIndivdualParentClassIri != null : !this$deprecatedIndivdualParentClassIri.equals(other$deprecatedIndivdualParentClassIri) ) return false;
        final Object this$onlyLanguage = this.getOnlyLanguage();
        final Object other$onlyLanguage = other.getOnlyLanguage();
        if ( this$onlyLanguage == null ? other$onlyLanguage != null : !this$onlyLanguage.equals(other$onlyLanguage) ) return false;
        final Object this$rewireAxioms = this.getRewireAxioms();
        final Object other$rewireAxioms = other.getRewireAxioms();
        if ( this$rewireAxioms == null ? other$rewireAxioms != null : !this$rewireAxioms.equals(other$rewireAxioms) ) return false;
        final Object this$labelPropertyIris = this.getLabelPropertyIris();
        final Object other$labelPropertyIris = other.getLabelPropertyIris();
        if ( this$labelPropertyIris == null ? other$labelPropertyIris != null : !this$labelPropertyIris.equals(other$labelPropertyIris) ) return false;
        if ( this.extraSlots == null ? other.extraSlots != null : !this.extraSlots.equals(other.extraSlots) ) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DeprecationProfile;
    }

    @Override
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $description = this.getDescription();
        result = result * PRIME + ($description == null ? 43 : $description.hashCode());
        final Object $activatedBy = this.getActivatedBy();
        result = result * PRIME + ($activatedBy == null ? 43 : $activatedBy.hashCode());
        final Object $removeLogicalDefinition = this.getRemoveLogicalDefinition();
        result = result * PRIME + ($removeLogicalDefinition == null ? 43 : $removeLogicalDefinition.hashCode());
        final Object $removeAnnotationAssertions = this.getRemoveAnnotationAssertions();
        result = result * PRIME + ($removeAnnotationAssertions == null ? 43 : $removeAnnotationAssertions.hashCode());
        final Object $replacedByAnnotationPropertyIri = this.getReplacedByAnnotationPropertyIri();
        result = result * PRIME + ($replacedByAnnotationPropertyIri == null ? 43 : $replacedByAnnotationPropertyIri.hashCode());
        final Object $alternateEntityAnnotationPropertyIri = this.getAlternateEntityAnnotationPropertyIri();
        result = result * PRIME + ($alternateEntityAnnotationPropertyIri == null ? 43 : $alternateEntityAnnotationPropertyIri.hashCode());
        final Object $labelPrefix = this.getLabelPrefix();
        result = result * PRIME + ($labelPrefix == null ? 43 : $labelPrefix.hashCode());
        final Object $annotationValuePrefix = this.getAnnotationValuePrefix();
        result = result * PRIME + ($annotationValuePrefix == null ? 43 : $annotationValuePrefix.hashCode());
        final Object $preservedAnnotationAssertionPropertyIris = this.getPreservedAnnotationAssertionPropertyIris();
        result = result * PRIME + ($preservedAnnotationAssertionPropertyIris == null ? 43 : $preservedAnnotationAssertionPropertyIris.hashCode());
        final Object $deprecatedClassParentIri = this.getDeprecatedClassParentIri();
        result = result * PRIME + ($deprecatedClassParentIri == null ? 43 : $deprecatedClassParentIri.hashCode());
        final Object $deprecatedObjectPropertyParentIri = this.getDeprecatedObjectPropertyParentIri();
        result = result * PRIME + ($deprecatedObjectPropertyParentIri == null ? 43 : $deprecatedObjectPropertyParentIri.hashCode());
        final Object $deprecatedDataPropertyParentIri = this.getDeprecatedDataPropertyParentIri();
        result = result * PRIME + ($deprecatedDataPropertyParentIri == null ? 43 : $deprecatedDataPropertyParentIri.hashCode());
        final Object $deprecatedAnnotationPropertyParentIri = this.getDeprecatedAnnotationPropertyParentIri();
        result = result * PRIME + ($deprecatedAnnotationPropertyParentIri == null ? 43 : $deprecatedAnnotationPropertyParentIri.hashCode());
        final Object $deprecatedIndivdualParentClassIri = this.getDeprecatedIndivdualParentClassIri();
        result = result * PRIME + ($deprecatedIndivdualParentClassIri == null ? 43 : $deprecatedIndivdualParentClassIri.hashCode());
        final Object $onlyLanguage = this.getOnlyLanguage();
        result = result * PRIME + ($onlyLanguage == null ? 43 : $onlyLanguage.hashCode());
        final Object $rewireAxioms = this.getRewireAxioms();
        result = result * PRIME + ($rewireAxioms == null ? 43 : $rewireAxioms.hashCode());
        final Object $labelPropertyIris = this.getLabelPropertyIris();
        result = result * PRIME + ($labelPropertyIris == null ? 43 : $labelPropertyIris.hashCode());
        result = result * PRIME + (this.extraSlots == null ? 43 : this.extraSlots.hashCode());
        return result;
    }
}