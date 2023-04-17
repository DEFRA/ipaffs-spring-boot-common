package uk.gov.defra.tracesx.common.event.exception;

import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import uk.gov.defra.tracesx.common.event.model.Message;

public class MessageValidationException extends RuntimeException {
  public MessageValidationException(Set<ConstraintViolation<Message>> violations) {
    super(violations.stream()
        .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
        .collect(Collectors.joining(", ")));
  }
}
