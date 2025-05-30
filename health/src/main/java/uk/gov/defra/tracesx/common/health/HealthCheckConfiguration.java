package uk.gov.defra.tracesx.common.health;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.AzureHealthCheck;
import uk.gov.defra.tracesx.common.health.checks.CheckHealth;
import uk.gov.defra.tracesx.common.health.checks.JdbcHealthCheck;

@AutoConfiguration
public class HealthCheckConfiguration {
  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnProperty("azure.index-name")
  public AzureHealthCheck azureHealthCheck(
      final RestTemplate defaultHealthCheckRestTemplate,
      @Value("${azure.search-service-name}") String serviceName,
      @Value("${azure.index-name}") String indexName,
      @Value("${azure.query-api-key}") String apiKey,
      @Value("${azure.api-version}") String apiVersion) {

    final String url = UrlHelper.buildAzureIndexSearchUrl(serviceName, indexName, apiVersion);
    return new AzureHealthCheck(defaultHealthCheckRestTemplate, url, apiKey);
  }

  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnBean(JdbcTemplate.class)
  @ConditionalOnProperty(name = "spring.datasource.url")
  public JdbcHealthCheck jdbcHealthCheck(final JdbcTemplate jdbcTemplate) {
    return new JdbcHealthCheck(jdbcTemplate);
  }

  @Bean
  @Qualifier("defaultHealthCheckRestTemplate")
  public RestTemplate defaultHealthCheckRestTemplate(
      @Value("${management.health.custom.http.connectTimeout:1000}") Integer connectTimeout,
      @Value("${management.health.custom.http.readTimeout:1000}") Integer readTimeout) {
    return new RestTemplateBuilder()
        .connectTimeout(Duration.ofSeconds(connectTimeout))
        .readTimeout(Duration.ofSeconds(readTimeout))
        .build();
  }

  @Bean
  public HealthChecker healthChecker(Optional<List<CheckHealth>> checks) {
    return new HealthChecker(checks.orElse(Collections.emptyList()));
  }
}
