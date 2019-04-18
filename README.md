# Spring Boot Common Security

## Common Health library

This module should be included as a dependency for Spring projects in order to implement their health check.

To integrate this library you should ensure you have access to the defra artifacts store so a valid settings.xml file will be required.

# Steps to integrate

Include the following library in your pom.xml

```
<dependency>
    <groupId>uk.gov.defra.tracesx</groupId>
    <artifactId>TracesX-SpringBoot-Common-Health</artifactId>
    <version>desired version</version>
</dependency> 
```

By default the library will bootstrap an Azure health check and Jdbc health check, you will need to supply your own custom bean to bootstrap if you require anything else specific, this can be done by implementing the CheckHealth interface.

## How to configure

Example from permissions service:

```
@Bean
  public HealthChecker healthChecks(
          JdbcHealthCheck jdbcHealthCheck) {
    return new HealthChecker(Collections.singletonList(jdbcHealthCheck));
  }
```

You will also need to add @ComponentScan to your config class like

```
@ComponentScan("uk.gov.defra.tracesx.common.*")
```

### Config

The library configures its own RestTemplate, the timeouts can be configured separately via the following config

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