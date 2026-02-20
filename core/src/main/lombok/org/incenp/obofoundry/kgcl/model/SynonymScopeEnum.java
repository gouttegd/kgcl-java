package org.incenp.obofoundry.kgcl.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum SynonymScopeEnum {

  RELATED("related"),

  BROAD("broad"),

  NARROW("narrow"),

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

  @JsonCreator
  public static SynonymScopeEnum fromString(String v) {
    return MAP.get(v);
  }

}