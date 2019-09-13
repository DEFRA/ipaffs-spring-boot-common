package uk.gov.defra.tracesx.common.event.monitor;

import uk.gov.defra.tracesx.common.event.model.Message;

@FunctionalInterface
public interface ProtectiveMonitor {

  void sendMessage(Message message);
}
