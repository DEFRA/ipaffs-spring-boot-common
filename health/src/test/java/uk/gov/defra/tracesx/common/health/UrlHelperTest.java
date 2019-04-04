package uk.gov.defra.tracesx.common.health;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import uk.gov.defra.tracesx.common.health.checks.JdbcHealthCheck;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.defra.tracesx.common.health.UrlHelper.buildAzureIndexSearchUrl;

@RunWith(MockitoJUnitRunner.class)
public class UrlHelperTest {

  @Test
  public void willReturnHealthyWhenCalled() {

    String url = buildAzureIndexSearchUrl("service", "env", "index", "version");

    assertEquals(
        url,
        "https://service-env.search.windows.net/indexes/index/docs/search?api-version=version");
  }
}
