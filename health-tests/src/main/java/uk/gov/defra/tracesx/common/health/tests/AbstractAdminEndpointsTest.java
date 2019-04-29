package uk.gov.defra.tracesx.common.health.tests;

import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static uk.gov.defra.tracesx.common.health.tests.RequestHelper.getUrl;

public abstract class AbstractAdminEndpointsTest {

  @Test
  public void testAdminHealthEndpointWithNoAuthentication() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get(getUrl("/admin/health-check"))
        .then()
        .assertThat()
        .statusCode(200)
        .body(containsString("status"));
  }

  @Test
  public void testAdminInfoEndpointWithNoAuthentication() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get(getUrl("/admin/info"))
        .then()
        .assertThat()
        .statusCode(200)
        .body(containsString("version"));
  }
}
