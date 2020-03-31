package uk.gov.defra.tracesx.common.event.monitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.Before;
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

  private static final String CLOSED = "CLOSED";
  private static final String DUMMY_MESSAGE = "message";

  @Mock
  private CircuitBreakerRegistry circuitBreakerRegistry;

  @Mock
  private MessageUtil messageUtil;

  @Mock
  private AppInsightsBasedMonitor appInsightsBasedMonitor;

  @Mock
  private EventHubClient eventHubClient;

  @InjectMocks
  private EventHubBasedMonitor eventHubBasedMonitor;

  private CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("testCircuitBreaker");
  private Message message = Message.getDefaultMessageBuilder().build();

  @Before
  public void before() throws JsonProcessingException {
    byte[] payloadBytes = new ObjectMapper().writeValueAsBytes(message);
    when(messageUtil.writeMessageToBytes(any())).thenReturn(payloadBytes);
    when(circuitBreakerRegistry.circuitBreaker(any())).thenReturn(circuitBreaker);
  }

  @Test
  public void sendMessage_CallsEventHubClientAndAppInsights() throws EventHubException {
    eventHubBasedMonitor.sendMessage(message);

    verify(messageUtil).writeMessageToBytes(message);
    verify(eventHubClient).sendSync(any(EventData.class));
    verify(appInsightsBasedMonitor).sendMessage(message);
    assertThat(circuitBreaker.getState().name()).isEqualTo(CLOSED);
  }

  @Test
  public void sendMessage_CallsAppInsightsBasedMonitorOnEventHubException()
      throws EventHubException {
    doThrow(new EventHubException(true, DUMMY_MESSAGE))
        .when(eventHubClient)
        .sendSync(any(EventData.class));

    eventHubBasedMonitor.sendMessage(message);

    verify(messageUtil).writeMessageToBytes(message);
    verify(eventHubClient).sendSync(any(EventData.class));

    message.setPriority(Priority.CRITICAL);
    verify(appInsightsBasedMonitor).sendMessage(message);
    assertThat(circuitBreaker.getState().name()).isEqualTo(CLOSED);
  }

  @Test
  public void sendMessage_RegistersAFailureInCircuitBreakerOnEventHubException()
      throws EventHubException {
    doThrow(new EventHubException(true, DUMMY_MESSAGE))
        .when(eventHubClient)
        .sendSync(any(EventData.class));

    final int initialNumberOfFailedCalls = circuitBreaker.getMetrics().getNumberOfFailedCalls();

    eventHubBasedMonitor.sendMessage(message);

    assertThat(initialNumberOfFailedCalls).isEqualTo(0);
    assertThat(circuitBreaker.getState().name()).isEqualTo(CLOSED);
    assertThat(circuitBreaker.getMetrics().getNumberOfFailedCalls()).isEqualTo(1);
  }

  @Test
  public void sendMessage_CallsAppInsightsBasedMonitorOnNullPointerException()
      throws EventHubException {
    doThrow(new NullPointerException(DUMMY_MESSAGE))
        .when(eventHubClient)
        .sendSync(any(EventData.class));

    eventHubBasedMonitor.sendMessage(message);

    verify(messageUtil).writeMessageToBytes(message);
    verify(eventHubClient).sendSync(any(EventData.class));

    message.setPriority(Priority.CRITICAL);
    verify(appInsightsBasedMonitor).sendMessage(message);
    assertThat(circuitBreaker.getState().name()).isEqualTo(CLOSED);
  }

  @Test
  public void sendMessage_CallsSetEventHubEnvironment() {
    eventHubBasedMonitor.sendMessage(message);

    verify(messageUtil).setEventHubEnvironment(message);
    assertThat(circuitBreaker.getState().name()).isEqualTo(CLOSED);
  }
}