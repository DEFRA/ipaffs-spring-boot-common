package uk.gov.defra.tracesx.common.event.exception;

import uk.gov.defra.tracesx.common.event.model.Message;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;

public class MessageValidationException extends RuntimeException {
  public MessageValidationException(Set<ConstraintViolation<Message>> violations) {
    super(violations.stream()
        .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
        .collect(Collectors.joining(", ")));
  }
}
