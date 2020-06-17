package uk.gov.defra.tracesx.common.health;

import static org.junit.Assert.assertEquals;
import static uk.gov.defra.tracesx.common.health.UrlHelper.buildAzureIndexSearchUrl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UrlHelperTest {

  @Test
  public void willReturnHealthyWhenCalled() {

    String url = buildAzureIndexSearchUrl("service", "index", "version");

    assertEquals("https://service.search.windows.net/indexes/index/docs/search?api-version=version", url);
  }
}
