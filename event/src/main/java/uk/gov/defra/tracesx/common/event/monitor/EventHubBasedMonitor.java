package uk.gov.defra.tracesx.common.event.monitor;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.defra.tracesx.common.event.exception.EventHubSendException;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.model.Priority;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

public class EventHubBasedMonitor implements ProtectiveMonitor {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventHubBasedMonitor.class);
  private static final String CIRCUIT_BREAKER_NAME = "event-hub";

  private final AppInsightsBasedMonitor appInsightsBasedMonitor;
  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final EventHubClient eventHubClient;
  private final MessageUtil messageUtil;

  public EventHubBasedMonitor(
      AppInsightsBasedMonitor appInsightsBasedMonitor,
      EventHubClient eventHubClient,
      MessageUtil messageUtil,
      CircuitBreakerRegistry circuitBreakerRegistry) {
    this.appInsightsBasedMonitor = appInsightsBasedMonitor;
    this.eventHubClient = eventHubClient;
    this.messageUtil = messageUtil;
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @Override
  public void sendMessage(Message message) {
    messageUtil.setEventHubEnvironment(message);
    byte[] payloadBytes = messageUtil.writeMessageToBytes(message);
    EventData sendEvent = EventData.create(payloadBytes);

    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);

    Supplier<String> decoratedSupplier =
        CircuitBreaker.decorateSupplier(circuitBreaker, () -> sendSync(sendEvent, message));

    Try.ofSupplier(decoratedSupplier).recover(throwable -> logToAppInsights(message)).get();

    String state = circuitBreaker.getState().name();
    LOGGER.info("CircuitBreaker state is {}", state);
  }

  private String sendSync(EventData eventData, Message message) {
    try {
      eventHubClient.sendSync(eventData);
      appInsightsBasedMonitor.sendMessage(message);
    } catch (EventHubException | NullPointerException exception) {
      throw new EventHubSendException("Sending to Event Hub failed due to exception: " + exception);
    }
    return "Successfully logged to Event Hub";
  }

  private String logToAppInsights(Message message) {
    message.setPriority(Priority.CRITICAL);
    appInsightsBasedMonitor.sendMessage(message);
    return "Fallback logging to App Insights invoked";
  }
}