package uk.gov.defra.tracesx.common.event.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PmcCode {
  PMC_0701("0701"),
  PMC_0706("0706"),
  PMC_1400("1400");

  private String value;

  PmcCode(String value) {
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
