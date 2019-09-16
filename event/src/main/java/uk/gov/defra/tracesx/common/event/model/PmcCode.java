package uk.gov.defra.tracesx.common.event.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PmcCode {
  PMC_02("0200"),
  PMC_07("0700"),
  PMC_12("1200"),
  PMC_13("1300"),
  PMC_14("1400");

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
