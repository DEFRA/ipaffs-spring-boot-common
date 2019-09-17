package uk.gov.defra.tracesx.common.event.monitor;

import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.model.Priority;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

public class EventHubBasedMonitor implements ProtectiveMonitor {

  private final AppInsightsBasedMonitor appInsightsBasedMonitor;
  private final EventHubClient eventHubClient;
  private final MessageUtil messageUtil;

  public EventHubBasedMonitor(
      AppInsightsBasedMonitor appInsightsBasedMonitor,
      EventHubClient eventHubClient,
      MessageUtil messageUtil) {
    this.appInsightsBasedMonitor = appInsightsBasedMonitor;
    this.eventHubClient = eventHubClient;
    this.messageUtil = messageUtil;
  }

  @Override
  public void sendMessage(Message message) {
    byte[] payloadBytes = messageUtil.writeMessageToBytes(message);
    EventData sendEvent = EventData.create(payloadBytes);
    try {
      eventHubClient.sendSync(sendEvent);
      appInsightsBasedMonitor.sendMessage(message);
    } catch (EventHubException | NullPointerException exception) {
      message.setPriority(Priority.CRITICAL);
      appInsightsBasedMonitor.sendMessage(message);
    }
  }
}
