package uk.gov.defra.tracesx.common.azuresql;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Starts a real SpringApplication, so the post-processor is picked up from spring.factories and
 * the DataSource is bound by Boot as it is in a service. HikariCP only connects on first use, so
 * no database is needed.
 */
class AzureSqlDefaultsEnvironmentPostProcessorTest {

  // A local fixture URL with no credentials
  private static final String URL =
      "--spring.datasource.url=jdbc:sqlserver://localhost;databaseName=test"; // trufflehog:ignore

  @Configuration
  @Import(DataSourceAutoConfiguration.class)
  static class TestApplication {}

  private ConfigurableApplicationContext run(String... args) {
    List<String> allArgs = new ArrayList<>(List.of(URL));
    allArgs.addAll(List.of(args));
    return new SpringApplicationBuilder(TestApplication.class)
        .web(WebApplicationType.NONE)
        .run(allArgs.toArray(String[]::new));
  }

  @Test
  void appliesPoolDefaultsToTheAutoConfiguredDataSource() {
    try (ConfigurableApplicationContext context = run()) {
      HikariDataSource dataSource = context.getBean(HikariDataSource.class);

      assertThat(dataSource.getMaximumPoolSize()).isEqualTo(10);
      assertThat(dataSource.getKeepaliveTime()).isEqualTo(60_000L);
      assertThat(dataSource.getMaxLifetime()).isEqualTo(600_000L);
      assertThat(dataSource.getConnectionTimeout()).isEqualTo(10_000L);
      assertThat(dataSource.getValidationTimeout()).isEqualTo(2_000L);
      assertThat(dataSource.getDriverClassName())
          .isEqualTo("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }
  }

  @Test
  void passesDriverTimeoutsToTheSqlServerDriver() {
    try (ConfigurableApplicationContext context = run()) {
      Properties driverProperties =
          context.getBean(HikariDataSource.class).getDataSourceProperties();

      assertThat(driverProperties)
          .containsEntry("socketTimeout", "90000")
          .containsEntry("loginTimeout", "15")
          .containsEntry("cancelQueryTimeout", "10");
    }
  }

  @Test
  void driverTimeoutsCanBeOverridden() {
    try (ConfigurableApplicationContext context =
        run("--spring.datasource.hikari.data-source-properties[socketTimeout]=120000")) {
      assertThat(context.getBean(HikariDataSource.class).getDataSourceProperties())
          .containsEntry("socketTimeout", "120000")
          .containsEntry("loginTimeout", "15");
    }
  }

  /**
   * The chart's JDBC URL still has socketTimeout=1800000. The defaults only win because the driver
   * lets supplied properties override the URL; this fails if a driver upgrade changes that.
   */
  @Test
  void driverPropertiesOverrideTheSameSettingsInTheJdbcUrl() throws SQLException {
    String url = "jdbc:sqlserver://localhost;databaseName=test;" // trufflehog:ignore
        + "socketTimeout=1800000;loginTimeout=30";
    Properties supplied = new Properties();
    supplied.setProperty("socketTimeout", "90000");
    supplied.setProperty("loginTimeout", "15");

    Map<String, String> effective = new HashMap<>();
    for (DriverPropertyInfo info : new SQLServerDriver().getPropertyInfo(url, supplied)) {
      effective.put(info.name, info.value);
    }

    assertThat(effective)
        .containsEntry("socketTimeout", "90000")
        .containsEntry("loginTimeout", "15");
  }

  @Test
  void setsHikariLoggingToWarn() {
    try (ConfigurableApplicationContext context = run()) {
      Logger hikariLogger = (Logger) LoggerFactory.getLogger("com.zaxxer.hikari");
      assertThat(hikariLogger.getLevel()).isEqualTo(Level.WARN);
    }
  }

  @Test
  void applicationYamlOverridesTheDefaults() {
    try (ConfigurableApplicationContext context =
        run("--spring.config.location=classpath:azure-sql-override.yml")) {
      HikariDataSource dataSource = context.getBean(HikariDataSource.class);

      assertThat(dataSource.getMaximumPoolSize()).isEqualTo(25);
      assertThat(dataSource.getConnectionTimeout()).isEqualTo(5_000L);
      // Not set in the file, so the default still applies
      assertThat(dataSource.getKeepaliveTime()).isEqualTo(60_000L);
    }
  }

  @Test
  void commandLinePropertiesOverrideTheDefaults() {
    try (ConfigurableApplicationContext context =
        run("--spring.datasource.hikari.max-lifetime=300000")) {
      assertThat(context.getBean(HikariDataSource.class).getMaxLifetime()).isEqualTo(300_000L);
    }
  }
}
