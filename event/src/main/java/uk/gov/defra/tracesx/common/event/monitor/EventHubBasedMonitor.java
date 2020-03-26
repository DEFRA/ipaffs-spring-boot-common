package uk.gov.defra.tracesx.common.event.monitor;

import com.azure.messaging.eventhubs.EventData;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.model.Priority;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

import java.util.function.Supplier;

public class EventHubBasedMonitor implements ProtectiveMonitor {

  private static final Logger LOGGER = LoggerFactory.getLogger(EventHubBasedMonitor.class);
  private static final String CIRCUIT_BREAKER_NAME = "event-hub";

  private final AppInsightsBasedMonitor appInsightsBasedMonitor;
  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final EventHubBasedMonitorHelper eventHubBasedMonitorHelper;
  private final MessageUtil messageUtil;

  public EventHubBasedMonitor(
      AppInsightsBasedMonitor appInsightsBasedMonitor,
      EventHubBasedMonitorHelper eventHubBasedMonitorHelper,
      MessageUtil messageUtil,
      CircuitBreakerRegistry circuitBreakerRegistry) {
    this.appInsightsBasedMonitor = appInsightsBasedMonitor;
    this.eventHubBasedMonitorHelper = eventHubBasedMonitorHelper;
    this.messageUtil = messageUtil;
    this.circuitBreakerRegistry = circuitBreakerRegistry;
  }

  @Override
  public void sendMessage(Message message) {
    messageUtil.setEventHubEnvironment(message);
    byte[] payloadBytes = messageUtil.writeMessageToBytes(message);
    EventData sendEvent = new EventData(payloadBytes);
    CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME);
    Supplier<String> decoratedSupplier =
        CircuitBreaker.decorateSupplier(circuitBreaker, () -> sendSync(sendEvent, message));
    Try.ofSupplier(decoratedSupplier).recover(throwable -> logToAppInsights(message)).get();
    String state = circuitBreaker.getState().name();
    LOGGER.info(String.format("CircuitBreaker state is %s", state));
  }

  private String sendSync(EventData eventData, Message message) {
    try {
      eventHubBasedMonitorHelper.sendBatchData(eventData);
      appInsightsBasedMonitor.sendMessage(message);
    } catch (Exception exception) {
      LOGGER.error(exception.toString());
      throw new RuntimeException(exception);
    }
    return "Successfully logged to Event Hub";
  }

  private String logToAppInsights(Message message) {
    message.setPriority(Priority.CRITICAL);
    appInsightsBasedMonitor.sendMessage(message);
    return "Fallback logging to App Insights invoked";
  }
}
