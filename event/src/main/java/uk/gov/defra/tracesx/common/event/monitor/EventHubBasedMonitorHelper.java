package uk.gov.defra.tracesx.common.event.monitor;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;

public class EventHubBasedMonitorHelper {

  private final EventHubProducerClient eventHubProducerClient;

  public EventHubBasedMonitorHelper(
      EventHubProducerClient eventHubProducerClient) {
    this.eventHubProducerClient = eventHubProducerClient;
  }

  public void sendBatchData(EventData eventData) {
    EventDataBatch batch = eventHubProducerClient.createBatch();
    batch.tryAdd(eventData);
    eventHubProducerClient.send(batch);
  }
}
