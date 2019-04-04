package uk.gov.defra.tracesx.common.health;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckerConfigurationTest {

  private int connectTimeout = 2;
  private int readTimeout = 2;

  @Mock private RestTemplateBuilder restTemplateBuilder;
  @Mock private RestTemplate restTemplate;

  @Test
  public void willCorrectlyBuildARestTemplateForHealthChecking() {

    when(restTemplateBuilder.setConnectTimeout(connectTimeout)).thenReturn(restTemplateBuilder);
    when(restTemplateBuilder.setReadTimeout(readTimeout)).thenReturn(restTemplateBuilder);
    when(restTemplateBuilder.build()).thenReturn(restTemplate);

    RestTemplate template =
        new HealthCheckConfiguration(connectTimeout, readTimeout)
            .defaultRestTemplate(restTemplateBuilder);

    assertEquals(template, restTemplate);
  }

  @Test
  public void willCorrectlyBuildARestTemplateBuilder() {
    RestTemplateBuilder restTemplateBuilder =
        new HealthCheckConfiguration(connectTimeout, readTimeout).restTemplateBuilder();

    assertNotNull(restTemplateBuilder);
  }
}
