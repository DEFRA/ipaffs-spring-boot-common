package uk.gov.defra.tracesx.common.health.checks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "spring.datasource.url")
public class JdbcHealthCheck implements CheckHealth {

  private JdbcTemplate jdbcTemplate;

  private static final Logger LOGGER = LoggerFactory.getLogger(JdbcHealthCheck.class);

  public JdbcHealthCheck(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public String getName() {
    return "Jdbc Health Check";
  }

  @Override
  public Health check() {
    Health health;

    try {
      List<Object> results = jdbcTemplate.query("select 1", new SingleColumnRowMapper<>());
      if (results.size() != 1) {
        LOGGER.error("{} failed simple query check", getName());
        health = Health.down().withDetail(getName(), "Unavailable").build();
      } else {
        health = Health.up().build();
      }
    } catch (DataAccessException exception) {
      LOGGER.error("{} failed with message {}", getName(), exception.getMessage());
      health = Health.down().build();
    }
    return health;
  }
}
