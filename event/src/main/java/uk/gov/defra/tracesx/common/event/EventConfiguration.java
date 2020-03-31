package uk.gov.defra.tracesx.common.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.defra.tracesx.common.event.monitor.AppInsightsBasedMonitor;
import uk.gov.defra.tracesx.common.event.monitor.EventHubBasedMonitor;
import uk.gov.defra.tracesx.common.event.monitor.LogBasedMonitor;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class EventConfiguration {

  private static final int EVENT_HUB_THREAD_POOL_SIZE = 4;

  @Value("${monitoring.event-hub.namespace:#{null}}")
  private String eventHubNamespace;

  @Value("${monitoring.event-hub.name:#{null}}")
  private String eventHubName;

  @Value("${monitoring.event-hub.key.name:#{null}}")
  private String eventHubKeyName;

  @Value("${monitoring.event-hub.key.value:#{null}}")
  private String eventHubKeyValue;

  @Value("${monitoring.event-hub.environment}")
  private String eventHubEnvironment;

  @Value("${resilience4j.circuitbreaker.instances.event-hub.automaticTransitionEnabled:#{false}}")
  private boolean autoTransitionFromOpenToHalfOpenEnabled;

  @Value("${resilience4j.circuitbreaker.instances.event-hub.failureRateThreshold:#{50}}")
  private float failureRateThreshold;

  @Value("${resilience4j.circuitbreaker.instances.event-hub.minimumNumberOfCalls:#{10}}")
  private int minimumNumberOfCalls;

  @Value("${resilience4j.circuitbreaker.instances.event-hub.permittedCallsWhenHalfOpen:#{10}}")
  private int permittedNumberOfCallsInHalfOpenState;

  @Value("${resilience4j.circuitbreaker.instances.event-hub.slidingWindowSize:#{100}}")
  private int slidingWindowSize;

  @Value("${resilience4j.circuitbreaker.instances.event-hub.slowCallDurationThreshold:#{60000}}")
  private long slowCallDurationThreshold;

  @Value("${resilience4j.circuitbreaker.instances.event-hub.slowCallRateThreshold:#{100}}")
  private float slowCallRateThreshold;

  @Value("${resilience4j.circuitbreaker.instances.event-hub.waitDurationInOpenState:#{60000}}")
  private long waitDurationInOpenState;

  private LogBasedMonitor logBasedMonitor;
  private AppInsightsBasedMonitor appInsightsBasedMonitor;

  @Bean
  @ConditionalOnProperty(name = "monitoring.type", havingValue = "event-hub")
  public CircuitBreakerRegistry createCircuitBreakerRegistry() {
    CircuitBreakerConfig circuitBreakerConfig =
        CircuitBreakerConfig.custom()
            .automaticTransitionFromOpenToHalfOpenEnabled(autoTransitionFromOpenToHalfOpenEnabled)
            .failureRateThreshold(failureRateThreshold)
            .minimumNumberOfCalls(minimumNumberOfCalls)
            .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState)
            .slidingWindowSize(slidingWindowSize)
            .slowCallDurationThreshold(Duration.ofMillis(slowCallDurationThreshold))
            .slowCallRateThreshold(slowCallRateThreshold)
            .waitDurationInOpenState(Duration.ofMillis(waitDurationInOpenState))
            .build();

    CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
    circuitBreakerRegistry.circuitBreaker("event-hub", circuitBreakerConfig);

    return circuitBreakerRegistry;
  }

  @Bean
  public TelemetryClient createTelemetryClient() {
    TelemetryConfiguration configuration = TelemetryConfiguration.getActive();
    return new TelemetryClient(configuration);
  }

  @Bean
  @ConditionalOnProperty(name = "monitoring.type", havingValue = "event-hub")
  public EventHubClient createEventHubClient() {
    try {
      ConnectionStringBuilder connectionString =
          new ConnectionStringBuilder()
              .setNamespaceName(eventHubNamespace)
              .setEventHubName(eventHubName)
              .setSasKeyName(eventHubKeyName)
              .setSasKey(eventHubKeyValue);
      ScheduledExecutorService executorService =
          Executors.newScheduledThreadPool(EVENT_HUB_THREAD_POOL_SIZE);
      return EventHubClient.createFromConnectionStringSync(
          connectionString.toString(), executorService);
    } catch (EventHubException | IOException exception) {
      return null;
    }
  }

  @Bean
  public MessageUtil createMessageUtil() {
    return new MessageUtil(new ObjectMapper(), eventHubEnvironment);
  }

  @Bean
  @ConditionalOnProperty(name = "monitoring.type", havingValue = "log", matchIfMissing = true)
  public LogBasedMonitor createLogProtectiveMonitor() {
    return getLogBasedMonitor();
  }

  @Bean
  @ConditionalOnProperty(name = "monitoring.type", havingValue = "app-insights")
  public AppInsightsBasedMonitor createAppInsightsProtectiveMonitor() {
    return getAppInsightsBasedMonitor();
  }

  @Bean
  @ConditionalOnProperty(name = "monitoring.type", havingValue = "event-hub")
  public EventHubBasedMonitor createEventHubProtectiveMonitor() {
    return new EventHubBasedMonitor(
        getAppInsightsBasedMonitor(),
        createEventHubClient(),
        createMessageUtil(),
        createCircuitBreakerRegistry());
  }

  private LogBasedMonitor getLogBasedMonitor() {
    if (logBasedMonitor == null) {
      logBasedMonitor = new LogBasedMonitor(createMessageUtil());
      return logBasedMonitor;
    } else {
      return logBasedMonitor;
    }
  }

  private AppInsightsBasedMonitor getAppInsightsBasedMonitor() {
    if (appInsightsBasedMonitor == null) {
      appInsightsBasedMonitor =
          new AppInsightsBasedMonitor(
              getLogBasedMonitor(), createTelemetryClient(), createMessageUtil());
      return appInsightsBasedMonitor;
    } else {
      return appInsightsBasedMonitor;
    }
  }
}