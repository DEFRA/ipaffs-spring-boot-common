# Spring Boot Common

## Common Health library

This module should be included as a dependency for TracesX Spring boot microservice projects in order to implement their health check.

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

The library detects any CheckHealth beans available in the application context and adds these to the health check. It also instantiates an Azure health check and JDBC health check according to the following conditions:
- If application property ```azure.index-name``` is configured then an Azure health check is instantiated.
- If application property ```spring.datasource.url``` is configured then a Jdbc health check is instantiated.
- If neither of the above properties are present a basic health check is provided so you need provide no extra configuration in your service.

If you have another means of determining health in your service then implement the CheckHealth interface and make a bean available in your configuration, it will be wired in automatically.

## How to configure

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