package uk.gov.defra.tracesx.common.health.checks;

import static uk.gov.defra.tracesx.common.health.UrlHelper.buildAzureIndexSearchUrl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthCheck;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthParams;
import uk.gov.defra.tracesx.common.health.checks.util.AzureHealthCheckUtil;

@Component
@ConditionalOnProperty({"azure.public-index-name", "spring.datasource.multiple-enabled"})
public class PublicAzureHealthCheck extends HttpHealthCheck {

  public PublicAzureHealthCheck(
      @Qualifier("defaultHealthCheckRestTemplate") RestTemplate restTemplate,
      @Value("${azure.search-service-name}") String serviceName,
      @Value("${azure.public-index-name}") String indexName,
      @Value("${azure.query-api-key}") String apiKey,
      @Value("${azure.api-version}") String apiVersion) {
    super(
        restTemplate,
        new HttpHealthParams(
            buildAzureIndexSearchUrl(serviceName, indexName, apiVersion),
            "Azure Health Check",
            AzureHealthCheckUtil.buildEntity(apiKey),
            HttpMethod.POST));
  }
}
