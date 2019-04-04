package uk.gov.defra.tracesx.common.health;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionsHealthCheckTest {

    private static final String SCHEME = "http";
    private static final String HOST = "localhost";
    private static final String PORT = "1234";
    private static final String PERMISSIONS_SCHEME = "permissionsScheme";
    private static final String PERMISSIONS_HOST = "permissionsHost";
    private static final String PERMISSIONS_PORT = "permissionsPort";
    private static final String PERMISSION_HEALTH_CHECK_PATH = "http://localhost:1234/admin/health-check";

    @Mock
    private RestTemplate restTemplate;

    private PermissionsHealthCheck permissionsHealthCheck;
    private Health health;

    @Before
    public void init() {
        permissionsHealthCheck = new PermissionsHealthCheck(restTemplate, PERMISSIONS_SCHEME, PERMISSIONS_HOST, PERMISSIONS_PORT);
        ReflectionTestUtils.setField(permissionsHealthCheck, PERMISSIONS_SCHEME, SCHEME);
        ReflectionTestUtils.setField(permissionsHealthCheck, PERMISSIONS_HOST, HOST);
        ReflectionTestUtils.setField(permissionsHealthCheck, PERMISSIONS_PORT, PORT);
    }

    @Test
    public void whenPermissionsServiceIsUpThenHealthIsUp() {
        givenPermissionsServicesIsUp();
        whenWeCheckTheHealth();
        thenWeExpectTheHealthToBeUp();
    }

    @Test
    public void whenPermissionsServiceIsDownThenHealthIsDown() {
        givenPermissionsServicesIsDown();
        whenWeCheckTheHealth();
        thenWeExpectTheHealthToBeDown();
    }

    @Test
    public void testWhenGetHealthCheckPathIsCalledThenReturnHealthCheckPathWithCorrectSetValues() {
        assertThat(permissionsHealthCheck.getHealthCheckPath())
                .isEqualTo(PERMISSION_HEALTH_CHECK_PATH);
    }

    private void givenPermissionsServicesIsUp() {
        when(restTemplate.exchange(eq(PERMISSION_HEALTH_CHECK_PATH), Mockito.<HttpMethod> eq(HttpMethod.GET),
                Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any())).thenReturn(
                new ResponseEntity(new HealthDto(Status.UP), HttpStatus.OK));
    }

    private void givenPermissionsServicesIsDown() {
        when(restTemplate.exchange(eq(PERMISSION_HEALTH_CHECK_PATH), Mockito.<HttpMethod> eq(HttpMethod.GET),
                Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any())).thenReturn(
                new ResponseEntity(new HealthDto(Status.DOWN), HttpStatus.OK));
    }

    private void whenWeCheckTheHealth() {
        health = permissionsHealthCheck.get();
    }

    private void thenWeExpectTheHealthToBeUp() {
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    private void thenWeExpectTheHealthToBeDown() {
        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
    }
}
