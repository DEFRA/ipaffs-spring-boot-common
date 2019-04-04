package uk.gov.defra.tracesx.common.health;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import uk.gov.defra.tracesx.common.health.checks.JdbcHealthCheck;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JdbcHealthCheckTest {

  @Mock private JdbcTemplate jdbcTemplate;

  @Test
  public void willReturnHealthyWhenCalled() {

    when(jdbcTemplate.query(eq("select 1"), any(SingleColumnRowMapper.class)))
        .thenReturn(Arrays.asList("1"));
    Health health = new JdbcHealthCheck(jdbcTemplate).check();

    assertEquals(health, Health.up().build());
  }

  @Test
  public void willReturnUnHealthyWhenCalled() {

    when(jdbcTemplate.query(eq("select 1"), any(SingleColumnRowMapper.class)))
        .thenReturn(new ArrayList());
    Health health = new JdbcHealthCheck(jdbcTemplate).check();

    assertEquals(health.getStatus(), Health.down().build().getStatus());
  }
}
