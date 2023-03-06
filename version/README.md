## Common Version library

This module defines an Azure Function http triggered endpoint to return the application version and name.

### Steps to integrate

Include the following library in your pom.xml

```
<dependency>
    <groupId>uk.gov.defra.tracesx</groupId>
    <artifactId>TracesX-SpringBoot-Common-Version</artifactId>
    <version>desired version</version>
</dependency>
```

The function uses the following environment variables:

- `APP_NAME`
- `API_VERSION`
