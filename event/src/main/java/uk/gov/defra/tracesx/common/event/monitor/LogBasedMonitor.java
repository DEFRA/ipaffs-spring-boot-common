package uk.gov.defra.tracesx.common.event.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.defra.tracesx.common.event.model.Message;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

public class LogBasedMonitor implements ProtectiveMonitor {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogBasedMonitor.class);

  private MessageUtil messageUtil;

  public LogBasedMonitor(MessageUtil messageUtil) {
    this.messageUtil = messageUtil;
  }

  @Override
  public void sendMessage(Message message) {
    LOGGER.info(messageUtil.writeMessage(message));
  }
}
