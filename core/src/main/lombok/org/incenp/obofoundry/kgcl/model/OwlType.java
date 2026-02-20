package org.incenp.obofoundry.kgcl.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OwlType {

  CLASS("CLASS"),

  OBJECT_PROPERTY("OBJECT_PROPERTY"),

  NAMED_INDIVIDUAL("NAMED_INDIVIDUAL"),

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

  @JsonCreator
  public static OwlType fromString(String v) {
    return MAP.get(v);
  }

}