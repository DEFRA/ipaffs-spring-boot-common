package uk.gov.defra.tracesx.common.version;

import static com.github.stefanbirkner.systemlambda.SystemLambda.withEnvironmentVariable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FunctionAppVersionTest {

  @Mock
  HttpRequestMessage<Optional<String>> httpRequestMessage;
  @Mock
  HttpResponseMessage.Builder httpResponseMessageBuilder;

  @Test
  void runVersion_ReturnsApplicationInformation() throws Exception {
    when(httpRequestMessage.createResponseBuilder(any())).thenReturn(httpResponseMessageBuilder);
    when(httpResponseMessageBuilder.header(any(), any())).thenReturn(httpResponseMessageBuilder);
    when(httpResponseMessageBuilder.body(any())).thenReturn(httpResponseMessageBuilder);
    withEnvironmentVariable("APP_NAME", "test_app")
        .and("API_VERSION", "1.2.3")
        .execute(
            () -> {
              new FunctionAppVersion().runVersion(httpRequestMessage);
            });

    verify(httpRequestMessage).createResponseBuilder(HttpStatus.OK);
    verify(httpResponseMessageBuilder).header("Content-Type", "application/json");
    verify(httpResponseMessageBuilder)
        .body("{\"app\":{\"name\":\"test_app\",\"version\":\"1.2.3\"}}");
  }
}
