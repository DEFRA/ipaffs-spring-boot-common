package uk.gov.defra.tracesx.common.health.checks;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthCheck;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthParams;

import java.util.Collections;

import static uk.gov.defra.tracesx.common.health.UrlHelper.buildAzureIndexSearchUrl;

@Component
public class AzureHealthCheck extends HttpHealthCheck {

  public AzureHealthCheck(
      @Qualifier("healthCheck") RestTemplate restTemplate,
      @Value("${azure.search-service-name:service}") String serviceName,
      @Value("${azure.index-name:index}") String indexName,
      @Value("${azure.query-api-key:api-key}") String apiKey,
      @Value("${azure.api-version:api-version}") String apiVersion,
      @Value("${azure.deployment-environment:env}") String deploymentEnvironment) {
    super(
        restTemplate,
        new HttpHealthParams(
            buildAzureIndexSearchUrl(serviceName, deploymentEnvironment, indexName, apiVersion),
            "Azure Health Check",
            buildEntity(apiKey),
            HttpMethod.POST));
  }

  private static HttpEntity<String> buildEntity(String apiKey) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.addAll("api-key ", Collections.singletonList(apiKey));
    return new HttpEntity<>(
        "{\"search\":\"*\",\"top\":0,\"skip\":0,\"queryType\":\"simple\"}", headers);
  }
}
