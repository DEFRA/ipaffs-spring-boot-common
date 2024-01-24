package uk.gov.defra.tracesx.common.health.checks.http;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

public class HttpHealthParams {

  private final String url;
  private final String name;
  private final HttpEntity<String> entity;
  private final HttpMethod method;

  public HttpHealthParams(String url, String name, HttpEntity<String> entity, HttpMethod method) {
    this.url = url;
    this.name = name;
    this.entity = entity;
    this.method = method;
  }

  public String getUrl() {
    return url;
  }

  public String getName() {
    return name;
  }

  public HttpMethod getMethod() {
    return method;
  }

  public HttpEntity<String> getEntity() {
    return entity;
  }
}
