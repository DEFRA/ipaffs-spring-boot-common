package uk.gov.defra.tracesx.common.health.checks;

import org.springframework.boot.actuate.health.Health;

public interface CheckHealth {

    String getName();

    Health check();
}
