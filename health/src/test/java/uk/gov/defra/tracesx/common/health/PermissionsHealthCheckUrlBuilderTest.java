package uk.gov.defra.tracesx.common.health;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class PermissionsHealthCheckUrlBuilderTest {

    private static final String SCHEME = "http";
    private static final String HOST = "localhost";
    private static final String PORT = "1234";
    private static final String PERMISSIONS_SCHEME = "permissionsScheme";
    private static final String PERMISSIONS_HOST = "permissionsHost";
    private static final String PERMISSIONS_PORT = "permissionsPort";
    private static final String PERMISSION_HEALTH_CHECK_PATH = "http://localhost:1234/admin/health-check";

    private PermissionsHealthCheckUrlBuilder permissionsHealthCheckUrlBuilder;

    @Before
    public void init() {
        permissionsHealthCheckUrlBuilder = new PermissionsHealthCheckUrlBuilder(PERMISSIONS_SCHEME, PERMISSIONS_HOST, PERMISSIONS_PORT);
        ReflectionTestUtils.setField(permissionsHealthCheckUrlBuilder, PERMISSIONS_SCHEME, SCHEME);
        ReflectionTestUtils.setField(permissionsHealthCheckUrlBuilder, PERMISSIONS_HOST, HOST);
        ReflectionTestUtils.setField(permissionsHealthCheckUrlBuilder, PERMISSIONS_PORT, PORT);
    }

    @Test
    public void testWhenBuildUrlIsCalledThenReturnHealthCheckPath() {
        assertThat(permissionsHealthCheckUrlBuilder.buildUrl())
                .isEqualTo(PERMISSION_HEALTH_CHECK_PATH);
    }
}
