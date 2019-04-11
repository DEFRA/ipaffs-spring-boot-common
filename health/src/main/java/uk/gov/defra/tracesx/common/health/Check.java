package uk.gov.defra.tracesx.common.health;

import org.springframework.boot.actuate.health.Health;

public interface Check {

    Health check();
}
