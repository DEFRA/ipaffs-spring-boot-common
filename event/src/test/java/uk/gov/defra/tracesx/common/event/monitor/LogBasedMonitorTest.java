package uk.gov.defra.tracesx.common.event.monitor;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

@ExtendWith(MockitoExtension.class)
class LogBasedMonitorTest {

  @Mock
  private MessageUtil messageUtil;

  @InjectMocks
  private LogBasedMonitor logBasedMonitor;

  @Test
  void sendMessage_CallsMessageUtil() {
    Message message = Message.getDefaultMessageBuilder().build();
    when(messageUtil.writeMessage(message)).thenReturn("Message");

    logBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).writeMessage(message);
  }

  @Test
  void sendMessage_CallsSetEventHubEnvironment() {
    Message message = Message.getDefaultMessageBuilder().build();
    logBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).setEventHubEnvironment(message);
  }
}
