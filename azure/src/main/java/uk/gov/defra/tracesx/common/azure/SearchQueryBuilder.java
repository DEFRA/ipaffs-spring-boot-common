package uk.gov.defra.tracesx.common.azure;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SearchQueryBuilder {

  private static final List<String> AZURE_SEARCH_SPECIAL_CHARACTERS = Arrays
      .asList("+", "-", "&&", "||", "!", "(", ")", "{", "}", "[", "]", "^", "\"", "~", "*", "?",
          ":", "\\", "/");
  private static final String ESCAPE_PREFIX = "\\";

  public Query createWildcardSearchQuery(String field, String value) {
    String escapedValue = escapeMetaCharacters(value);
    String updatedValue = hasAzureSpecialCharacters(escapedValue)
        ? escapedValue
        : String.format("%s*", escapedValue);
    return createSearchQuery(field, updatedValue);
  }

  private Boolean hasAzureSpecialCharacters(String value) {
    return !AZURE_SEARCH_SPECIAL_CHARACTERS.stream()
        .filter(value::contains)
        .collect(Collectors.toList())
        .isEmpty();
  }

  private Query createSearchQuery(String field, String value) {
    return new TermQuery(new Term(field, value));
  }

  private String escapeMetaCharacters(String inputString) {
    String escapedString = inputString;
    for (String specialCharacter : AZURE_SEARCH_SPECIAL_CHARACTERS) {
      escapedString = StringUtils.replace(escapedString,
          specialCharacter,
          ESCAPE_PREFIX.concat(specialCharacter));
    }
    return escapedString;
  }
}
