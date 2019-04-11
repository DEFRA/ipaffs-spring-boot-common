package uk.gov.defra.tracesx.common.health.checks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.defra.tracesx.common.health.Check;
import uk.gov.defra.tracesx.common.health.HealthDto;

import java.util.Collections;

@Component
public class PermissionsCheck implements Check {

    @Value("${permissions.service.scheme}")
    private String permissionsScheme;

    @Value("${permissions.service.host}")
    private String permissionsHost;

    @Value("${permissions.service.port}")
    private String permissionsPort;

    private RestTemplate healthCheckRestTemplate;
    private static final String HEALTH_CHECK = "/admin/health-check";

    @Autowired
    public PermissionsCheck(RestTemplate healthCheckRestTemplate) {
        this.healthCheckRestTemplate = healthCheckRestTemplate;
    }

    @Override
    public Health check() {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        try {
            ResponseEntity response = healthCheckRestTemplate.exchange(getHealthCheckPath(), HttpMethod.GET, entity, HealthDto.class);
            return response.getStatusCode() == HttpStatus.OK ? Health.down().build() : Health.up().build();
        } catch (Exception e) {
            return Health.down().build();
        }
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
