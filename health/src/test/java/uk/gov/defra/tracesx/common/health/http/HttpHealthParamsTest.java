package uk.gov.defra.tracesx.common.health.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthParams;

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

    assertEquals(url, params.getUrl());
    assertEquals(name, params.getName());
    assertEquals(httpEntity, params.getEntity());
    assertEquals(HttpMethod.POST, params.getMethod());
  }
}
