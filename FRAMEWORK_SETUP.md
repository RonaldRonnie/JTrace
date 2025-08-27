# JTrace - Framework Integration Guide

## 🏗️ Overview

This guide provides detailed instructions for integrating JTrace with various Java frameworks and architectural patterns. Each section includes complete configuration examples and best practices.

## 🌱 Spring Boot Integration

### Maven Integration

#### Basic Plugin Configuration
```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <configuration>
        <configFile>jtrace.yml</configFile>
        <failOnViolations>true</failOnViolations>
    </configuration>
    <executions>
        <execution>
            <id>architecture-check</id>
            <phase>verify</phase>
            <goals>
                <goal>scan</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Advanced Configuration
```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <configuration>
        <configFile>jtrace.yml</configFile>
        <failOnViolations>true</failOnViolations>
        <includeTests>false</includeTests>
        <verbose>true</verbose>
        <outputFormat>html</outputFormat>
        <outputFile>target/architecture-report.html</outputFile>
        <severity>warning</severity>
    </configuration>
    <executions>
        <execution>
            <id>pre-commit-check</id>
            <phase>validate</phase>
            <goals>
                <goal>scan</goal>
            </goals>
            <configuration>
                <failOnViolations>false</failOnViolations>
                <severity>info</severity>
            </configuration>
        </execution>
        <execution>
            <id>build-verification</id>
            <phase>verify</phase>
            <goals>
                <goal>scan</goal>
            </goals>
            <configuration>
                <failOnViolations>true</failOnViolations>
                <severity>error</severity>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Gradle Integration

#### Basic Plugin Configuration
```groovy
// build.gradle
plugins {
    id 'io.jtrace.gradle' version '0.1.0-SNAPSHOT'
}

jtrace {
    configFile = file('jtrace.yml')
    failOnViolations = true
}
```

#### Advanced Configuration
```groovy
// build.gradle
plugins {
    id 'io.jtrace.gradle' version '0.1.0-SNAPSHOT'
}

jtrace {
    configFile = file('jtrace.yml')
    failOnViolations = true
    includeTests = false
    verbose = true
    outputFormat = 'html'
    outputFile = file('build/reports/jtrace/architecture-report.html')
    severity = 'warning'
}

// Custom task configuration
tasks.named('jtraceScan') {
    doFirst {
        println "Starting Spring Boot architecture analysis..."
    }
    doLast {
        println "Spring Boot architecture analysis completed"
    }
}

// Integration with check task
tasks.named('check') {
    dependsOn 'jtraceScan'
}

// Integration with build task
tasks.named('build') {
    dependsOn 'jtraceScan'
}
```

### Spring Boot Configuration

#### Basic Architecture Rules
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.springapp"
failOn:
  severity: "error"

rules:
  # Layer dependency enforcement
  - id: spring-layering
    type: layering
    layers:
      - name: "presentation"
        packages: ["com.example.springapp.controller..*", "com.example.springapp.rest..*"]
      - name: "business"
        packages: ["com.example.springapp.service..*", "com.example.springapp.facade..*"]
      - name: "data"
        packages: ["com.example.springapp.repository..*", "com.example.springapp.dao..*"]
      - name: "domain"
        packages: ["com.example.springapp.domain..*", "com.example.springapp.model..*"]
      - name: "config"
        packages: ["com.example.springapp.config..*"]
    allowedDependencies:
      - from: "presentation"
        to: ["business", "domain"]
      - from: "business"
        to: ["data", "domain"]
      - from: "data"
        to: ["domain"]
      - from: "config"
        to: ["presentation", "business", "data", "domain"]
    severity: error
    message: "Invalid Spring Boot layer dependency detected"

  # Required annotations
  - id: spring-controller
    type: requireAnnotation
    target: "com.example.springapp.controller..*"
    annotation: "org.springframework.stereotype.Controller"
    severity: error
    message: "Controller classes must be annotated with @Controller"

  - id: spring-service
    type: requireAnnotation
    target: "com.example.springapp.service..*"
    annotation: "org.springframework.stereotype.Service"
    severity: error
    message: "Service classes must be annotated with @Service"

  - id: spring-repository
    type: requireAnnotation
    target: "com.example.springapp.repository..*"
    annotation: "org.springframework.stereotype.Repository"
    severity: error
    message: "Repository classes must be annotated with @Repository"

  - id: spring-component
    type: requireAnnotation
    target: "com.example.springapp.component..*"
    annotation: "org.springframework.stereotype.Component"
    severity: error
    message: "Component classes must be annotated with @Component"

  # Forbidden dependencies
  - id: no-controller-to-repository
    type: forbiddenDependency
    from: "com.example.springapp.controller..*"
    to: "com.example.springapp.repository..*"
    severity: error
    message: "Controllers must not access repositories directly"

  - id: no-controller-to-domain
    type: forbiddenDependency
    from: "com.example.springapp.controller..*"
    to: "com.example.springapp.domain..*"
    severity: error
    message: "Controllers must not access domain objects directly"

  # Visibility rules
  - id: public-controller-methods
    type: visibility
    target: "com.example.springapp.controller..*"
    visibility: "public"
    scope: "method"
    severity: warning
    message: "Controller methods should be public"

  - id: private-domain-fields
    type: visibility
    target: "com.example.springapp.domain..*"
    visibility: "private"
    scope: "field"
    severity: warning
    message: "Domain fields should be private"
