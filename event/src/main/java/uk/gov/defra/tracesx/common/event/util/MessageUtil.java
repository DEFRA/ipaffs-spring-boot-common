package uk.gov.defra.tracesx.common.event.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import uk.gov.defra.tracesx.common.event.exception.MessageValidationException;
import uk.gov.defra.tracesx.common.event.exception.ProtectiveMonitorJsonProcessingException;
import uk.gov.defra.tracesx.common.event.model.Message;

public class MessageUtil {

  private final ObjectMapper objectMapper;
  private final String eventHubEnvironment;

  public MessageUtil(ObjectMapper objectMapper, String eventHubEnvironment) {
    this.objectMapper = objectMapper;
    this.eventHubEnvironment = eventHubEnvironment;
  }

  public void setEventHubEnvironment(Message message) {
    message.setEnvironment(eventHubEnvironment);
  }

  public String writeMessage(Message message) {
    try {
      validate(message);
      this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE);
      return objectMapper.writeValueAsString(message);
    } catch (JsonProcessingException exception) {
      throw new ProtectiveMonitorJsonProcessingException(
          "Unable to convert ProtectiveMonitor Message to JSON string");
    }
  }

  public byte[] writeMessageToBytes(Message message) {
    try {
      validate(message);
      this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CASE);
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
