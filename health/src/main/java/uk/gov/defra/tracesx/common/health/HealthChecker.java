package uk.gov.defra.tracesx.common.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import uk.gov.defra.tracesx.common.health.checks.CheckHealth;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class HealthChecker {

    private List<CheckHealth> checks;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public HealthChecker(List<CheckHealth> checks) {
        this.checks = checks;
    }

    public Health get() {
        //if you want concurrent use checks.parallelStream().filter
        List<CheckHealth> failed = checks.stream().filter(failCheck())
                .collect(Collectors.toList());

        return failed.isEmpty() ? Health.up().build() : Health.down().build();
    }

    private Predicate<CheckHealth> failCheck(){
        return check -> {
            if(check.check().equals(Health.down().build())){
                LOGGER.error("{} failed the health check", check.getName());
                return false;
            }
            return true;
        };
    }
}
