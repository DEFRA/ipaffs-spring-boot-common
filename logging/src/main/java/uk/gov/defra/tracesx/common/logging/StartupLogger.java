package uk.gov.defra.tracesx.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupLogger {

  private final String applicationName;
  private final String applicationVersion;

  public StartupLogger(
      @Value("${spring.application.name}") String applicationName,
      @Value("${info.app.version}") String applicationVersion) {
    this.applicationName = applicationName;
    this.applicationVersion = applicationVersion;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void logStarted() {
    log.info("Started IPAFFS service {} {}", applicationName, applicationVersion);
  }
}
