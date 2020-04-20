package uk.gov.defra.tracesx.common.event.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PmcCode {
  PMC_0201("0201"),
  PMC_0203("0203"),
  PMC_0204("0204"),
  PMC_0210("0210"),
  PMC_0211("0211"),
  PMC_0212("0212"),
  PMC_0213("0213"),
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
