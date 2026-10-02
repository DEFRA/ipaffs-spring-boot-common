package uk.gov.defra.tracesx.common.health.checks;

import org.springframework.boot.health.contributor.Health;

public interface CheckHealth {

  String getName();

  Health check();
}
