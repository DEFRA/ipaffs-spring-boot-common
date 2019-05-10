package uk.gov.defra.tracesx.common.health.tests;

import java.net.MalformedURLException;
import java.net.URL;

final class RequestHelper {

  private static final String SERVICE_BASE_URL =
      System.getProperty("service.base.url", "http://localhost:5460");

  private RequestHelper() {
  }

  static URL getUrl(final String str) {
    try {
      return new URL(SERVICE_BASE_URL + str);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }
}
