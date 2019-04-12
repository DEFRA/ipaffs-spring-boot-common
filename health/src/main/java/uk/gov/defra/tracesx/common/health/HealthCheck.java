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
import java.util.Collections;
import java.util.List;

public class HealthCheck {

  private RestTemplate healthCheckRestTemplate;
  private JdbcTemplate jdbcTemplate;
  private PermissionsHealthCheckUrlBuilder permissionsHealthCheckUrlBuilder;
  private List<String> dependentServiceHealthCheckUrls;

  @Value("${permissions.service.scheme:http}")
  private String permissionsScheme;

  @Value("${permissions.service.host:localhost}")
  private String permissionsHost;

  @Value("${permissions.service.port:8000}")
  private String permissionsPort;

  private boolean permissionsHealthCheck;

  public HealthCheck(
      JdbcTemplate jdbcTemplate,
      RestTemplate healthCheckRestTemplate,
      List<String> dependentServiceHealthCheckUrls, boolean permissionHealthCheck) {
    this.permissionsHealthCheck = permissionHealthCheck;
    this.healthCheckRestTemplate = healthCheckRestTemplate;
    this.jdbcTemplate = jdbcTemplate;
    this.dependentServiceHealthCheckUrls = dependentServiceHealthCheckUrls;
  }

  public HealthCheck(
          JdbcTemplate jdbcTemplate) {
    this.permissionsHealthCheck = false;
    this.jdbcTemplate = jdbcTemplate;
    this.dependentServiceHealthCheckUrls = new ArrayList<>();
  }

  public Health get() {
    Health health;

    if (checkDatabase(jdbcTemplate)) {
      health = checkDatabaseHealth(jdbcTemplate);
      if (health.getStatus().equals(Status.DOWN)) {
        return health;
      }
    }
    dependentServiceHealthCheckUrls = addDependentServiceUrls(dependentServiceHealthCheckUrls);

    if (permissionsHealthCheck) {
        dependentServiceHealthCheckUrls
                .add(new PermissionsHealthCheckUrlBuilder(permissionsScheme, permissionsHost, permissionsPort)
                        .buildUrl());
    }
    return checkDependentServicesHealth(dependentServiceHealthCheckUrls);
  }

  private List<String> addDependentServiceUrls(List<String> dependentServiceHealthCheckUrls) {
    return
        dependentServiceHealthCheckUrls != null
            ? new ArrayList<>(dependentServiceHealthCheckUrls)
            : new ArrayList<>();
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
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    HttpEntity<String> entity = new HttpEntity<>(headers);

    for (String dependentServiceHealthCheckUrl : dependentServiceHealthCheckUrls) {
      try {
        ResponseEntity response =
            healthCheckRestTemplate.exchange(
                dependentServiceHealthCheckUrl, HttpMethod.GET, entity, String.class);

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
