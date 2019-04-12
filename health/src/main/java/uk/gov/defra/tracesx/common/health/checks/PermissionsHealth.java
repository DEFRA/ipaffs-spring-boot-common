package uk.gov.defra.tracesx.common.health.checks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.defra.tracesx.common.health.HttpCheckStatus;

import java.util.Collections;

import static uk.gov.defra.tracesx.common.health.Constants.HEALTH_CHECK;

@Component
@ConditionalOnProperty("{requires.permission.check:true}")
public class PermissionsHealth implements CheckHealth {

    private String permissionsScheme;
    private String permissionsHost;
    private String permissionsPort;
    private RestTemplate healthCheckRestTemplate;

    @Autowired
    public PermissionsHealth(RestTemplate healthCheckRestTemplate,
                             @Value("${permissions.service.scheme}") String permissionsScheme,
                             @Value("${permissions.service.host}") String permissionsHost,
                             @Value("${permissions.service.port}") String permissionsPort) {
        this.healthCheckRestTemplate = healthCheckRestTemplate;
        this.permissionsScheme = permissionsScheme;
        this.permissionsHost = permissionsHost;
        this.permissionsPort = permissionsPort;
    }

    @Override
    public String getName() {
        return "Permission Health Check";
    }

    @Override
    public Health check() {

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        try {
            ResponseEntity response = healthCheckRestTemplate.exchange(buildUrl(), HttpMethod.GET, entity, HttpCheckStatus.class);
            return response.getStatusCode() == HttpStatus.OK ? Health.down().build() : Health.up().build();
        } catch (Exception e) {
            return Health.down().build();
        }
    }

    private String buildUrl() {
        UriComponentsBuilder uri = UriComponentsBuilder.newInstance()
                .scheme(permissionsScheme)
                .host(permissionsHost)
                .port(permissionsPort)
                .path(HEALTH_CHECK);
        return uri.toUriString();
    }
}
