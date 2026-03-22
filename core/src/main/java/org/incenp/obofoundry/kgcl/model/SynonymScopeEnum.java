package org.incenp.obofoundry.kgcl.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.incenp.linkml.core.annotations.LinkURI;

@LinkURI("http://w3id.org/kgcl/om/SynonymScopeEnum")
public enum SynonymScopeEnum {

    @LinkURI("http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym")
    RELATED("related"),

    @LinkURI("http://www.geneontology.org/formats/oboInOwl#hasBroadSynonym")
    BROAD("broad"),

    @LinkURI("http://www.geneontology.org/formats/oboInOwl#hasNarrowSynonym")
    NARROW("narrow"),

    @LinkURI("http://www.geneontology.org/formats/oboInOwl#hasExactSynonym")
    EXACT("exact");

    private final static Map<String, SynonymScopeEnum> MAP;

    static {
        Map<String, SynonymScopeEnum> map = new HashMap<String, SynonymScopeEnum>();
        for ( SynonymScopeEnum value : SynonymScopeEnum.values() ) {
            map.put(value.toString(), value);
        }

        MAP = Collections.unmodifiableMap(map);
    }

    private final String repr;

    SynonymScopeEnum(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }

    public static SynonymScopeEnum fromString(String v) {
        return MAP.get(v);
    }
}