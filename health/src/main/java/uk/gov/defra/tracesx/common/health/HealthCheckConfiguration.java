package uk.gov.defra.tracesx.common.health;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.CheckHealth;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
  @Qualifier("defaultHealthCheckRestTemplate")
  public RestTemplate defaultHealthCheckRestTemplate() {
    return new RestTemplateBuilder()
        .setConnectTimeout(Duration.ofSeconds((long) connectTimeout))
        .setReadTimeout(Duration.ofSeconds((long) readTimeout))
        .build();
  }

  @Bean
  public HealthChecker healthChecker(Optional<List<CheckHealth>> checks) {
    return new HealthChecker(checks.orElse(Collections.emptyList()));
  }
}
