# Spring Boot Common

## Secret scanning
Secret scanning is setup using [truffleHog](https://github.com/trufflesecurity/truffleHog).
It is used as a pre-push hook and will scan any local commits being pushed

### Pre-push hook setup
1. Install [truffleHog](https://github.com/trufflesecurity/truffleHog)
    - `brew install trufflesecurity/trufflehog/trufflehog`
2. Set DEFRA_WORKSPACE env var (`export DEFRA_WORKSPACE=/path/to/workspace`)
3. Potentially there's an older version of Trufflehog located at: `/usr/local/bin/trufflehog`. If so, remove this.

### Git hook setup

1. Run `mvn install` to configure hooks from service folder.

To integrate this library you will be required to ensure the correct configuration is applied in the relevant application.yml file.

## Common Azure SQL library

For services that use JPA on Azure SQL. It brings `spring-boot-starter-data-jpa`, the SQL Server
driver and `azure-identity` (for `authentication=ActiveDirectoryDefault` in the JDBC URL), and sets
connection pool defaults suited to Azure SQL.

```
<dependency>
    <groupId>uk.gov.defra.tracesx</groupId>
    <artifactId>tracesx-springboot-common-azure-sql</artifactId>
    <version>desired version</version>
</dependency>
```

Let Spring Boot create the DataSource from `spring.datasource.url`. The defaults only apply to a
DataSource bound from `spring.datasource.hikari`, so don't build one with `DataSourceBuilder`. At
startup the module logs a warning for any `HikariDataSource` bean whose pool settings differ from
those properties, naming each setting that differs. A service that deliberately configures its own
pools (e.g. several DataSources) can turn that off with
`tracesx.azure-sql.datasource-check.enabled=false`.

| Property | Default | HikariCP's own default |
|---|---|---|
| `spring.datasource.hikari.maximum-pool-size` | 10 | 10 |
| `spring.datasource.hikari.keepalive-time` | 60000 (1 min) | 120000 |
| `spring.datasource.hikari.max-lifetime` | 600000 (10 min) | 1800000 |
| `spring.datasource.hikari.connection-timeout` | 10000 (10 s) | 30000 |
| `spring.datasource.hikari.validation-timeout` | 2000 (2 s) | 5000 |
| `spring.datasource.hikari.data-source-properties[socketTimeout]` | 90000 (90 s) | driver: none |
| `spring.datasource.hikari.data-source-properties[loginTimeout]` | 15 (s) | driver: 30 |
| `spring.datasource.hikari.data-source-properties[cancelQueryTimeout]` | 10 (s) | driver: none |
| `logging.level.com.zaxxer.hikari` | WARN | |

Anything in the service's `application.yml` or environment overrides them, e.g.
`SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20`.

The `data-source-properties` are SQL Server driver settings. The driver lets them override the same
settings in the JDBC URL, so the connection string only needs to say where the database is and how
to authenticate. `socketTimeout` must stay above the service's longest query timeout.

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

## Common Health library

This module should be included as a dependency for TracesX Spring boot microservice projects in order to implement their health check.

To integrate this library you should ensure you have access to the defra artifacts store so a valid settings.xml file will be required.

### Steps to integrate

Include the following library in your pom.xml

```
<dependency>
    <groupId>uk.gov.defra.tracesx</groupId>
    <artifactId>TracesX-SpringBoot-Common-Health</artifactId>
    <version>desired version</version>
</dependency>
```

The library detects any CheckHealth beans available in the application context and adds these to the health check. It also instantiates an Azure health check and JDBC health check according to the following conditions:
- If application property ```azure.index-name``` is configured then an Azure health check is instantiated.
- If application property ```spring.datasource.url``` is configured then a Jdbc health check is instantiated.
- If neither of the above properties are present a basic health check is provided so you need provide no extra configuration in your service.

If you have another means of determining health in your service then implement the CheckHealth interface and make a bean available in your configuration, it will be wired in automatically.

### How to configure

If the service already does  component scan on ```uk.gov.defra.tracesx``` (as per using the common security or common spring boot parent) then no configuration is required.
If your service is not doing that component scan simply add @ComponentScan to your applications configuration class like

```
@ComponentScan("uk.gov.defra.tracesx.common.health")
```

### Config

The library configures its own RestTemplate bean named ```defaultHealthCheckRestTemplate```, the timeouts can be configured separately via the following config

*management.health.custom.http.connectTimeout*   defaults to 1 second

*management.health.custom.http.readTimeout*   defaults to 1 second

### Turn off Springs defaults

Spring configures its own health checks, the following code is required in your service to disable the default behaviour and rely solely on your config

```
management:
  health:
    defaults:
      enabled: false
```

Also turn off the default behaviour for standard spring health checks as description is not required.

```
management:
  endpoint:
    health:
      show-details: "NEVER"
```
