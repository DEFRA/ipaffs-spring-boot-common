package uk.gov.defra.tracesx.common.health.checks;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.common.health.checks.util.JdbcHealthCheckUtil;

@Component
@ConditionalOnProperty({"spring.datasource.public.jdbcUrl", "spring.datasource.multiple-enabled"})
public class PublicJdbcHealthCheck implements CheckHealth {

  private JdbcTemplate publicJdbcTemplate;

  public PublicJdbcHealthCheck(JdbcTemplate publicJdbcTemplate) {
    this.publicJdbcTemplate = publicJdbcTemplate;
  }

  @Override
  public String getName() {
    return "Public Jdbc Health Check";
  }

  @Override
  public Health check() {
    return JdbcHealthCheckUtil.performCheck(getName(), publicJdbcTemplate);
  }
}