```

#### Advanced Spring Boot Rules
```yaml
# jtrace.yml - Advanced Spring Boot rules
rules:
  # REST API validation
  - id: rest-controller
    type: requireAnnotation
    target: "com.example.springapp.rest..*"
    annotation: "org.springframework.web.bind.annotation.RestController"
    severity: error
    message: "REST controllers must be annotated with @RestController"

  # Transactional services
  - id: transactional-services
    type: requireAnnotation
    target: "com.example.springapp.service..*"
    annotation: "org.springframework.transaction.annotation.Transactional"
    severity: warning
    message: "Service methods should be transactional"

  # Validation annotations
  - id: validation-annotations
    type: requireAnnotation
    target: "com.example.springapp.dto..*"
    annotation: "javax.validation.Valid"
    severity: warning
    message: "DTOs should have validation annotations"

  # Configuration properties
  - id: configuration-properties
    type: requireAnnotation
    target: "com.example.springapp.config..*"
    annotation: "org.springframework.boot.context.properties.ConfigurationProperties"
    severity: warning
    message: "Configuration classes should use @ConfigurationProperties"

  # Event handling
  - id: event-listeners
    type: requireAnnotation
    target: "com.example.springapp.listener..*"
    annotation: "org.springframework.context.event.EventListener"
    severity: warning
    message: "Event listeners should be annotated with @EventListener"
```

## 🏛️ Jakarta EE / Java EE Integration

### Maven Integration
```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <configuration>
        <configFile>jtrace.yml</configFile>
        <failOnViolations>true</failOnViolations>
    </configuration>
    <executions>
        <execution>
            <phase>verify</phase>
            <goals>
                <goal>scan</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Jakarta EE Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.jakartaapp"
failOn:
  severity: "error"

rules:
  # EJB annotations
  - id: ejb-stateless
    type: requireAnnotation
    target: "com.example.jakartaapp.ejb..*"
    annotation: "javax.ejb.Stateless"
    severity: error
    message: "EJB classes must be annotated with @Stateless"

  - id: ejb-stateful
    type: requireAnnotation
    target: "com.example.jakartaapp.ejb.stateful..*"
    annotation: "javax.ejb.Stateful"
    severity: error
    message: "Stateful EJB classes must be annotated with @Stateful"

  - id: ejb-singleton
    type: requireAnnotation
    target: "com.example.jakartaapp.ejb.singleton..*"
    annotation: "javax.ejb.Singleton"
    severity: error
    message: "Singleton EJB classes must be annotated with @Singleton"

  # Web annotations
  - id: web-servlet
    type: requireAnnotation
    target: "com.example.jakartaapp.web..*"
    annotation: "javax.servlet.annotation.WebServlet"
    severity: error
    message: "Servlet classes must be annotated with @WebServlet"

  - id: web-filter
    type: requireAnnotation
    target: "com.example.jakartaapp.web.filter..*"
    annotation: "javax.servlet.annotation.WebFilter"
    severity: error
    message: "Filter classes must be annotated with @WebFilter"

  - id: web-listener
    type: requireAnnotation
    target: "com.example.jakartaapp.web.listener..*"
    annotation: "javax.servlet.annotation.WebListener"
    severity: error
    message: "Listener classes must be annotated with @WebListener"

  # JPA annotations
  - id: jpa-entity
    type: requireAnnotation
    target: "com.example.jakartaapp.entity..*"
    annotation: "javax.persistence.Entity"
    severity: error
    message: "Entity classes must be annotated with @Entity"

  - id: jpa-mapped-superclass
    type: requireAnnotation
    target: "com.example.jakartaapp.entity.base..*"
    annotation: "javax.persistence.MappedSuperclass"
    severity: error
    message: "Base entity classes must be annotated with @MappedSuperclass"

  # Layering
  - id: jakarta-layering
    type: layering
    layers:
      - name: "web"
        packages: ["com.example.jakartaapp.web..*"]
      - name: "business"
        packages: ["com.example.jakartaapp.ejb..*"]
      - name: "data"
        packages: ["com.example.jakartaapp.entity..*", "com.example.jakartaapp.dao..*"]
      - name: "util"
        packages: ["com.example.jakartaapp.util..*"]
    allowedDependencies:
      - from: "web"
        to: ["business", "util"]
      - from: "business"
        to: ["data", "util"]
      - from: "data"
        to: ["util"]
    severity: error
    message: "Invalid Jakarta EE layer dependency detected"

  # Forbidden dependencies
  - id: no-web-to-data
    type: forbiddenDependency
    from: "com.example.jakartaapp.web..*"
    to: ["com.example.jakartaapp.entity..*", "com.example.jakartaapp.dao..*"]
    severity: error
    message: "Web layer cannot directly access data layer"
