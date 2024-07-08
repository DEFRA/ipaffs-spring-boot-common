package uk.gov.defra.tracesx.common.health.tests;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.is;
import static uk.gov.defra.tracesx.common.health.tests.RequestHelper.getUrl;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

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
        .body(is(emptyString()));
  }
}
