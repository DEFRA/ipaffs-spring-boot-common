package uk.gov.defra.tracesx.common.event.monitor;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.defra.tracesx.common.event.util.Constants.MONITORING_EVENT_METRIC;

import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.model.Priority;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

import java.util.Collections;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class AppInsightsBasedMonitorTest {

  @Mock
  private MessageUtil messageUtil;

  @Mock
  private LogBasedMonitor logBasedMonitor;

  @Mock
  private TelemetryClient telemetryClient;

  @InjectMocks
  private AppInsightsBasedMonitor appInsightsBasedMonitor;

  @Test
  void sendMessage_CallsTelemetryClient() {
    Map<String, String> properties = Collections.singletonMap("data", "Message");
    Message message = Message.getDefaultMessageBuilder().build();
    when(messageUtil.writeMessage(message)).thenReturn("Message");

    appInsightsBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).writeMessage(message);
    verify(telemetryClient, times(1))
        .trackEvent(MONITORING_EVENT_METRIC, properties, null);
  }

  @Test
  void sendMessage_CallsLogBasedMonitorOnException() {
    Map<String, String> properties = Collections.singletonMap("data", "Message");
    Message message = Message.getDefaultMessageBuilder().build();
    when(messageUtil.writeMessage(message)).thenReturn("Message");
    doThrow(new IllegalArgumentException()).when(telemetryClient).trackEvent(MONITORING_EVENT_METRIC, properties, null);

    appInsightsBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).writeMessage(message);
    verify(telemetryClient, times(1))
        .trackEvent(MONITORING_EVENT_METRIC, properties, null);

    message.setPriority(Priority.CRITICAL);
    verify(logBasedMonitor).sendMessage(message);
  }

  @Test
  void sendMessage_CallsSetEventHubEnvironment() {
    Message message = Message.getDefaultMessageBuilder().build();
    when(messageUtil.writeMessage(message)).thenReturn("Message");

    appInsightsBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).setEventHubEnvironment(message);
  }
}
