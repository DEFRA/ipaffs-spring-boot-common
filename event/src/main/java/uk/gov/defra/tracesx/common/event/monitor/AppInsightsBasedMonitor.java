package uk.gov.defra.tracesx.common.event.monitor;

import static uk.gov.defra.tracesx.common.event.util.Constants.MONITORING_EVENT_METRIC;

import com.microsoft.applicationinsights.TelemetryClient;
import java.util.Collections;
import java.util.Map;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.model.Priority;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

public class AppInsightsBasedMonitor implements ProtectiveMonitor {

  private final LogBasedMonitor logBasedMonitor;
  private final TelemetryClient telemetryClient;
  private final MessageUtil messageUtil;

  public AppInsightsBasedMonitor(
      LogBasedMonitor logBasedMonitor,
      TelemetryClient telemetryClient,
      MessageUtil messageUtil) {
    this.logBasedMonitor = logBasedMonitor;
    this.telemetryClient = telemetryClient;
    this.messageUtil = messageUtil;
  }

  @Override
  public void sendMessage(Message message) {
    try {
      messageUtil.setEventHubEnvironment(message);
      Map<String, String> properties =
          Collections.singletonMap("data", messageUtil.writeMessage(message));
      telemetryClient.trackEvent(MONITORING_EVENT_METRIC, properties, null);
    } catch (Exception exception) {
      message.setPriority(Priority.CRITICAL);
      logBasedMonitor.sendMessage(message);
    }
  }
}
