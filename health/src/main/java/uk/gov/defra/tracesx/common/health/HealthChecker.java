package uk.gov.defra.tracesx.common.health;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import uk.gov.defra.tracesx.common.health.checks.CheckHealth;

public class HealthChecker implements HealthIndicator {

  private List<CheckHealth> checks;

  private static final Logger LOGGER = LoggerFactory.getLogger(HealthChecker.class);

  public HealthChecker(List<CheckHealth> checks) {
    this.checks = checks;
  }

  @Override
  public Health health() {
    List<CheckHealth> failed =
        checks.parallelStream().filter(failCheck()).collect(Collectors.toList());
    return failed.isEmpty() ? Health.up().build() : Health.down().build();
  }

  private Predicate<CheckHealth> failCheck() {
    return check -> {
      Health health = check.check();
      if (health.getStatus().equals(Status.DOWN)) {
        LOGGER.error("{} is unhealthy - reason: {}", check.getName(), health);
        return true;
      }
      return false;
    };
  }
}