```

## 🚀 Microservices Integration

### Microservices Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.microservice"
failOn:
  severity: "error"

rules:
  # Service boundary enforcement
  - id: service-isolation
    type: forbiddenDependency
    from: "com.example.microservice..*"
    to: ["com.example.orderservice..*", "com.example.paymentservice..*", "com.example.userservice..*"]
    severity: error
    message: "Cross-service dependencies not allowed"

  # Internal package structure
  - id: internal-structure
    type: layering
    layers:
      - name: "api"
        packages: ["com.example.microservice.api..*"]
      - name: "internal"
        packages: ["com.example.microservice.internal..*"]
      - name: "infrastructure"
        packages: ["com.example.microservice.infrastructure..*"]
      - name: "shared"
        packages: ["com.example.microservice.shared..*"]
    allowedDependencies:
      - from: "api"
        to: ["internal", "shared"]
      - from: "internal"
        to: ["infrastructure", "shared"]
      - from: "infrastructure"
        to: ["shared"]
    severity: error
    message: "Invalid internal dependency detected"

  # External API validation
  - id: api-contracts
    type: requireAnnotation
    target: "com.example.microservice.api..*"
    annotation: "org.springframework.web.bind.annotation.RestController"
    severity: error
    message: "API classes must be annotated with @RestController"

  # Infrastructure isolation
  - id: infrastructure-isolation
    type: forbiddenDependency
    from: "com.example.microservice.infrastructure..*"
    to: ["com.example.microservice.api..*", "com.example.microservice.internal..*"]
    severity: error
    message: "Infrastructure must not depend on business logic"

  # Health check endpoints
  - id: health-check
    type: requireAnnotation
    target: "com.example.microservice.health..*"
    annotation: "org.springframework.boot.actuate.endpoint.annotation.Endpoint"
    severity: warning
    message: "Health check endpoints should use @Endpoint"
```

## 🔒 Clean Architecture Integration

### Clean Architecture Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.cleanapp"
failOn:
  severity: "error"

rules:
  # Domain layer isolation
  - id: domain-isolation
    type: forbiddenDependency
    from: "com.example.cleanapp.domain..*"
    to: ["com.example.cleanapp.infrastructure..*", "com.example.cleanapp.application..*", "com.example.cleanapp.presentation..*"]
    severity: error
    message: "Domain layer must not depend on external layers"

  # Infrastructure dependencies
  - id: infrastructure-deps
    type: forbiddenDependency
    from: "com.example.cleanapp.infrastructure..*"
    to: ["com.example.cleanapp.application..*", "com.example.cleanapp.presentation..*"]
    severity: error
    message: "Infrastructure layer must not depend on application or presentation"

  # Application layer boundaries
  - id: application-boundaries
    type: forbiddenDependency
    from: "com.example.cleanapp.application..*"
    to: ["com.example.cleanapp.presentation..*"]
    severity: error
    message: "Application layer must not depend on presentation"

  # Layering enforcement
  - id: clean-layering
    type: layering
    layers:
      - name: "domain"
        packages: ["com.example.cleanapp.domain..*"]
      - name: "application"
        packages: ["com.example.cleanapp.application..*"]
      - name: "infrastructure"
        packages: ["com.example.cleanapp.infrastructure..*"]
      - name: "presentation"
        packages: ["com.example.cleanapp.presentation..*"]
    allowedDependencies:
      - from: "presentation"
        to: ["application"]
      - from: "application"
        to: ["domain"]
      - from: "infrastructure"
        to: ["domain"]
    severity: error
    message: "Invalid layer dependency detected"

  # Interface segregation
  - id: interface-segregation
    type: requireAnnotation
    target: "com.example.cleanapp.application..*"
    annotation: "org.springframework.stereotype.Service"
    severity: error
    message: "Application services must be annotated with @Service"

  # Repository pattern
  - id: repository-pattern
    type: requireAnnotation
    target: "com.example.cleanapp.infrastructure.persistence..*"
    annotation: "org.springframework.stereotype.Repository"
    severity: error
    message: "Repository implementations must be annotated with @Repository"
