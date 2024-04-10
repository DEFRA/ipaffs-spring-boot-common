package uk.gov.defra.tracesx.common.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.defra.tracesx.common.health.UrlHelper.buildAzureIndexSearchUrl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlHelperTest {

  @Test
  void willReturnHealthyWhenCalled() {

    String url = buildAzureIndexSearchUrl("service", "index", "version");

    assertEquals("https://service.search.windows.net/indexes/index/docs/search?api-version=version", url);
  }
}
