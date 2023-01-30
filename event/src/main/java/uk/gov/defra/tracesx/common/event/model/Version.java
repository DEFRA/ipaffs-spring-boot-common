package uk.gov.defra.tracesx.common.event.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Version {
  VERSION("1.1");

  private String value;

  Version(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
