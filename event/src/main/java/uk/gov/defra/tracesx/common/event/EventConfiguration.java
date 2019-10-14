package uk.gov.defra.tracesx.common.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.defra.tracesx.common.event.monitor.AppInsightsBasedMonitor;
import uk.gov.defra.tracesx.common.event.monitor.EventHubBasedMonitor;
import uk.gov.defra.tracesx.common.event.monitor.LogBasedMonitor;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

import java.io.IOException;
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

  private LogBasedMonitor logBasedMonitor;
  private AppInsightsBasedMonitor appInsightsBasedMonitor;

  @Bean
  public TelemetryClient createTelemetryClient() {
    TelemetryConfiguration configuration = TelemetryConfiguration.getActive();
    return new TelemetryClient(configuration);
  }

  @Bean
  @ConditionalOnProperty(name = "monitoring.type", havingValue = "event-hub")
  public EventHubClient createEventHubClient() {
    try {
      ConnectionStringBuilder connectionString = new ConnectionStringBuilder()
          .setNamespaceName(eventHubNamespace)
          .setEventHubName(eventHubName)
          .setSasKeyName(eventHubKeyName)
          .setSasKey(eventHubKeyValue);
      ScheduledExecutorService executorService =
          Executors.newScheduledThreadPool(EVENT_HUB_THREAD_POOL_SIZE);
      return EventHubClient
          .createFromConnectionStringSync(connectionString.toString(), executorService);
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
        createMessageUtil());
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
      appInsightsBasedMonitor = new AppInsightsBasedMonitor(
          getLogBasedMonitor(),
          createTelemetryClient(),
          createMessageUtil());
      return appInsightsBasedMonitor;
    } else {
      return appInsightsBasedMonitor;
    }
  }
}