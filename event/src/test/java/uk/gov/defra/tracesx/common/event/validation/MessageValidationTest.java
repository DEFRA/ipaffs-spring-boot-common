package uk.gov.defra.tracesx.common.event.validation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.defra.tracesx.common.event.exception.MessageValidationException;
import uk.gov.defra.tracesx.common.event.model.Details;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

class MessageValidationTest {

  private static final String MAX_LENGTH_STRING = StringUtils.repeat("a", 81);
  private static final String DEPLOYMENT_ENVIRONMENT = "env";

  private MessageUtil messageUtil;

  @BeforeEach
  public void setUp() {
    messageUtil = new MessageUtil(new ObjectMapper(), DEPLOYMENT_ENVIRONMENT);
  }

  @Test
  void writeMessage_Throws_OnUserLength() {
    Message message = new Message();
    message.setUser(MAX_LENGTH_STRING);

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("user length must be between 0 and 80");
  }

  @Test
  void writeMessage_Throws_OnSessionId() {
    Message message = new Message();
    message.setSessionId(MAX_LENGTH_STRING);

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("sessionId length must be between 0 and 80");
  }

  @Test
  void writeMessage_Throws_OnDateTime() {
    Message message = new Message();
    message.setDateTime(MAX_LENGTH_STRING);

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("dateTime length must be between 0 and 80");
  }

  @Test
  void writeMessage_Throws_OnIp() {
    Message message = new Message();
    message.setIp(MAX_LENGTH_STRING);

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("ip length must be between 0 and 80");
  }

  @Test
  void writeMessage_Throws_OnDetails_message() {
    Message message = Message.getDefaultMessageBuilder()
        .details(Details.builder()
            .message(MAX_LENGTH_STRING)
            .build())
        .build();

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("details.message length must be between 0 and 80");
  }

  @Test
  void writeMessage_Throws_OnDetails_additionalInfo() {
    Message message = Message.getDefaultMessageBuilder()
        .details(Details.builder()
            .additionalInfo(MAX_LENGTH_STRING)
            .build())
        .build();

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("details.additionalInfo length must be between 0 and 80");
  }

  @Test
  void writeMessage_Throws_WithMultipleViolations() {
    Message message = Message.getDefaultMessageBuilder()
        .user(MAX_LENGTH_STRING)
        .details(Details.builder()
            .additionalInfo(MAX_LENGTH_STRING)
            .build())
        .build();

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("user length must be between 0 and 80")
        .hasMessageContaining("details.additionalInfo length must be between 0 and 80");
  }

  @Test
  void writeMessage_Throws_NotNullViolations() {
    Message message = new Message();

    assertThatThrownBy(() -> messageUtil.writeMessage(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("user must not be null")
        .hasMessageContaining("sessionId must not be null")
        .hasMessageContaining("dateTime must not be null")
        .hasMessageContaining("application must not be null")
        .hasMessageContaining("component must not be null")
        .hasMessageContaining("ip must not be null")
        .hasMessageContaining("pmcCode must not be null")
        .hasMessageContaining("priority must not be null")
        .hasMessageContaining("details must not be null")
        .hasMessageContaining("environment must not be null")
        .hasMessageContaining("version must not be null");
  }

  @Test
  void writeBytes_Throws_OnUserLength() {
    Message message = new Message();
    message.setUser(MAX_LENGTH_STRING);

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("user length must be between 0 and 80");
  }

  @Test
  void writeBytes_Throws_OnSessionId() {
    Message message = new Message();
    message.setSessionId(MAX_LENGTH_STRING);

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("sessionId length must be between 0 and 80");
  }

  @Test
  void writeBytes_Throws_OnDateTime() {
    Message message = new Message();
    message.setDateTime(MAX_LENGTH_STRING);

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("dateTime length must be between 0 and 80");
  }

  @Test
  void writeBytes_Throws_OnIp() {
    Message message = new Message();
    message.setIp(MAX_LENGTH_STRING);

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("ip length must be between 0 and 80");
  }

  @Test
  void writeByes_Throws_OnDetails_message() {
    Message message = Message.getDefaultMessageBuilder()
        .details(Details.builder()
            .message(MAX_LENGTH_STRING)
            .build())
        .build();

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("details.message length must be between 0 and 80");
  }

  @Test
  void writeBytes_Throws_OnDetails_additionalInfo() {
    Message message = Message.getDefaultMessageBuilder()
        .details(Details.builder()
            .additionalInfo(MAX_LENGTH_STRING)
            .build())
        .build();

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("details.additionalInfo length must be between 0 and 80");
  }

  @Test
  void writeBytes_Throws_WithMultipleViolations() {
    Message message = Message.getDefaultMessageBuilder()
        .user(MAX_LENGTH_STRING)
        .details(Details.builder()
            .additionalInfo(MAX_LENGTH_STRING)
            .build())
        .build();

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("details.additionalInfo length must be between 0 and 80")
        .hasMessageContaining("user length must be between 0 and 80");
  }

  @Test
  void writeBytes_Throws_NotNullViolations() {
    Message message = new Message();

    assertThatThrownBy(() -> messageUtil.writeMessageToBytes(message))
        .isInstanceOf(MessageValidationException.class)
        .hasMessageContaining("user must not be null")
        .hasMessageContaining("sessionId must not be null")
        .hasMessageContaining("dateTime must not be null")
        .hasMessageContaining("application must not be null")
        .hasMessageContaining("component must not be null")
        .hasMessageContaining("ip must not be null")
        .hasMessageContaining("pmcCode must not be null")
        .hasMessageContaining("priority must not be null")
        .hasMessageContaining("details must not be null")
        .hasMessageContaining("environment must not be null")
        .hasMessageContaining("version must not be null");
  }
}
