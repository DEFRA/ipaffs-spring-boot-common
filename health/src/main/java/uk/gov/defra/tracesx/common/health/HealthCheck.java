package uk.gov.defra.tracesx.common.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HealthCheck {

    private RestTemplate healthCheckRestTemplate;
    private JdbcTemplate jdbcTemplate;
    private PermissionsHealthCheckUrlBuilder permissionsHealthCheckUrlBuilder;
    private List<String> dependentServiceHealthCheckUrls;

    @Value("${permissions.service.scheme}")
    private String permissionsScheme;

    @Value("${permissions.service.host}")
    private String permissionsHost;

    @Value("${permissions.service.port}")
    private String permissionsPort;

    public HealthCheck(JdbcTemplate jdbcTemplate, RestTemplate healthCheckRestTemplate, List<String> dependentServiceHealthCheckUrls) {
        this.healthCheckRestTemplate = healthCheckRestTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.dependentServiceHealthCheckUrls = dependentServiceHealthCheckUrls;
    }

    @PostConstruct
    public void init() {
        this.permissionsHealthCheckUrlBuilder = new PermissionsHealthCheckUrlBuilder(
                permissionsScheme, permissionsHost, permissionsPort);
    }

    public Health get() {
        Health health;

        if (checkDatabase(jdbcTemplate)) {
            health = checkDatabaseHealth(jdbcTemplate);
            if (health.getStatus().equals(Status.DOWN)) {
                return health;
            }
        }

        dependentServiceHealthCheckUrls = addPermissionsService(dependentServiceHealthCheckUrls);

        health = checkDependentServicesHealth(dependentServiceHealthCheckUrls);

        return health;
    }

    private List<String> addPermissionsService(List<String> dependentServiceHealthCheckUrls) {
        List<String> healthCheckUrls = dependentServiceHealthCheckUrls != null ? new ArrayList<>(dependentServiceHealthCheckUrls) : new ArrayList<>();
        healthCheckUrls.add(permissionsHealthCheckUrlBuilder.buildUrl());
        return healthCheckUrls;
    }

    private Health checkDatabaseHealth(JdbcTemplate jdbcTemplate) {
        Health health = Health.up().build();

        try {
            List<Object> results = jdbcTemplate.query("select 1", new SingleColumnRowMapper<>());
            if (results.size() != 1) {
                health = Health.down().build();
            }
        } catch (Exception e) {
            health = Health.down().build();
        }

        return health;
    }

    private boolean checkDatabase(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate != null;
    }

    private Health checkDependentServicesHealth(List<String> dependentServiceHealthCheckUrls) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        for (String dependentServiceHealthCheckUrl : dependentServiceHealthCheckUrls) {
            try {
                ResponseEntity response = healthCheckRestTemplate.exchange(dependentServiceHealthCheckUrl, HttpMethod.GET, entity, String.class);

                if (response.getStatusCode() != HttpStatus.OK) {
                    return Health.down().build();
                }

            } catch (Exception e) {
                return Health.down().build();
            }
        }
        return Health.up().build();
    }

}
