package uk.gov.defra.tracesx.common.event.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.defra.tracesx.common.event.exception.MessageValidationException;
import uk.gov.defra.tracesx.common.event.exception.ProtectiveMonitorJsonProcessingException;
import uk.gov.defra.tracesx.common.event.model.Message;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class MessageUtil {

  private final ObjectMapper objectMapper;
  private final String eventHubEnvironment;

  public MessageUtil(ObjectMapper objectMapper, String eventHubEnvironment) {
    this.objectMapper = objectMapper;
    this.eventHubEnvironment = eventHubEnvironment;
  }

  public void setEventHubEnvironment(Message message) {
    message.setEventHubEnvironment(eventHubEnvironment);
  }

  public String writeMessage(Message message) {
    try {
      validate(message);
      return objectMapper.writeValueAsString(message);
    } catch (JsonProcessingException exception) {
      throw new ProtectiveMonitorJsonProcessingException(
          "Unable to convert ProtectiveMonitor Message to JSON string");
    }
  }

  public byte[] writeMessageToBytes(Message message) {
    try {
      validate(message);
      return objectMapper.writeValueAsBytes(message);
    } catch (JsonProcessingException exception) {
      throw new ProtectiveMonitorJsonProcessingException(
          "Unable to convert ProtectiveMonitor Message to byte array");
    }
  }

  private void validate(Message message) {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<Message>> violations = validator.validate(message);

    if (!violations.isEmpty()) {
      throw new MessageValidationException(violations);
    }
  }
}
