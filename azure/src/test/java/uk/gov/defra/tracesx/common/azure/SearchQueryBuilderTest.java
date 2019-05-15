package uk.gov.defra.tracesx.common.azure;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;

public class SearchQueryBuilderTest {

  private static final String FIELD_NAME = "test-field";

  private SearchQueryBuilder searchQueryBuilder;

  @Before
  public void setUp() {
    searchQueryBuilder = new SearchQueryBuilder();
  }

  @Test
  public void createWildcardSearchQueryWithNoSpecialCharactersReturnsCorrectQuery() {
    String value = "abcdefg";

    Query query = searchQueryBuilder.createWildcardSearchQuery(FIELD_NAME, value);

    assertEquals("test-field:abcdefg*", query.toString());
  }

  @Test
  public void createWildcardSearchQueryWithSpecialCharactersReturnsCorrectQuery() {
    String value = "abcde+-&&||!(){}[]^\"~*?:\\/fg";

    Query query = searchQueryBuilder.createWildcardSearchQuery(FIELD_NAME, value);

    assertEquals(
        "test-field:abcde\\\\+\\\\-\\\\&&\\\\||\\\\!\\\\(\\\\)\\\\{\\\\}\\\\[\\\\]\\\\^\\\\\"\\\\~\\\\*\\\\?\\\\:\\\\\\/fg",
        query.toString());
  }
}
