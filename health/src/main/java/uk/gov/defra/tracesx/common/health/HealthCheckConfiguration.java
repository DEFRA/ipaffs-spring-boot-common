package uk.gov.defra.tracesx.common.health;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HealthCheckConfiguration {

  private final Integer connectTimeout;
  private final Integer readTimeout;

  public HealthCheckConfiguration(
      @Value("${management.health.custom.http.connectTimeout:1000}") Integer connectTimeout,
      @Value("${management.health.custom.http.readTimeout:1000}") Integer readTimeout) {

    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
  }

  @Bean
  @Qualifier("healthCheck")
  public RestTemplate defaultRestTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder
        .setConnectTimeout(connectTimeout)
        .setReadTimeout(readTimeout)
        .build();
  }

  @Bean
  public RestTemplateBuilder restTemplateBuilder() {
    return new RestTemplateBuilder();
  }
}
