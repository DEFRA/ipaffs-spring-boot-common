package uk.gov.defra.tracesx.common.health;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.AzureHealthCheck;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.defra.tracesx.common.health.UrlHelper.buildAzureIndexSearchUrl;

@RunWith(MockitoJUnitRunner.class)
public class AzureHealthCheckTest {

  private String serviceName = "imports-azure-search-shared";
  private String indexName = "1-economic-operators-index";
  private String apiKey = "292100772E2E949870ADA7F9038B1DBA";
  private String apiVersion = "2017-11-11";

  private String url;

  @Mock private RestTemplate restTemplate;

  @Before
  public void setUp() {
    url = buildAzureIndexSearchUrl(serviceName, indexName, apiVersion);
  }

  @Test
  public void willReturnHealthyWhenAzureQueryOk() {

    when(restTemplate.exchange(
            eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.<Class<String>>any()))
        .thenReturn(ResponseEntity.ok().build());
    Health health =
        new AzureHealthCheck(
                restTemplate, serviceName, indexName, apiKey, apiVersion)
            .check();

    assertEquals(health, Health.up().build());
  }

  @Test
  public void willReturnUnHealthyWhenAzureQueryNotOk() {

    url = buildAzureIndexSearchUrl(serviceName, indexName, apiVersion);
    when(restTemplate.exchange(
            eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.<Class<String>>any()))
        .thenReturn(ResponseEntity.notFound().build());
    Health health =
        new AzureHealthCheck(
                restTemplate, serviceName, indexName, apiKey, apiVersion)
            .check();

    assertEquals(health, Health.down().build());
  }

  @Test
  public void willReturnUnHealthyWhenClientThrowsException() {

    url = buildAzureIndexSearchUrl(serviceName, indexName, apiVersion);
    when(restTemplate.exchange(
            eq(url), Mockito.eq(HttpMethod.POST), Mockito.any(), Mockito.<Class<String>>any()))
        .thenThrow(RestClientException.class);
    Health health =
        new AzureHealthCheck(
                restTemplate, serviceName, indexName, apiKey, apiVersion)
            .check();

    assertEquals(health, Health.down().build());
  }
}
