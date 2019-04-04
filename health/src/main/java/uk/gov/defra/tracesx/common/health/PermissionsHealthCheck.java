package uk.gov.defra.tracesx.common.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

public class PermissionsHealthCheck {

    private RestTemplate healthCheckRestTemplate;
    private static final String HEALTH_CHECK = "/admin/health-check";

    private String permissionsScheme;

    private String permissionsHost;

    private String permissionsPort;

    public PermissionsHealthCheck(RestTemplate healthCheckRestTemplate, String permissionsScheme, String permissionsHost, String permissionsPort) {
        this.healthCheckRestTemplate = healthCheckRestTemplate;
        this.permissionsScheme = permissionsScheme;
        this.permissionsHost = permissionsHost;
        this.permissionsPort = permissionsPort;
    }

    public Health get() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        try {
            ResponseEntity response = healthCheckRestTemplate.exchange(getHealthCheckPath(), HttpMethod.GET, entity, HealthDto.class);

            HealthDto healthDto = response.getStatusCode() == HttpStatus.OK ? (HealthDto) response.getBody() : null;
            if (healthDto == null || Status.DOWN.equals(healthDto.getStatus())) {
                return Health.down().build();
            }

        } catch (Exception e) {
            return Health.down().build();
        }

        return Health.up().build();
    }

    public String getHealthCheckPath() {
        UriComponentsBuilder uri = UriComponentsBuilder.newInstance()
                .scheme(permissionsScheme)
                .host(permissionsHost)
                .port(permissionsPort)
                .path(HEALTH_CHECK);
        return uri.toUriString();
    }
}
