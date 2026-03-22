package org.incenp.obofoundry.kgcl.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.incenp.linkml.core.annotations.LinkURI;

@LinkURI("http://w3id.org/kgcl/ObsoletionPolicyEnum")
public enum ObsoletionPolicyEnum {

    NOLOGICALAXIOMSONOBSOLETES("NoLogicalAxiomsOnObsoletes"),

    OBSOLETELABELSAREPREFIXED("ObsoleteLabelsArePrefixed");

    private final static Map<String, ObsoletionPolicyEnum> MAP;

    static {
        Map<String, ObsoletionPolicyEnum> map = new HashMap<String, ObsoletionPolicyEnum>();
        for ( ObsoletionPolicyEnum value : ObsoletionPolicyEnum.values() ) {
            map.put(value.toString(), value);
        }

        MAP = Collections.unmodifiableMap(map);
    }

    private final String repr;

    ObsoletionPolicyEnum(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }

    public static ObsoletionPolicyEnum fromString(String v) {
        return MAP.get(v);
    }
}