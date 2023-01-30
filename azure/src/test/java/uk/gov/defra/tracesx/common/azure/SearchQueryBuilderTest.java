package uk.gov.defra.tracesx.common.azure;

import static org.junit.Assert.assertEquals;

import org.apache.lucene.search.Query;
import org.junit.Before;
import org.junit.Test;

public class SearchQueryBuilderTest {

  private static final String FIELD_NAME = "test-field";
  private static final String FIELD_NAME_AS_STRING = "test-field:";
  private static final String VALUE_NO_SPECIAL_CHARS = "abcdefg àáâ ' , . £ $";
  private static final String VALUE_SPECIAL_CHARS = "abc + - && || ! ( ) { } [ ] ^ ~ * ? : / ` < > # % ; @ = abc123";
  private static final String ESCAPED_VALUE_SPECIAL_CHARS =
      "abc \\+ \\- \\&\\& \\|\\| \\! \\( \\) \\{ \\} \\[ \\] \\^ \\~ \\* \\? \\: \\/ \\` \\< \\> \\# \\% \\; \\@ \\= abc123";
  private static final String DOUBLE_VALUE_SPECIAL_CHARS = "++ -- &&&& |||| !! (( )) {{ }} [[ ]] ^^ ~~ ** ?? :: // `` << >> ## %% ;; @@ ==";
  private static final String ESCAPED_DOUBLE_VALUE_SPECIAL_CHARS =
      "\\+\\+ \\-\\- \\&\\&\\&\\& \\|\\|\\|\\| \\!\\! \\(\\( \\)\\) \\{\\{ \\}\\} \\[\\[ \\]\\] \\^\\^ \\~\\~ \\*\\* \\?\\? \\:\\: \\/\\/ \\`\\` \\<\\< \\>\\> \\#\\# \\%\\% \\;\\; \\@\\@ \\=\\=";
  private static final String QUOTE_BACKSLASH_VALUE = "\\ \"";
  private static final String ESCAPED_QUOTE_BACKSLASH_VALUE = "\\\\ \\\"";

  private SearchQueryBuilder searchQueryBuilder;

  @Before
  public void setUp() {
    searchQueryBuilder = new SearchQueryBuilder();
  }

  @Test
  public void createWildcardSearchQueryWithNoSpecialCharactersReturnsCorrectQuery() {
    Query query = searchQueryBuilder.createWildcardSearchQuery(FIELD_NAME, VALUE_NO_SPECIAL_CHARS);

    assertEquals(FIELD_NAME_AS_STRING + VALUE_NO_SPECIAL_CHARS + "*", query.toString());
  }

  @Test
  public void createWildcardSearchQueryWithSpecialCharactersReturnsCorrectQuery() {
    Query query = searchQueryBuilder.createWildcardSearchQuery(FIELD_NAME, VALUE_SPECIAL_CHARS);

    assertEquals(FIELD_NAME_AS_STRING + ESCAPED_VALUE_SPECIAL_CHARS, query.toString());
  }

  @Test
  public void createSearchValueWithNoSpecialCharactersReturnsCorrectValue() {
    String value = searchQueryBuilder.createSearchValue(VALUE_NO_SPECIAL_CHARS);

    assertEquals(VALUE_NO_SPECIAL_CHARS, value);
  }

  @Test
  public void createSearchValueWithSpecialCharactersReturnsCorrectValue() {
    String value = searchQueryBuilder.createSearchValue(VALUE_SPECIAL_CHARS);

    assertEquals(ESCAPED_VALUE_SPECIAL_CHARS, value);
  }

  @Test
  public void createSearchValueWithDoubleSpecialCharactersReturnsCorrectValue() {
    String value = searchQueryBuilder.createSearchValue(DOUBLE_VALUE_SPECIAL_CHARS);

    assertEquals(ESCAPED_DOUBLE_VALUE_SPECIAL_CHARS, value);
  }

  @Test
  public void createSearchValueWithQuoteAndBackslashCharactersReturnsCorrectValue() {
    String value = searchQueryBuilder.createSearchValue(QUOTE_BACKSLASH_VALUE);

    assertEquals(ESCAPED_QUOTE_BACKSLASH_VALUE, value);
  }
}
