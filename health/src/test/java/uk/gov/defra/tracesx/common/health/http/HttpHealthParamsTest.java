package uk.gov.defra.tracesx.common.health.http;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import uk.gov.defra.tracesx.common.health.checks.http.HttpHealthParams;

@ExtendWith(MockitoExtension.class)
class HttpHealthParamsTest {

  @Test
  void willCorrectlyInitialize() {

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
