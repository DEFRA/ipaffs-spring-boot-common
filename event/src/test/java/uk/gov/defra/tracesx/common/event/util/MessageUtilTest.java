package uk.gov.defra.tracesx.common.event.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.defra.tracesx.common.event.exception.ProtectiveMonitorJsonProcessingException;
import uk.gov.defra.tracesx.common.event.model.Component;
import uk.gov.defra.tracesx.common.event.model.Details;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.model.PmcCode;
import uk.gov.defra.tracesx.common.event.model.TransactionCode;

@RunWith(MockitoJUnitRunner.class)
public class MessageUtilTest {

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private MessageUtil messageUtil;

  private Message message;

  @Before
  public void setUp() {
    message = Message.getDefaultMessageBuilder()
        .user("user")
        .sessionId("session")
        .component(Component.BORDERNOTIFICATION_MICROSERVICE)
        .environment("environment")
        .details(Details.builder()
            .transactionCode(TransactionCode.IPAFFS_404)
            .message("message")
            .build())
        .pmcCode(PmcCode.PMC_1400)
        .build();
  }

  @Test
  public void writeMessage_WritesValueAsString() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(message)).thenReturn("string");
    String result = messageUtil.writeMessage(message);

    assertEquals("string", result);
  }

  @Test
  public void writeMessage_ConvertsToCustomException() throws JsonProcessingException {
    when(objectMapper.writeValueAsString(message)).thenThrow(new JsonProcessingException("message"){});

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(ProtectiveMonitorJsonProcessingException.class)
        .hasMessage("Unable to convert ProtectiveMonitor Message to JSON string");
  }

  @Test
  public void writeMessageToBytes_WritesValueAsByteArray() throws JsonProcessingException {
    byte[] bytes = new ObjectMapper().writeValueAsBytes(message);
    when(objectMapper.writeValueAsBytes(message)).thenReturn(bytes);
    byte[] result = messageUtil.writeMessageToBytes(message);

    assertEquals(bytes, result);
  }

  @Test
  public void writeMessageToBytes_ConvertsToCustomException() throws JsonProcessingException {
    when(objectMapper.writeValueAsBytes(message)).thenThrow(new JsonProcessingException("message"){});

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(ProtectiveMonitorJsonProcessingException.class)
        .hasMessage("Unable to convert ProtectiveMonitor Message to byte array");
  }
}
