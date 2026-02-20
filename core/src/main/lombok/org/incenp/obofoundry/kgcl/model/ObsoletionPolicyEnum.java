package org.incenp.obofoundry.kgcl.model;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ObsoletionPolicyEnum {

  /**
   * The obsoletion policy is that there MUST NOT be logical axioms about an obsolete node
   */
  NOLOGICALAXIOMSONOBSOLETES("NoLogicalAxiomsOnObsoletes"),

  /**
   * The obsoletion policy is that any label on an obsolete node MUST be prefixed with 'obsolete' or similar
   */
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

  @JsonCreator
  public static ObsoletionPolicyEnum fromString(String v) {
    return MAP.get(v);
  }

}