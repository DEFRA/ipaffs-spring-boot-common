package uk.gov.defra.tracesx.common.health;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import uk.gov.defra.tracesx.common.health.checks.CheckHealth;
import uk.gov.defra.tracesx.common.health.checks.JdbcHealthCheck;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthCheck;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckerTest {

  @Mock private JdbcHealthCheck jdbcHealthCheck;
  @Mock private HttpHealthCheck httpHealthCheck;

  @Test
  public void willReportUpWhenAllChecksAreHealthy() {

    when(jdbcHealthCheck.check()).thenReturn(Health.up().build());
    when(httpHealthCheck.check()).thenReturn(Health.up().build());

    List<CheckHealth> checks = Arrays.asList(jdbcHealthCheck, httpHealthCheck);

    Health health = new HealthChecker(checks).health();
    assertThat(health, is(Health.up().build()));
  }

  @Test
  public void willReportDownWhenOneServiceIsDown() {

    when(jdbcHealthCheck.check()).thenReturn(Health.down().build());
    when(httpHealthCheck.check()).thenReturn(Health.up().build());

    List<CheckHealth> checks = Arrays.asList(jdbcHealthCheck, httpHealthCheck);

    Health health = new HealthChecker(checks).health();
    assertThat(health, is(Health.down().build()));
  }
}
