package uk.gov.defra.tracesx.common.azure;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.Arrays;
import java.util.List;

public class SearchQueryBuilder {

  private static final List<String> AZURE_SEARCH_SPECIAL_CHARACTERS = Arrays
      .asList("\\", "\"", "+", "-", "&", "|", "!", "(", ")", "{", "}", "[", "]", "^", "~", "*",
          "?", ":", "/", "`", "<", ">", "#", "%", ";", "@", "=");
  private static final String ESCAPE_PREFIX = "\\";

  public Query createWildcardSearchQuery(String field, String value) {
    Query searchQuery;
    String escapedValue = escapeSpecialCharacters(value);
    if (!escapedValue.equals(value)) {
      searchQuery = createSearchQuery(field, escapedValue);
    } else {
      searchQuery = createSearchQuery(field, String.format("%s*", value));
    }
    return searchQuery;
  }

  private Query createSearchQuery(String field, String value) {
    return new TermQuery(new Term(field, value));
  }

  private String escapeSpecialCharacters(String inputValue) {
    String escapedValue = inputValue;
    for (String specialCharacter : AZURE_SEARCH_SPECIAL_CHARACTERS) {
      escapedValue = StringUtils.replace(escapedValue,
          specialCharacter,
          ESCAPE_PREFIX.concat(specialCharacter));
    }
    return escapedValue;
  }

  public String createSearchValue(String value) {
    return escapeSpecialCharacters(value);
  }
}
