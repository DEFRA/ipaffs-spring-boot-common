package uk.gov.defra.tracesx.common.health.checks.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.util.List;

public class JdbcHealthCheckUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(JdbcHealthCheckUtil.class);

  public static Health performCheck(String name, JdbcTemplate jdbcTemplate) {
    Health health;

    try {
      List<Object> results = jdbcTemplate.query("select 1", new SingleColumnRowMapper<>());
      if (results.size() != 1) {
        LOGGER.error("{} failed simple query check {}", name);
        health = Health.down().withDetail(name, "Unavailable").build();
      } else {
        health = Health.up().build();
      }
    } catch (DataAccessException exception) {
      LOGGER.error("{} failed with message {}", name, exception.getMessage());
      health = Health.down().build();
    }
    return health;
  }
}
