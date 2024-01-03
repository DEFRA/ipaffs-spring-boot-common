package uk.gov.defra.tracesx.common.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.defra.tracesx.common.health.UrlHelper.buildAzureIndexSearchUrl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.AzureHealthCheck;

@ExtendWith(MockitoExtension.class)
class AzureHealthCheckTest {

  private String serviceName = "imports-azure-search-shared";
  private String indexName = "1-economic-operators-index";
  private String apiKey = "292100772E2E949870ADA7F9038B1DBA";
  private String apiVersion = "2017-11-11";

  private String url;

  @Mock private RestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    url = buildAzureIndexSearchUrl(serviceName, indexName, apiVersion);
  }

  @Test
  void willReturnHealthyWhenAzureQueryOk() {

    when(restTemplate.exchange(
            eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.<Class<String>>any()))
        .thenReturn(ResponseEntity.ok().build());
    Health health =
        new AzureHealthCheck(restTemplate, serviceName, indexName, apiKey, apiVersion).check();

    assertEquals(health, Health.up().build());
  }

  @Test
  void willReturnUnHealthyWhenAzureQueryNotOk() {

    url = buildAzureIndexSearchUrl(serviceName, indexName, apiVersion);
    when(restTemplate.exchange(
            eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.<Class<String>>any()))
        .thenReturn(ResponseEntity.notFound().build());
    Health health =
        new AzureHealthCheck(restTemplate, serviceName, indexName, apiKey, apiVersion).check();

    assertEquals(health, Health.down().build());
  }

  @Test
  void willReturnUnHealthyWhenClientThrowsException() {

    url = buildAzureIndexSearchUrl(serviceName, indexName, apiVersion);
    when(restTemplate.exchange(
            eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.<Class<String>>any()))
        .thenThrow(RestClientException.class);
    Health health =
        new AzureHealthCheck(restTemplate, serviceName, indexName, apiKey, apiVersion).check();

    assertEquals(health, Health.down().build());
  }
}
