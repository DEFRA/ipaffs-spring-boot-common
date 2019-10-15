package uk.gov.defra.tracesx.common.event.monitor;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

@RunWith(MockitoJUnitRunner.class)
public class LogBasedMonitorTest {

  @Mock
  private MessageUtil messageUtil;

  @InjectMocks
  private LogBasedMonitor logBasedMonitor;

  @Test
  public void sendMessage_CallsMessageUtil() {
    Message message = Message.getDefaultMessageBuilder().build();
    when(messageUtil.writeMessage(message)).thenReturn("Message");

    logBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).writeMessage(message);
  }

  @Test
  public void sendMessage_CallsSetDeploymentEnvironment() {
    Message message = Message.getDefaultMessageBuilder().build();
    logBasedMonitor.sendMessage(message);

    verify(messageUtil, times(1)).setDeploymentEnvironment(message);
  }
}
