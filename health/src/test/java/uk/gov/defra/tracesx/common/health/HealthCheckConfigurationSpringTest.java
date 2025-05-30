package uk.gov.defra.tracesx.common.health;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;
import uk.gov.defra.tracesx.common.health.checks.AzureHealthCheck;
import uk.gov.defra.tracesx.common.health.checks.CheckHealth;
import uk.gov.defra.tracesx.common.health.checks.JdbcHealthCheck;

class HealthCheckConfigurationSpringTest {

  @Configuration
  @ComponentScan({"uk.gov.defra.tracesx.common.health.checks"})
  @Import(HealthCheckConfiguration.class)
  public static class ComponentScanConfiguration {
    @Autowired Optional<List<CheckHealth>> checks;
  }

  @Configuration
  public static class JdbcTemplateConfiguration {

    @Bean
    public JdbcTemplate jdbcTemplate() {
      return mock(JdbcTemplate.class);
    }
  }

  @Configuration
  public static class CustomHealthCheckerConfiguration {

    @Bean
    public CheckHealth customHealthCheck() {
      CheckHealth checkHealth = mock(CheckHealth.class);
      when(checkHealth.check()).thenReturn(Health.up().build());
      return checkHealth;
    }
  }

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(ComponentScanConfiguration.class);

  @Test
  void testEmptyHealthCheckerCreated() {
    this.contextRunner.run(
        (context) -> {
          assertThat(context).hasSingleBean(HealthChecker.class);
          assertThat(context.getBean(ComponentScanConfiguration.class).checks).isEmpty();
          assertThat(context.getBean(HealthChecker.class).health()).isEqualTo(Health.up().build());
        });
  }

  @Test
  void testDefaultHealthCheckRestTemplateCreated() {
    this.contextRunner.run(
        (context) -> {
          assertThat(context).hasBean("defaultHealthCheckRestTemplate");
          assertThat(context.getBean("defaultHealthCheckRestTemplate"))
              .isInstanceOf(RestTemplate.class);
        });
  }

  @Test
  void testAzureHealthCheckCreated() {
    this.contextRunner
        .withPropertyValues(
            "azure.search-service-name",
            "test-service",
            "azure.index-name",
            "test-index",
            "azure.query-api-key",
            "test-key",
            "azure.api-version",
            "test-version")
        .run(
            (context) -> {
              assertThat(context).hasSingleBean(AzureHealthCheck.class);
              assertThat(context.getBean(ComponentScanConfiguration.class).checks.get())
                  .contains(context.getBean(AzureHealthCheck.class));
            });
  }

  @Test
  void testAzureHealthCheckNotCreated() {
    this.contextRunner.run(
        (context) -> {
          assertThat(context).doesNotHaveBean(AzureHealthCheck.class);
          assertThat(context.getBean(ComponentScanConfiguration.class).checks).isEmpty();
        });
  }

  @Test
  void testJdbcHealthCheckCreated() {
    this.contextRunner
        .withPropertyValues("spring.datasource.url", "foo:url")
        .withUserConfiguration(ComponentScanConfiguration.class, JdbcTemplateConfiguration.class)
        .run(
            (context) -> {
              assertThat(context).hasSingleBean(JdbcTemplate.class);
              assertThat(context).hasSingleBean(JdbcHealthCheck.class);
              assertThat(context.getBean(ComponentScanConfiguration.class).checks.get())
                  .contains(context.getBean(JdbcHealthCheck.class));
            });
  }

  @Test
  void testJdbcHealthCheckNotCreated() {
    this.contextRunner.run(
        (context) -> {
          assertThat(context).doesNotHaveBean(JdbcHealthCheck.class);
          assertThat(context.getBean(ComponentScanConfiguration.class).checks).isEmpty();
        });
  }

  @Test
  void testCustomHealthCheckBeanRegisteredAndInvoked() {
    this.contextRunner
        .withUserConfiguration(
            ComponentScanConfiguration.class, CustomHealthCheckerConfiguration.class)
        .run(
            (context) -> {
              assertThat(context).getBeans(CheckHealth.class).hasSize(1);
              Collection<CheckHealth> customHealths =
                  context.getBeansOfType(CheckHealth.class).values();
              assertThat(context.getBean(HealthChecker.class).health())
                  .isEqualTo(Health.up().build());
              customHealths.forEach(check -> verify(check).check());
            });
  }
}
