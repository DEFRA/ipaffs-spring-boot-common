package uk.gov.defra.tracesx.common.health.http;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthParams;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class HttpHealthParamsTest {

  @Test
  public void willCorrectlyInitialize() {

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> httpEntity = new HttpEntity<>("{}");
    String url = "http://localhost:8000/foo";
    String name = "test service";

    HttpHealthParams params = new HttpHealthParams(url, name, httpEntity, HttpMethod.POST);

    assertEquals(params.getUrl(), url);
    assertEquals(params.getName(), name);
    assertEquals(params.getEntity(), httpEntity);
    assertEquals(params.getMethod(), HttpMethod.POST);
  }
}
