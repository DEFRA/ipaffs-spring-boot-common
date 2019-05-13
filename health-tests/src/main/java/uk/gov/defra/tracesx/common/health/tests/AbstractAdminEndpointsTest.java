package uk.gov.defra.tracesx.common.health.tests;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.containsString;
import static uk.gov.defra.tracesx.common.health.tests.RequestHelper.getUrl;

import io.restassured.http.ContentType;
import org.junit.Test;

public abstract class AbstractAdminEndpointsTest {

  @Test
  public final void testAdminHealthEndpointWithNoAuthentication() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get(getUrl("/admin/health-check"))
        .then()
        .assertThat()
        .statusCode(SC_OK)
        .body(containsString("status"));
  }

  @Test
  public final void testAdminInfoEndpointWithNoAuthentication() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get(getUrl("/admin/info"))
        .then()
        .assertThat()
        .statusCode(SC_OK)
        .body(containsString("version"));
  }
}
