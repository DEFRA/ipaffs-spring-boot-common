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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(properties = {"permissionsScheme = http"})
public class HealthCheckTest {

    private static final String SCHEME = "http";
    private static final String HOST = "localhost";
    private static final String PORT = "1234";
    private static final String PERMISSIONS_SCHEME = "permissionsScheme";
    private static final String PERMISSIONS_HOST = "permissionsHost";
    private static final String PERMISSIONS_PORT = "permissionsPort";
    private static final String PERMISSIONS_SERVICE = "http://localhost:1234/admin/health-check";
    private static final String DEPENDENT_SERVICE_1 = "dependentService_1";
    private static final String DEPENDENT_SERVICE_2 = "dependentService_2";

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private RestTemplate restTemplate;

    private List<String> dependentServiceHealthCheckUrls;

    private HealthCheck healthCheck;
    private Health health;

    @Before
    public void init() {
        dependentServiceHealthCheckUrls = Arrays.asList(PERMISSIONS_SERVICE, DEPENDENT_SERVICE_1, DEPENDENT_SERVICE_2);
        healthCheck = new HealthCheck(jdbcTemplate, restTemplate, dependentServiceHealthCheckUrls, true);
    }

    @Test
    public void whenDatabaseIsUpThenHealthIsUp() {
        givenDatabaseIsUp();
        givenServiceIsUp(PERMISSIONS_SERVICE);
        givenNoDependentService();
        whenWeCheckTheHealth();
        thenWeExpectTheHealthToBe(Status.UP);
    }

    @Test
    public void whenDatabaseIsDownThenHealthIsDown() {
        givenDatabaseIsDown();
        whenWeCheckTheHealth();
        thenWeExpectNoServiceCheckIsCalled(PERMISSIONS_SERVICE);
        thenWeExpectNoServiceCheckIsCalled(DEPENDENT_SERVICE_1);
        thenWeExpectNoServiceCheckIsCalled(DEPENDENT_SERVICE_2);
        thenWeExpectTheHealthToBe(Status.DOWN);
    }

    @Test
    public void whenPermissionsServiceIsUpThenHealthIsUp() {
        givenNoDatabaseAndNoDependentServices();
        givenServiceIsUp(PERMISSIONS_SERVICE);
        whenWeCheckTheHealth();
        thenWeExpectTheHealthToBe(Status.UP);
    }

    @Test
    public void whenPermissionsServiceIsDownThenHealthIsDown() {
        givenNoDatabaseAndNoDependentServices();
        givenServiceIsDown(PERMISSIONS_SERVICE);
        whenWeCheckTheHealth();
        thenWeExpectTheHealthToBe(Status.DOWN);
    }

    @Test
    public void whenDependentServiceIsUpThenHealthIsUp() {
        givenNoDatabase();
        givenServiceIsUp(PERMISSIONS_SERVICE);
        givenServiceIsUp(DEPENDENT_SERVICE_1);
        givenServiceIsUp(DEPENDENT_SERVICE_2);
        whenWeCheckTheHealth();
        thenWeExpectTheHealthToBe(Status.UP);
    }

    @Test
    public void whenDependentServiceIsDownThenHealthIsDown() {
        givenNoDatabase();
        givenServiceIsUp(PERMISSIONS_SERVICE);
        givenServiceIsDown(DEPENDENT_SERVICE_1);
        whenWeCheckTheHealth();
        thenWeExpectNoServiceCheckIsCalled(DEPENDENT_SERVICE_2);
        thenWeExpectTheHealthToBe(Status.DOWN);
    }

    @Test
    public void whenNoPermissionsCheckIsRequired() {
        dependentServiceHealthCheckUrls = Arrays.asList(PERMISSIONS_SERVICE, DEPENDENT_SERVICE_1, DEPENDENT_SERVICE_2);
        healthCheck = new HealthCheck(jdbcTemplate);

        setPermissionsCheckConfig();
        givenDatabaseIsUp();
        givenServiceIsUp(PERMISSIONS_SERVICE);
        whenWeCheckTheHealth();
        thenWeExpectNoServiceCheckIsCalled(PERMISSIONS_SERVICE);
        thenWeExpectTheHealthToBe(Status.UP);
    }

    private void givenDatabaseIsUp() {
        when(jdbcTemplate.query(anyString(), any(SingleColumnRowMapper.class))).thenReturn(Arrays.asList("1"));
    }

    private void givenDatabaseIsDown() {
        when(jdbcTemplate.query(anyString(), any(SingleColumnRowMapper.class))).thenReturn(Collections.emptyList());
    }

    private void givenServiceIsUp(String service) {
        when(restTemplate.exchange(eq(service), Mockito.<HttpMethod> eq(HttpMethod.GET),
                Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any())).thenReturn(
                new ResponseEntity<>(Status.UP, HttpStatus.OK));
    }

    private void givenServiceIsDown(String service) {
        when(restTemplate.exchange(eq(service), Mockito.<HttpMethod> eq(HttpMethod.GET),
                Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any())).thenReturn(
                new ResponseEntity<>(Status.DOWN, HttpStatus.SERVICE_UNAVAILABLE));
    }

    private void givenNoDatabaseAndNoDependentServices() {
        dependentServiceHealthCheckUrls = Collections.singletonList(PERMISSIONS_SERVICE);
        healthCheck = new HealthCheck(null, restTemplate, dependentServiceHealthCheckUrls, true);
        setPermissionsCheckConfig();
    }

    private void givenNoDatabase() {
        dependentServiceHealthCheckUrls = Arrays.asList(PERMISSIONS_SERVICE, DEPENDENT_SERVICE_1, DEPENDENT_SERVICE_2);
        healthCheck = new HealthCheck(null, restTemplate, dependentServiceHealthCheckUrls, true);
        setPermissionsCheckConfig();
    }

    private void givenNoDependentService() {
        dependentServiceHealthCheckUrls = Collections.singletonList(PERMISSIONS_SERVICE);
        healthCheck = new HealthCheck(jdbcTemplate, restTemplate, dependentServiceHealthCheckUrls, true);
        setPermissionsCheckConfig();
    }

    private void whenWeCheckTheHealth() {
        health = healthCheck.get();
    }

    private void thenWeExpectTheHealthToBe(Status status) {
        assertThat(health.getStatus()).isEqualTo(status);
    }

    private void thenWeExpectNoServiceCheckIsCalled(String service) {
        verify(restTemplate, never()).exchange(eq(service), Mockito.<HttpMethod> eq(HttpMethod.GET),
                Mockito.any(), Mockito.<Class<Object>> any());

    }

    private void setPermissionsCheckConfig() {
      ReflectionTestUtils.setField(healthCheck, PERMISSIONS_SCHEME, SCHEME);
      ReflectionTestUtils.setField(healthCheck, PERMISSIONS_HOST, HOST);
      ReflectionTestUtils.setField(healthCheck, PERMISSIONS_PORT, PORT);
    }
}
