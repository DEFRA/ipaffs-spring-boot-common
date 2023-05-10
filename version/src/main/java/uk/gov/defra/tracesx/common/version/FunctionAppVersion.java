package uk.gov.defra.tracesx.common.version;

import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.Optional;

public class FunctionAppVersion {

  private static final String APP_NAME = "APP_NAME";
  private static final String API_VERSION = "API_VERSION";

  @FunctionName("FunctionAppVersion")
  public HttpResponseMessage runVersion(
          @HttpTrigger(name = "req",
                  methods = {HttpMethod.GET},
                  authLevel = AuthorizationLevel.ANONYMOUS,
                  route = "app/info")
                  HttpRequestMessage<Optional<String>> request) {

    return request
        .createResponseBuilder(HttpStatus.OK)
        .header("Content-Type", "application/json")
        .body(
            String.format(
                "{\"app\":{\"name\":\"%1$s\",\"version\":\"%2$s\"}}",
                System.getenv(APP_NAME), System.getenv(API_VERSION)))
        .build();
  }
}
