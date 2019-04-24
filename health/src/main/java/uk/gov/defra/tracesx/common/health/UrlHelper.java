package uk.gov.defra.tracesx.common.health;

public class UrlHelper {

  private static final String DOCS_SEARCH = "docs/search";
  private static final String API_VERSION = "api-version";
  private static final String SCHEME = "https";
  private static final String SEARCH_WINDOWS_NET = "search.windows.net";
  private static final String INDEXES = "indexes";

  public static String buildAzureIndexSearchUrl(
      String serviceName, String indexName, String apiVersion) {
    String root = String.format("%s://%s.%s", SCHEME, serviceName, SEARCH_WINDOWS_NET);
    String path = String.format("%s/%s/%s", INDEXES, indexName, DOCS_SEARCH);
    String param = String.format("%s=%s", API_VERSION, apiVersion);
    return String.format("%s/%s?%s", root, path, param);
  }
}
