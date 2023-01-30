package uk.gov.defra.tracesx.common.logging;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class StartupLoggerTest {

  private ListAppender<ILoggingEvent> logWatcher;

  @BeforeEach
  void before() {
    logWatcher = new ListAppender<>();
    logWatcher.start();
    ((Logger) LoggerFactory.getLogger(StartupLogger.class)).addAppender(logWatcher);
  }

  @Test
  void logStarted_LogsCorrectLogs() {
    StartupLogger startupLogger = new StartupLogger("name", "version");

    startupLogger.logStarted();

    assertThat(logWatcher.list).hasSize(1);
    assertThat(logWatcher.list.get(0).getFormattedMessage())
        .isEqualTo("Started IPAFFS service name version");
  }
}
