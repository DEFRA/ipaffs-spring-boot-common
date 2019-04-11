package uk.gov.defra.tracesx.common.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HealthChecker {

    private List<Check> checks;

    @Autowired
    public HealthChecker(List<Check> checks) {
        this.checks = checks;
    }

    public Health get() {
        List<Check> failed = checks.stream().filter(x -> x.check()
                .equals(Health.down().build())).collect(Collectors.toList());

        //if you want concurrent
//        List<Check> failed = checks.parallelStream().filter(x -> x.check()
//                .equals(Health.down().build())).collect(Collectors.toList());

        //maybe log out the failures
        return failed.isEmpty() ? Health.up().build() : Health.down().build();
    }

}
