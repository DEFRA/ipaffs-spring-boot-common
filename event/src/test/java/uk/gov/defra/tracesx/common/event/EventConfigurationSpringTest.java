package uk.gov.defra.tracesx.common.event;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.azure.eventhubs.EventHubClient;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import uk.gov.defra.tracesx.common.event.monitor.AppInsightsBasedMonitor;
import uk.gov.defra.tracesx.common.event.monitor.EventHubBasedMonitor;
import uk.gov.defra.tracesx.common.event.monitor.LogBasedMonitor;
import uk.gov.defra.tracesx.common.event.util.MessageUtil;

public class EventConfigurationSpringTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(EventConfiguration.class);

  @Test
  public void createsDefaultBeans_WhenNoConfiguration() {
    this.contextRunner.run(context -> {
      assertThat(context).hasSingleBean(TelemetryClient.class);
      assertThat(context).hasSingleBean(MessageUtil.class);
      assertThat(context).hasSingleBean(LogBasedMonitor.class);
      assertThat(context).doesNotHaveBean(AppInsightsBasedMonitor.class);
      assertThat(context).doesNotHaveBean(EventHubBasedMonitor.class);
      assertThat(context).doesNotHaveBean(EventHubClient.class);
    });
  }

  @Test
  public void createsLogBasedMonitorBean_WhenConfigured() {
    this.contextRunner
        .withPropertyValues("monitoring.type=log")
        .run(context -> assertThat(context).hasSingleBean(LogBasedMonitor.class));
  }

  @Test
  public void createsAppInsightsBasedMonitorBean_WhenConfigured() {
    this.contextRunner
        .withPropertyValues("monitoring.type=app-insights")
        .run(context -> assertThat(context).hasSingleBean(AppInsightsBasedMonitor.class));
  }

  @Test
  public void createsEventHubBasedMonitorBean_WhenConfigured() {
    this.contextRunner
        .withPropertyValues("monitoring.type=event-hub")
        .run(context -> {
          assertThat(context).hasSingleBean(EventHubBasedMonitor.class);
          assertThat(context).hasSingleBean(EventHubClient.class);
        });
  }
}
