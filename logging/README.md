## Common Logging library

This module adds microservice name and version logging to service startup.

### Steps to integrate

Include the following library in your pom.xml

```
<dependency>
    <groupId>uk.gov.defra.tracesx</groupId>
    <artifactId>TracesX-SpringBoot-Common-Logging</artifactId>
    <version>desired version</version>
</dependency>
```

The startup logger uses the configuration file to build the log message, the following values must
be set:

- `spring.application.name`
- `info.app.version`
