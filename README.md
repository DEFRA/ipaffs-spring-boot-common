# Spring Boot Common 

## Health Checks

### This module should be included as a dependency for Spring projects in order to implement their health check.

This is a mvn module that allows a common method of integrating consistent health checks across the service estate.

## Usage

Providing you have the relevant settings.xml file in your .m2 directory and therefore have access to the Artifact store you 
should be able to just add ther following to your dependencies node in the service pom.

```
<dependency>
   <groupId>uk.gov.defra.tracesx</groupId>
   <artifactId>TracesX-SpringBoot-Common-Health</artifactId>
   <version>{desired version}</version>
</dependency>

```

Once this is in place, a *mvn clean install* should bring down the dependency 
