package uk.gov.defra.tracesx.common.event.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Component {
  NOTIFICATION_MICROSERVICE("IPAFFS-NOTIFICATION-MICROSERVICE"),
  CHECKS_MICROSERVICE("IPAFFS-CHECKS-MICROSERVICE"),
  BORDERNOTIFICATION_MICROSERVICE("IPAFFS-BORDERNOTIFICATION-MICROSERVICE"),
  SOAPSEARCH_MICROSERVICE("IPAFFS-SOAPSEARCH-MICROSERVICE"),
  IMPORTS_PROXY("IPAFFS-IMPORTS-PROXY"),
  FILEUPLOAD_MICROSERVICE("IPAFFS-FILEUPLOAD-MICROSERVICE");

  private String value;

  Component(String value) {
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
