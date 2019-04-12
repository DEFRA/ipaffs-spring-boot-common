package uk.gov.defra.tracesx.common.health;

import org.springframework.web.util.UriComponentsBuilder;

public class PermissionsHealthCheckUrlBuilder {

    private static final String HEALTH_CHECK = "/admin/health-check";

    private String permissionsScheme;

    private String permissionsHost;

    private String permissionsPort;

    public PermissionsHealthCheckUrlBuilder(String permissionsScheme, String permissionsHost, String permissionsPort) {
        this.permissionsScheme = permissionsScheme;
        this.permissionsHost = permissionsHost;
        this.permissionsPort = permissionsPort;
    }

    public String buildUrl() {
        UriComponentsBuilder uri = UriComponentsBuilder.newInstance()
                .scheme(permissionsScheme)
                .host(permissionsHost)
                .port(permissionsPort)
                .path(HEALTH_CHECK);
        return uri.toUriString();
    }
}
