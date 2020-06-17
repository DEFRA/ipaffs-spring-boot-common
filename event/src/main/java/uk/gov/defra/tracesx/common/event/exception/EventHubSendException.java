package uk.gov.defra.tracesx.common.event.exception;

public class EventHubSendException extends RuntimeException {
  public EventHubSendException(String message) {
    super(message);
  }
}
