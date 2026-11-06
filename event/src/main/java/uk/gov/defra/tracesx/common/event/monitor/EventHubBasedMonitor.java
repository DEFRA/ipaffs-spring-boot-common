package uk.gov.defra.tracesx.common.event.monitor;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import java.util.concurrent.atomic.AtomicReference;
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
  private final AtomicReference<CircuitBreaker.State> lastLoggedState = new AtomicReference<>();

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

    logStateChange(circuitBreaker.getState());
  }

  // Logging the state on every send produced one line per event; only log when it changes
  private void logStateChange(CircuitBreaker.State state) {
    CircuitBreaker.State previous = lastLoggedState.getAndSet(state);
    if (state == previous) {
      return;
    }
    if (state == CircuitBreaker.State.CLOSED) {
      LOGGER.info("CircuitBreaker state is {}", state);
    } else {
      LOGGER.warn("CircuitBreaker state is {} (was {}); events fall back to App Insights",
          state, previous);
    }
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