```

## 🔄 Event-Driven Architecture

### Event-Driven Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.eventapp"
failOn:
  severity: "error"

rules:
  # Event-driven layering
  - id: event-layering
    type: layering
    layers:
      - name: "commands"
        packages: ["com.example.eventapp.commands..*"]
      - name: "aggregates"
        packages: ["com.example.eventapp.aggregates..*"]
      - name: "events"
        packages: ["com.example.eventapp.events..*"]
      - name: "projections"
        packages: ["com.example.eventapp.projections..*"]
      - name: "infrastructure"
        packages: ["com.example.eventapp.infrastructure..*"]
    allowedDependencies:
      - from: "commands"
        to: ["aggregates", "events"]
      - from: "aggregates"
        to: ["events"]
      - from: "projections"
        to: ["events"]
      - from: "infrastructure"
        to: ["events", "aggregates", "projections"]
    severity: error
    message: "Invalid event-driven layer dependency detected"

  # Event annotations
  - id: event-annotations
    type: requireAnnotation
    target: "com.example.eventapp.events..*"
    annotation: "com.example.eventapp.events.DomainEvent"
    severity: error
    message: "Event classes must be annotated with @DomainEvent"

  # Command annotations
  - id: command-annotations
    type: requireAnnotation
    target: "com.example.eventapp.commands..*"
    annotation: "com.example.eventapp.commands.CommandHandler"
    severity: error
    message: "Command handlers must be annotated with @CommandHandler"

  # Aggregate annotations
  - id: aggregate-annotations
    type: requireAnnotation
    target: "com.example.eventapp.aggregates..*"
    annotation: "com.example.eventapp.aggregates.AggregateRoot"
    severity: error
    message: "Aggregate classes must be annotated with @AggregateRoot"

  # Forbidden event dependencies
  - id: no-event-dependencies
    type: forbiddenDependency
    from: "com.example.eventapp.events..*"
    to: ["com.example.eventapp.commands..*", "com.example.eventapp.aggregates..*"]
    severity: error
    message: "Events must not depend on commands or aggregates"
```

## 🧪 Testing Framework Integration

### Test Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.testapp"
failOn:
  severity: "error"

rules:
  # Test isolation
  - id: test-isolation
    type: forbiddenDependency
    from: "src/test/java..*"
    to: "src/main/java..*"
    severity: error
    message: "Test code cannot depend on production code"

  # Test annotations
  - id: test-annotations
    type: requireAnnotation
    target: "src/test/java..*"
    annotation: "org.junit.jupiter.api.Test"
    severity: warning
    message: "Test methods should be annotated with @Test"

  # Integration test isolation
  - id: integration-isolation
    type: forbiddenDependency
    from: "src/integration/java..*"
    to: "src/test/java..*"
    severity: error
    message: "Integration tests cannot depend on unit tests"

  # Test resource isolation
  - id: test-resource-isolation
    type: forbiddenDependency
    from: "src/test/resources..*"
    to: "src/main/resources..*"
    severity: error
    message: "Test resources cannot depend on production resources"
```

## 🔧 Custom Framework Integration

### Custom Framework Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.customapp"
failOn:
  severity: "error"

rules:
  # Custom framework annotations
  - id: custom-annotation
    type: requireAnnotation
    target: "com.example.customapp.component..*"
    annotation: "com.example.customapp.annotation.Component"
    severity: error
    message: "Custom components must be annotated with @Component"

  # Custom layering
  - id: custom-layering
    type: layering
    layers:
      - name: "custom-layer-1"
        packages: ["com.example.customapp.layer1..*"]
      - name: "custom-layer-2"
        packages: ["com.example.customapp.layer2..*"]
    allowedDependencies:
      - from: "custom-layer-1"
        to: ["custom-layer-2"]
    severity: error
    message: "Invalid custom layer dependency detected"
```

## 📚 Framework-Specific Templates

### Template Generation
```bash
# Generate Spring Boot configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template spring-boot

# Generate Jakarta EE configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template jakarta-ee

# Generate microservices configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template microservices

# Generate clean architecture configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template clean-architecture
```

## 🎯 Best Practices

### Framework Integration Guidelines

1. **Start with Basic Rules**: Begin with simple dependency rules
2. **Layer by Layer**: Add complexity gradually as your architecture matures
3. **Framework-Specific**: Use framework-appropriate annotations and patterns
4. **Regular Review**: Update rules as your framework usage evolves
5. **Performance**: Optimize patterns for your specific use case
6. **Documentation**: Document framework-specific rules and patterns

### Performance Considerations

1. **Specific Patterns**: Use exact package names when possible
2. **Limit Complexity**: Avoid overly complex rule combinations
3. **Cache Results**: Enable caching in CI/CD environments
4. **Build Integration**: Run during build, not IDE
5. **Incremental Analysis**: Analyze only changed files when possible

---

**For more detailed information, see the [Setup Guide](SETUP_GUIDE.md) and [Architecture Examples](ARCHITECTURE_EXAMPLES.md).**
