package uk.gov.defra.tracesx.common.azuresql;

import java.util.Map;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 * Connection pool defaults for Azure SQL, added below every other property source so a service's
 * application.yml or an environment variable (e.g. SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE)
 * overrides any of them.
 *
 * <p>They only reach a DataSource that Spring Boot binds from spring.datasource.hikari, i.e. one
 * created by Boot's DataSourceAutoConfiguration. A DataSource built with DataSourceBuilder gets
 * HikariCP's own defaults instead.
 */
public class AzureSqlDefaultsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

  static final String PROPERTY_SOURCE_NAME = "tracesxAzureSqlDefaults";

  private static final String HIKARI = "spring.datasource.hikari.";

  /**
   * SQL Server driver properties. HikariCP passes these to the driver alongside the JDBC URL, and
   * the driver lets them override the same settings in the URL, so these timeouts are owned here
   * rather than by the connection string the Helm chart builds.
   */
  private static final String DRIVER = HIKARI + "data-source-properties";

  static final Map<String, Object> DEFAULTS =
      Map.ofEntries(
          Map.entry(HIKARI + "maximum-pool-size", 10),
          // Find connections the network has dropped in the background, not on a request
          Map.entry(HIKARI + "keepalive-time", 60_000L),
          // Replace connections well before Azure SQL or its gateway drops idle ones
          Map.entry(HIKARI + "max-lifetime", 600_000L),
          // Fail fast rather than hold a request thread for HikariCP's default 30 s
          Map.entry(HIKARI + "connection-timeout", 10_000L),
          Map.entry(HIKARI + "validation-timeout", 2_000L),
          // Milliseconds. Ends a read on a connection the network has silently dropped. Must stay
          // above the longest query timeout (notification's JDBI timeout is 60 s); in PRD no SQL
          // call took longer than 60 s in the 14 days to 25 Nov 2026.
          Map.entry(DRIVER + "[socketTimeout]", "90000"),
          // Seconds. Time allowed to open a connection, including getting the Entra ID token
          Map.entry(DRIVER + "[loginTimeout]", "15"),
          // Seconds. When a query times out but the cancel gets no reply (a dead connection), the
          // driver closes the connection after this long instead of waiting on it
          Map.entry(DRIVER + "[cancelQueryTimeout]", "10"),
          // Pool warnings (e.g. failed validation, leaks) are what explain connection stalls
          Map.entry("logging.level.com.zaxxer.hikari", "WARN"));

  @Override
  public void postProcessEnvironment(
      ConfigurableEnvironment environment, SpringApplication application) {
    MutablePropertySources sources = environment.getPropertySources();
    if (!sources.contains(PROPERTY_SOURCE_NAME)) {
      sources.addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, DEFAULTS));
    }
  }

  /** Runs after the config data post-processor, so addLast stays below application.yml. */
  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
