package uk.gov.defra.tracesx.common.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import uk.gov.defra.tracesx.common.health.checks.JdbcHealthCheck;

import java.util.ArrayList;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class JdbcHealthCheckTest {

  @Mock private JdbcTemplate jdbcTemplate;

  @Test
  void willReturnHealthyWhenCalled() {

    when(jdbcTemplate.query(eq("select 1"), any(SingleColumnRowMapper.class)))
        .thenReturn(Arrays.asList("1"));
    Health health = new JdbcHealthCheck(jdbcTemplate).check();

    assertEquals(health, Health.up().build());
  }

  @Test
  void willReturnUnHealthyWhenCalled() {

    when(jdbcTemplate.query(eq("select 1"), any(SingleColumnRowMapper.class)))
        .thenReturn(new ArrayList());
    Health health = new JdbcHealthCheck(jdbcTemplate).check();

    assertEquals(health.getStatus(), Health.down().build().getStatus());
  }

  @Test
  void willReturnUnHealthy_WhenThrowException() {

    when(jdbcTemplate.query(eq("select 1"), any(SingleColumnRowMapper.class)))
        .thenThrow(new DataAccessException("message"){});
    Health health = new JdbcHealthCheck(jdbcTemplate).check();

    assertEquals(health.getStatus(), Health.down().build().getStatus());
  }
}
