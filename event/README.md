## Common Event library
This module should be included as a dependency for TracesX Spring Boot microservice projects, whereby protective monitoring events are called.

### Steps to integrate

Include the following library in your pom.xml

```
<dependency>
    <groupId>uk.gov.defra.tracesx</groupId>
    <artifactId>TracesX-SpringBoot-Common-Event</artifactId>
    <version>desired version</version>
</dependency>
```

The library detects which ProtectiveMonitor to declare via conditional properties in the configuration file.
- If no configuration is provided, then the ProtectiveMonitor will default to LogBasedProtectiveMonitor - Output is LOGGER.info()
- If application property ```monitoring.type``` is set to ```log``` then it will use LogBasedProtectiveMonitor - Output is LOGGER.info()
- If application property ```monitoring.type``` is set to ```app-insights``` then it will use AppInsightsBasedMonitor - Output is AppInsights Event
- If application property ```monitoring.type``` is set to ```event-hub``` then it will use EventHubBasedMonitor - Output is the Azure Event Hub

Specify the following config in services, default will null out the event hub and use the log as standard.

```
monitoring:
  type: ${MONITORING_TYPE:log}
  event-hub:
    namespace: ${EVENT_HUB_NAMESPACE:null}
    name: ${EVENT_HUB_NAME:null}
    key:
      name: ${EVENT_HUB_KEY_NAME:null}
      value: ${EVENT_HUB_KEY_VALUE:null}
```