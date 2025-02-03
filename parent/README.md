# Common Parent

This module provides the parent POM for used by the (Java) Spring Boot based IPAFFS applications. It
- extends the parent POM provided by Spring Boot
- adds some `<dependencyManagement>`
- adds some "common" `<dependencies>`
- adds common build configuration

## Steps to integrate
Include the following in the pom.xml of your spring boot service

```
<parent>
  <groupId>uk.gov.defra.tracesx</groupId>
  <artifactId>TracesX-SpringBoot-Common-Parent</artifactId>
  <version>desired version</version>
</parent>
```