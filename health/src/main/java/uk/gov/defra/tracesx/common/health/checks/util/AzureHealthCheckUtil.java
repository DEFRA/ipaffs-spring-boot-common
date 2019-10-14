package uk.gov.defra.tracesx.common.health.checks.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

public class AzureHealthCheckUtil {

  public static HttpEntity<String> buildEntity(String apiKey) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.addAll("api-key ", Collections.singletonList(apiKey));
    return new HttpEntity<>(
        "{\"search\":\"*\",\"top\":0,\"skip\":0,\"queryType\":\"simple\"}", headers);
  }
}
