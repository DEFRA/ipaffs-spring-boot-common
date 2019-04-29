package uk.gov.defra.tracesx.common.health.tests;

import java.net.MalformedURLException;
import java.net.URL;

public class RequestHelper {

  private static final String SERVICE_BASE_URL = System.getProperty("service.base.url", "http://localhost:5460");

  private RequestHelper() {
  }

  public static URL getUrl(String s) {
    try {
      return new URL(SERVICE_BASE_URL + s);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }
}