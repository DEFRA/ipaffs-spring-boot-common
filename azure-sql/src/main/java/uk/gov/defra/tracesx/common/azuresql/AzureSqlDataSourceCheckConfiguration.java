package uk.gov.defra.tracesx.common.azuresql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToLongFunction;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Warns at startup about any HikariDataSource whose pool settings differ from what the
 * spring.datasource.hikari properties, including this module's defaults, say they should be. That
 * happens when a service builds its own DataSource, e.g. with DataSourceBuilder: Spring Boot then
 * backs off, and none of those properties apply.
 *
 * <p>This is a migration aid for services moving off their own DataSource beans, not a pattern
 * for other modules. A service that deliberately configures its own pools (e.g. several
 * DataSources) can turn it off with {@value #ENABLED}=false.
 */
@AutoConfiguration
@ConditionalOnClass(HikariDataSource.class)
@ConditionalOnProperty(name = AzureSqlDataSourceCheckConfiguration.ENABLED, havingValue = "true",
    matchIfMissing = true)
public class AzureSqlDataSourceCheckConfiguration {

  static final String ENABLED = "tracesx.azure-sql.datasource-check.enabled";

  private static final Logger LOGGER =
      LoggerFactory.getLogger(AzureSqlDataSourceCheckConfiguration.class);

  static final String PREFIX = "spring.datasource.hikari";

  private static final Map<String, ToLongFunction<HikariConfig>> CHECKED = new LinkedHashMap<>();

  static {
    CHECKED.put("maximum-pool-size", HikariConfig::getMaximumPoolSize);
    CHECKED.put("keepalive-time", HikariConfig::getKeepaliveTime);
    CHECKED.put("max-lifetime", HikariConfig::getMaxLifetime);
    CHECKED.put("connection-timeout", HikariConfig::getConnectionTimeout);
    CHECKED.put("validation-timeout", HikariConfig::getValidationTimeout);
  }

  @Bean
  static SmartInitializingSingleton azureSqlDataSourceCheck(
      ListableBeanFactory beanFactory, Environment environment) {
    return () -> check(beanFactory, environment);
  }

  static void check(ListableBeanFactory beanFactory, Environment environment) {
    HikariConfig expected =
        Binder.get(environment)
            .bind(PREFIX, Bindable.ofInstance(new HikariConfig()))
            .orElseGet(HikariConfig::new);

    beanFactory.getBeansOfType(DataSource.class, false, false).forEach((name, dataSource) -> {
      HikariDataSource hikari = unwrap(dataSource);
      if (hikari == null) {
        return;
      }
      List<String> differences = new ArrayList<>();
      CHECKED.forEach((property, getter) -> {
        long actual = getter.applyAsLong(hikari);
        long wanted = getter.applyAsLong(expected);
        if (actual != wanted) {
          differences.add(PREFIX + "." + property + " is " + actual + ", expected " + wanted);
        }
      });
      if (!differences.isEmpty()) {
        LOGGER.warn(
            "DataSource bean '{}' does not use the {} settings ({}). It looks like the service "
                + "builds it itself (e.g. with DataSourceBuilder), so Spring Boot does not bind "
                + "it and the Azure SQL pool defaults do not apply. Remove that bean and let "
                + "Spring Boot create the DataSource.",
            name, PREFIX, String.join("; ", differences));
      }
    });
  }

  private static HikariDataSource unwrap(DataSource dataSource) {
    if (dataSource instanceof HikariDataSource hikari) {
      return hikari;
    }
    try {
      return dataSource.isWrapperFor(HikariDataSource.class)
          ? dataSource.unwrap(HikariDataSource.class)
          : null;
    } catch (SQLException e) {
      return null;
    }
  }
}
