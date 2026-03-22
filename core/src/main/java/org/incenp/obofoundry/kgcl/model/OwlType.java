package org.incenp.obofoundry.kgcl.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.incenp.linkml.core.annotations.LinkURI;

@LinkURI("http://w3id.org/kgcl/om/OwlType")
public enum OwlType {

    @LinkURI("owl:Class")
    CLASS("CLASS"),

    @LinkURI("owl:ObjectProperty")
    OBJECT_PROPERTY("OBJECT_PROPERTY"),

    @LinkURI("owl:NamedIndividual")
    NAMED_INDIVIDUAL("NAMED_INDIVIDUAL"),

    @LinkURI("owl:AnnotationProperty")
    ANNOTATION_PROPERTY("ANNOTATION_PROPERTY");

    private final static Map<String, OwlType> MAP;

    static {
        Map<String, OwlType> map = new HashMap<String, OwlType>();
        for ( OwlType value : OwlType.values() ) {
            map.put(value.toString(), value);
        }

        MAP = Collections.unmodifiableMap(map);
    }

    private final String repr;

    OwlType(String repr) {
        this.repr = repr;
    }

    @Override
    public String toString() {
        return repr;
    }

    public static OwlType fromString(String v) {
        return MAP.get(v);
    }
}