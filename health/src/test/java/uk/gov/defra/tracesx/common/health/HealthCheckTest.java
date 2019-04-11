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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(properties = {"permissionsScheme = http"})
public class HealthCheckTest {

//    private static final String SCHEME = "http";
//    private static final String HOST = "localhost";
//    private static final String PORT = "1234";
//    private static final String PERMISSIONS_SCHEME = "permissionsScheme";
//    private static final String PERMISSIONS_HOST = "permissionsHost";
//    private static final String PERMISSIONS_PORT = "permissionsPort";
//    private static final String PERMISSION_HEALTH_CHECK_PATH = "http://localhost:1234/admin/health-check";
//    private static final String DEPENDENT_SERVICE = "dependentService";
//
//    @Mock
//    private JdbcTemplate jdbcTemplate;
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    private List<String> checkDependentServices;
//
//    private HealthChecker healthCheck;
//    private Health health;
//
//    @Before
//    public void init() {
//        healthCheck = new HealthChecker(jdbcTemplate, restTemplate);
//        ReflectionTestUtils.setField(healthCheck, PERMISSIONS_SCHEME, SCHEME);
//        ReflectionTestUtils.setField(healthCheck, PERMISSIONS_HOST, HOST);
//        ReflectionTestUtils.setField(healthCheck, PERMISSIONS_PORT, PORT);
//        healthCheck.init();
//    }
//
//    @Test
//    public void whenPermissionsServiceIsUpThenHealthIsUp() {
//        givenNoDatabase();
//        givenPermissionsServicesIsUp();
//        givenNoDependentService();
//        whenWeCheckTheHealth();
//        thenWeExpectTheHealthToBeUp();
//    }
//
//    @Test
//    public void whenPermissionsServiceIsDownThenHealthIsDown() {
//        givenNoDatabase();
//        givenPermissionsServicesIsDown();
//        givenNoDependentService();
//        whenWeCheckTheHealth();
//        thenWeExpectTheHealthToBeDown();
//    }
//
//    @Test
//    public void whenDatabaseIsUpThenHealthIsUp() {
//        givenDatabaseIsUp();
//        givenPermissionsServicesIsUp();
//        givenDependentServicesIsUp();
//        whenWeCheckTheHealth();
//        thenWeExpectTheHealthToBeUp();
//    }
//
//    @Test
//    public void whenDatabaseIsDownThenHealthIsDown() {
//        givenDatabaseIsDown();
//        givenPermissionsServicesIsUp();
//        givenNoDependentService();
//        whenWeCheckTheHealth();
//        thenWeExpectTheHealthToBeDown();
//    }
//
//    @Test
//    public void whenDependentServiceIsUpThenHealthIsUp() {
//        givenNoDatabase();
//        givenPermissionsServicesIsUp();
//        givenDependentServicesIsUp();
//        whenWeCheckTheHealth();
//        thenWeExpectTheHealthToBeUp();
//    }
//
//    @Test
//    public void whenDependentServiceIsDownThenHealthIsDown() {
//        givenNoDatabase();
//        givenPermissionsServicesIsUp();
//        givenDependentServiceIsDown();
//        whenWeCheckTheHealth();
//        thenWeExpectTheHealthToBeDown();
//    }
//
//    @Test
//    public void whenNoDependentServiceThenHealthIsUp() {
//        givenDatabaseIsUp();
//        givenPermissionsServicesIsUp();
//        givenNoDependentService();
//        whenWeCheckTheHealth();
//        thenWeExpectTheHealthToBeUp();
//    }
//
//    private void givenDatabaseIsUp() {
//        when(jdbcTemplate.query(anyString(), any(SingleColumnRowMapper.class))).thenReturn(Arrays.asList("1"));
//    }
//
//    private void givenDatabaseIsDown() {
//        when(jdbcTemplate.query(anyString(), any(SingleColumnRowMapper.class))).thenReturn(Collections.emptyList());
//    }
//
//    private void givenDependentServicesIsUp() {
//        checkDependentServices = Arrays.asList(DEPENDENT_SERVICE);
//        when(restTemplate.exchange(eq(DEPENDENT_SERVICE), Mockito.<HttpMethod> eq(HttpMethod.GET),
//                Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any())).thenReturn(
//                new ResponseEntity(new HealthDto(Status.UP), HttpStatus.OK));
//    }
//
//    private void givenDependentServiceIsDown() {
//        checkDependentServices = Arrays.asList(DEPENDENT_SERVICE);
//        when(restTemplate.exchange(eq(DEPENDENT_SERVICE), Mockito.<HttpMethod> eq(HttpMethod.GET),
//                Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any())).thenReturn(
//                new ResponseEntity(new HealthDto(Status.DOWN), HttpStatus.OK));
//
//    }
//
//    private void givenPermissionsServicesIsUp() {
//        when(restTemplate.exchange(eq(PERMISSION_HEALTH_CHECK_PATH), Mockito.<HttpMethod> eq(HttpMethod.GET),
//                Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any())).thenReturn(
//                new ResponseEntity(new HealthDto(Status.UP), HttpStatus.OK));
//    }
//
//    private void givenPermissionsServicesIsDown() {
//        when(restTemplate.exchange(eq(PERMISSION_HEALTH_CHECK_PATH), Mockito.<HttpMethod> eq(HttpMethod.GET),
//                Mockito.<HttpEntity<?>> any(), Mockito.<Class<Object>> any())).thenReturn(
//                new ResponseEntity(new HealthDto(Status.DOWN), HttpStatus.OK));
//    }
//
//    private void givenNoDatabase() {
//        healthCheck = new HealthChecker(null, restTemplate);
//        ReflectionTestUtils.setField(healthCheck, PERMISSIONS_SCHEME, SCHEME);
//        ReflectionTestUtils.setField(healthCheck, PERMISSIONS_HOST, HOST);
//        ReflectionTestUtils.setField(healthCheck, PERMISSIONS_PORT, PORT);
//        healthCheck.init();
//    }
//
//    private void givenNoDependentService() {
//        checkDependentServices = Collections.emptyList();
//    }
//
//    private void whenWeCheckTheHealth() {
//        health = healthCheck.get(checkDependentServices);
//    }
//
//    private void thenWeExpectTheHealthToBeUp() {
//        assertThat(health.getStatus()).isEqualTo(Status.UP);
//    }
//
//    private void thenWeExpectTheHealthToBeDown() {
//        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
//    }
}
