package uk.gov.defra.tracesx.common.health.tests;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static uk.gov.defra.tracesx.common.health.tests.RequestHelper.getUrl;

public abstract class AbstractRootEndpointTest {

  @Test
  public void testRootEndpointWithNoAuthentication() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get(getUrl("/"))
        .then()
        .assertThat()
        .statusCode(200)
        .body(Matchers.isEmptyString());
  }
}
