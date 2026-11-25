package uk.gov.defra.tracesx.common.azuresql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@ExtendWith(OutputCaptureExtension.class)
class AzureSqlDataSourceCheckConfigurationTest {

  // A local fixture URL with no credentials
  private static final String URL =
      "jdbc:sqlserver://localhost;databaseName=test"; // trufflehog:ignore

  private static final String WARNING = "does not use the spring.datasource.hikari settings";

  @Configuration
  @ImportAutoConfiguration({
      DataSourceAutoConfiguration.class,
      AzureSqlDataSourceCheckConfiguration.class
  })
  static class BootDataSourceApplication {}

  /** What most services do today. */
  @Configuration
  @ImportAutoConfiguration({
      DataSourceAutoConfiguration.class,
      AzureSqlDataSourceCheckConfiguration.class
  })
  static class OwnDataSourceApplication {
    @Bean
    DataSource getDataSource() {
      return DataSourceBuilder.create().url(URL).build();
    }
  }

  /** A wrapper bean around a hand-built pool is unwrapped and still checked. */
  @Configuration
  @ImportAutoConfiguration({
      DataSourceAutoConfiguration.class,
      AzureSqlDataSourceCheckConfiguration.class
  })
  static class WrappedDataSourceApplication {
    @Bean
    DataSource getDataSource() {
      return new TransactionAwareDataSourceProxy(DataSourceBuilder.create().url(URL).build());
    }
  }

  private ConfigurableApplicationContext run(Class<?> application, String... args) {
    List<String> allArgs = new ArrayList<>(List.of("--spring.datasource.url=" + URL));
    allArgs.addAll(List.of(args));
    return new SpringApplicationBuilder(application)
        .web(WebApplicationType.NONE)
        .run(allArgs.toArray(String[]::new));
  }

  @Test
  void canBeTurnedOffForServicesThatConfigureTheirOwnPools(CapturedOutput output) {
    String turnedOff = "--" + AzureSqlDataSourceCheckConfiguration.ENABLED + "=false";
    try (ConfigurableApplicationContext context = run(OwnDataSourceApplication.class, turnedOff)) {
      assertThat(output).doesNotContain(WARNING);
    }
  }

  @Test
  void staysQuietWhenSpringBootCreatesTheDataSource(CapturedOutput output) {
    try (ConfigurableApplicationContext context = run(BootDataSourceApplication.class)) {
      assertThat(output).doesNotContain(WARNING);
    }
  }

  @Test
  void warnsWhenTheServiceBuildsItsOwnDataSource(CapturedOutput output) {
    try (ConfigurableApplicationContext context = run(OwnDataSourceApplication.class)) {
      assertThat(output)
          .contains("DataSource bean 'getDataSource' " + WARNING)
          .contains("spring.datasource.hikari.keepalive-time is 120000, expected 60000")
          .contains("spring.datasource.hikari.connection-timeout is 30000, expected 10000");
    }
  }

  @Test
  void warnsAboutAHandBuiltPoolBehindAWrapper(CapturedOutput output) {
    try (ConfigurableApplicationContext context = run(WrappedDataSourceApplication.class)) {
      assertThat(output).contains("DataSource bean 'getDataSource' " + WARNING);
    }
  }
}
