package uk.gov.defra.tracesx.common.health.checks;

import java.util.Collections;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthCheck;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthParams;

public class AzureHealthCheck extends HttpHealthCheck {

  public AzureHealthCheck(RestTemplate restTemplate, String url, String apiKey) {
    super(
        restTemplate,
        new HttpHealthParams(url, "Azure Health Check", buildEntity(apiKey), HttpMethod.POST));
  }

  private static HttpEntity<String> buildEntity(String apiKey) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.addAll("api-key ", Collections.singletonList(apiKey));
    return new HttpEntity<>(
        "{\"search\":\"*\",\"top\":0,\"skip\":0,\"queryType\":\"simple\"}", headers);
  }
}
