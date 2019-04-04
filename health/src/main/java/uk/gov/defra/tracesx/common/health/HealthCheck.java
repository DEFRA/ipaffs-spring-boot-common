package uk.gov.defra.tracesx.common.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.*;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

public class HealthCheck {

    private RestTemplate healthCheckRestTemplate;
    private JdbcTemplate jdbcTemplate;
    private PermissionsHealthCheck permissionsHealthCheck;

    @Value("${permissions.service.scheme}")
    private String permissionsScheme;

    @Value("${permissions.service.host}")
    private String permissionsHost;

    @Value("${permissions.service.port}")
    private String permissionsPort;


    public HealthCheck(JdbcTemplate jdbcTemplate, RestTemplate healthCheckRestTemplate) {
        this.healthCheckRestTemplate = healthCheckRestTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        this.permissionsHealthCheck = new PermissionsHealthCheck(
                healthCheckRestTemplate, permissionsScheme, permissionsHost, permissionsPort);
    }

    public Health get(List<String> checkDependentServices) {
        Health health = permissionsHealthCheck.get();
        if (health.getStatus().equals(Status.DOWN)) {
            return health;
        }

        if (checkDatabase(jdbcTemplate)) {
            health = checkDatabaseHealth(jdbcTemplate);
            if (health.getStatus().equals(Status.DOWN)) {
                return health;
            }
        }

        health = checkDependentServicesHealth(checkDependentServices);

        return health;
    }

    private Health checkDatabaseHealth(JdbcTemplate jdbcTemplate) {
        Health health;

        try {
            List<Object> results = jdbcTemplate.query("select 1", new SingleColumnRowMapper<>());
            if (results.size() != 1) {
                health = Health.down().build();
            } else {
                health = Health.up().build();
            }
        } catch (CannotGetJdbcConnectionException e) {
            health = Health.down().build();
        }

        return health;
    }

    private boolean checkDatabase(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate != null;
    }

    private Health checkDependentServicesHealth(List<String> checkDependentServices) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        for (String dependentService : checkDependentServices) {
            try {
                ResponseEntity response = healthCheckRestTemplate.exchange(dependentService, HttpMethod.GET, entity, HealthDto.class);

                HealthDto healthDto = response.getStatusCode() == HttpStatus.OK ? (HealthDto) response.getBody() : null;
                if (healthDto == null || Status.DOWN.equals(healthDto.getStatus())) {
                    return Health.down().build();
                }

            } catch (Exception e) {
                return Health.down().build();
            }
        }
        return Health.up().build();
    }

}
