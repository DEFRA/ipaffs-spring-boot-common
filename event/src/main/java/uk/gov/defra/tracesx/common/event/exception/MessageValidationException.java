package uk.gov.defra.tracesx.common.event.exception;

import jakarta.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;
import uk.gov.defra.tracesx.common.event.model.Message;

public class MessageValidationException extends RuntimeException {
  public MessageValidationException(Set<ConstraintViolation<Message>> violations) {
    super(violations.stream()
        .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
        .collect(Collectors.joining(", ")));
  }
}
