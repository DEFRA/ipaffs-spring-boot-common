package uk.gov.defra.tracesx.common.event.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Application {
  IPAFFS("APH001");

  private String value;

  Application(String value) {
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
