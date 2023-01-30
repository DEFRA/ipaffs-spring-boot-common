package uk.gov.defra.tracesx.common.health.checks.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.CheckHealth;

public abstract class HttpHealthCheck implements CheckHealth {

  private RestTemplate restTemplate;
  private HttpHealthParams httpHealthParams;

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpHealthCheck.class);

  protected HttpHealthCheck(RestTemplate restTemplate, HttpHealthParams httpHealthParams) {
    this.restTemplate = restTemplate;
    this.httpHealthParams = httpHealthParams;
  }

  @Override
  public String getName() {
    return httpHealthParams.getName();
  }

  @Override
  public Health check() {

    try {
      ResponseEntity response =
          restTemplate.exchange(
              httpHealthParams.getUrl(),
              httpHealthParams.getMethod(),
              httpHealthParams.getEntity(),
              String.class);
      if (response.getStatusCode() == HttpStatus.OK) {
        return Health.up().build();
      } else {
        LOGGER.error("{} failed status code check", getName());
        return Health.down().build();
      }
    } catch (RestClientException exception) {
      LOGGER.error("{} failed with message {}", getName(), exception.getMessage());
      return Health.down().build();
    }
  }
}
