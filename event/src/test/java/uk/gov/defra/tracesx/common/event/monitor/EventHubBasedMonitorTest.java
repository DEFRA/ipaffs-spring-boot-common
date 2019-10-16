package uk.gov.defra.tracesx.common.event.monitor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.model.Priority;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

@RunWith(MockitoJUnitRunner.class)
public class EventHubBasedMonitorTest {

  @Mock
  private MessageUtil messageUtil;

  @Mock
  private AppInsightsBasedMonitor appInsightsBasedMonitor;

  @Mock
  private EventHubClient eventHubClient;

  @InjectMocks
  private EventHubBasedMonitor eventHubBasedMonitor;

  @Test
  public void sendMessage_CallsEventHubClientAndAppInsights() throws JsonProcessingException, EventHubException {
    Message message = Message.getDefaultMessageBuilder().build();
    byte[] bytes = new ObjectMapper().writeValueAsBytes(message);
    when(messageUtil.writeMessageToBytes(message)).thenReturn(bytes);

    eventHubBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).writeMessageToBytes(message);
    verify(eventHubClient, times(1)).sendSync(any(EventData.class));
    verify(appInsightsBasedMonitor, times(1)).sendMessage(message);
  }

  @Test
  public void sendMessage_CallsAppInsightsBasedMonitorOnEventHubException() throws JsonProcessingException, EventHubException {
    Message message = Message.getDefaultMessageBuilder().build();
    byte[] bytes = new ObjectMapper().writeValueAsBytes(message);
    when(messageUtil.writeMessageToBytes(message)).thenReturn(bytes);
    doThrow(new EventHubException(true, "message")).when(eventHubClient).sendSync(any(EventData.class));

    eventHubBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).writeMessageToBytes(message);
    verify(eventHubClient, times(1)).sendSync(any(EventData.class));

    message.setPriority(Priority.CRITICAL);
    verify(appInsightsBasedMonitor).sendMessage(message);
  }

  @Test
  public void sendMessage_CallsAppInsightsBasedMonitorOnNullPointerException() throws JsonProcessingException, EventHubException {
    Message message = Message.getDefaultMessageBuilder().build();
    byte[] bytes = new ObjectMapper().writeValueAsBytes(message);
    when(messageUtil.writeMessageToBytes(message)).thenReturn(bytes);
    doThrow(new NullPointerException("message")).when(eventHubClient).sendSync(any(EventData.class));

    eventHubBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).writeMessageToBytes(message);
    verify(eventHubClient, times(1)).sendSync(any(EventData.class));

    message.setPriority(Priority.CRITICAL);
    verify(appInsightsBasedMonitor).sendMessage(message);
  }

  @Test
  public void sendMessage_CallsSetEventHubEnvironment() throws JsonProcessingException {
    Message message = Message.getDefaultMessageBuilder().build();
    byte[] bytes = new ObjectMapper().writeValueAsBytes(message);
    when(messageUtil.writeMessageToBytes(message)).thenReturn(bytes);

    eventHubBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).setEventHubEnvironment(message);
  }
}
