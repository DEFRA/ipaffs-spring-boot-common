package uk.gov.defra.tracesx.common.health.checks;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.common.health.checks.util.JdbcHealthCheckUtil;

@Component
@ConditionalOnProperty(name = "spring.datasource.private.jdbcUrl")
public class PrivateJdbcHealthCheck implements CheckHealth {

  private JdbcTemplate privateJdbcTemplate;

  public PrivateJdbcHealthCheck(JdbcTemplate privateJdbcTemplate) {
    this.privateJdbcTemplate = privateJdbcTemplate;
  }

  @Override
  public String getName() {
    return "Private Jdbc Health Check";
  }

  @Override
  public Health check() {
    return JdbcHealthCheckUtil.performCheck(getName(), privateJdbcTemplate);
  }
}