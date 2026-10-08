package uk.gov.defra.tracesx.common.event.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import uk.gov.defra.tracesx.common.event.exception.ProtectiveMonitorJsonProcessingException;
import uk.gov.defra.tracesx.common.event.model.Component;
import uk.gov.defra.tracesx.common.event.model.Details;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.model.PmcCode;
import uk.gov.defra.tracesx.common.event.model.TransactionCode;

@ExtendWith(MockitoExtension.class)
class MessageUtilTest {

  private static final String EVENT_HUB_ENVIRONMENT = "env";

  @Mock
  private ObjectMapper objectMapper;

  private MessageUtil messageUtil;

  private Message message;

  @BeforeEach
  public void setUp() {
    messageUtil = new MessageUtil(objectMapper, EVENT_HUB_ENVIRONMENT);

    message = Message.getDefaultMessageBuilder()
        .user("user")
        .sessionId("session")
        .component(Component.BORDERNOTIFICATION_MICROSERVICE)
        .environment("some other environment")
        .details(Details.builder()
            .transactionCode(TransactionCode.IPAFFS_404)
            .message("message")
            .build())
        .pmcCode(PmcCode.PMC_1400)
        .build();
  }

  @Test
  void writeMessage_WritesValueAsString() throws JacksonException {
    when(objectMapper.writeValueAsString(message)).thenReturn("string");
    String result = messageUtil.writeMessage(message);

    assertEquals("string", result);
  }

  @Test
  void writeMessage_ConvertsToCustomException() throws JacksonException {
    when(objectMapper.writeValueAsString(message)).thenThrow(new JacksonException("message"){});

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(ProtectiveMonitorJsonProcessingException.class)
        .hasMessage("Unable to convert ProtectiveMonitor Message to JSON string");
  }

  @Test
  void writeMessageToBytes_WritesValueAsByteArray() throws JacksonException {
    byte[] bytes = new ObjectMapper().writeValueAsBytes(message);
    when(objectMapper.writeValueAsBytes(message)).thenReturn(bytes);
    byte[] result = messageUtil.writeMessageToBytes(message);

    assertEquals(bytes, result);
  }

  @Test
  void writeMessageToBytes_ConvertsToCustomException() throws JacksonException {
    when(objectMapper.writeValueAsBytes(message)).thenThrow(new JacksonException("message"){});

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(ProtectiveMonitorJsonProcessingException.class)
        .hasMessage("Unable to convert ProtectiveMonitor Message to byte array");
  }

  @Test
  void setDeployEnvironment_SetsTheEventHubEnvironment() {
    Message message = new Message();
    messageUtil.setEventHubEnvironment(message);

    assertEquals(EVENT_HUB_ENVIRONMENT, message.getEnvironment());
  }
}
