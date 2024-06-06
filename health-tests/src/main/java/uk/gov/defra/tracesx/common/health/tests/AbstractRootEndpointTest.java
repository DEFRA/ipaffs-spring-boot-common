package uk.gov.defra.tracesx.common.health.tests;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static uk.gov.defra.tracesx.common.health.tests.RequestHelper.getUrl;

import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Test;

public abstract class AbstractRootEndpointTest {

  @Test
  public final void testRootEndpointWithNoAuthentication() {
    given()
        .contentType(ContentType.JSON)
        .when()
        .get(getUrl("/"))
        .then()
        .assertThat()
        .statusCode(SC_OK)
        .body(Matchers.isEmptyString());
  }
}
