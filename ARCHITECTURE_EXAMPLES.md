# JTrace - Architecture Examples

## 🏗️ Overview

This document provides comprehensive examples of how to use JTrace with different architectural patterns, frameworks, and use cases. Each example includes complete configuration files and explanations.

## 🌱 Spring Boot Architecture

### Layered Architecture Example

#### Project Structure
```
com.example.app/
├── controller/          # Presentation layer
├── service/            # Business logic layer
├── repository/         # Data access layer
├── domain/            # Domain model layer
└── config/            # Configuration layer
```

#### JTrace Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.app"
failOn:
  severity: "error"

rules:
  # Layer dependency enforcement
  - id: layered-architecture
    type: layering
    layers:
      - name: "presentation"
        packages: ["com.example.app.controller..*"]
      - name: "business"
        packages: ["com.example.app.service..*"]
      - name: "data"
        packages: ["com.example.app.repository..*"]
      - name: "domain"
        packages: ["com.example.app.domain..*"]
      - name: "config"
        packages: ["com.example.app.config..*"]
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
    message: "Invalid layer dependency detected"

  # Required annotations
  - id: spring-annotations
    type: requireAnnotation
    target: "com.example.app.controller..*"
    annotation: "org.springframework.stereotype.Controller"
    severity: error
    message: "Controller classes must be annotated with @Controller"

  - id: service-annotations
    type: requireAnnotation
    target: "com.example.app.service..*"
    annotation: "org.springframework.stereotype.Service"
    severity: error
    message: "Service classes must be annotated with @Service"

  - id: repository-annotations
    type: requireAnnotation
    target: "com.example.app.repository..*"
    annotation: "org.springframework.stereotype.Repository"
    severity: error
    message: "Repository classes must be annotated with @Repository"

  # Forbidden dependencies
  - id: no-controller-to-repository
    type: forbiddenDependency
    from: "com.example.app.controller..*"
    to: "com.example.app.repository..*"
    severity: error
    message: "Controllers must not access repositories directly"

  - id: no-controller-to-domain
    type: forbiddenDependency
    from: "com.example.app.controller..*"
    to: "com.example.app.domain..*"
    severity: error
    message: "Controllers must not access domain objects directly"

  # Visibility rules
  - id: public-controller-methods
    type: visibility
    target: "com.example.app.controller..*"
    visibility: "public"
    scope: "method"
    severity: warning
    message: "Controller methods should be public"

  - id: private-domain-fields
    type: visibility
    target: "com.example.app.domain..*"
    visibility: "private"
    scope: "field"
    severity: warning
    message: "Domain fields should be private"
```

### Microservices Example

#### Project Structure
```
com.example.userservice/
├── api/               # External API
├── internal/          # Internal implementation
├── infrastructure/    # Infrastructure concerns
└── shared/           # Shared utilities
```

#### JTrace Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.userservice"
failOn:
  severity: "error"

rules:
  # Service boundary enforcement
  - id: service-isolation
    type: forbiddenDependency
    from: "com.example.userservice..*"
    to: ["com.example.orderservice..*", "com.example.paymentservice..*"]
    severity: error
    message: "Cross-service dependencies not allowed"

  # Internal package structure
  - id: internal-structure
    type: layering
    layers:
      - name: "api"
        packages: ["com.example.userservice.api..*"]
      - name: "internal"
        packages: ["com.example.userservice.internal..*"]
      - name: "infrastructure"
        packages: ["com.example.userservice.infrastructure..*"]
      - name: "shared"
        packages: ["com.example.userservice.shared..*"]
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
    target: "com.example.userservice.api..*"
    annotation: "org.springframework.web.bind.annotation.RestController"
    severity: error
    message: "API classes must be annotated with @RestController"

  # Infrastructure isolation
  - id: infrastructure-isolation
    type: forbiddenDependency
    from: "com.example.userservice.infrastructure..*"
    to: ["com.example.userservice.api..*", "com.example.userservice.internal..*"]
    severity: error
    message: "Infrastructure must not depend on business logic"
```

## 🏛️ Clean Architecture Example

### Hexagonal Architecture

#### Project Structure
```
com.example.cleanapp/
├── domain/           # Domain entities and business rules
├── application/      # Application services and use cases
├── infrastructure/   # External concerns (DB, HTTP, etc.)
└── presentation/     # User interface and controllers
```

#### JTrace Configuration
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

## 🚀 Jakarta EE / Java EE Example

### Enterprise Application Architecture

#### Project Structure
```
com.example.enterprise/
├── web/              # Web layer (servlets, JSP)
├── ejb/              # Enterprise JavaBeans
├── entity/           # JPA entities
├── dao/              # Data Access Objects
└── util/             # Utility classes
```

#### JTrace Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.enterprise"
failOn:
  severity: "error"

rules:
  # EJB annotations
  - id: ejb-stateless
    type: requireAnnotation
    target: "com.example.enterprise.ejb..*"
    annotation: "javax.ejb.Stateless"
    severity: error
    message: "EJB classes must be annotated with @Stateless"

  - id: ejb-stateful
    type: requireAnnotation
    target: "com.example.enterprise.ejb.stateful..*"
    annotation: "javax.ejb.Stateful"
    severity: error
    message: "Stateful EJB classes must be annotated with @Stateful"

  # Web annotations
  - id: web-servlet
    type: requireAnnotation
    target: "com.example.enterprise.web..*"
    annotation: "javax.servlet.annotation.WebServlet"
    severity: error
    message: "Servlet classes must be annotated with @WebServlet"

  - id: web-filter
    type: requireAnnotation
    target: "com.example.enterprise.web.filter..*"
    annotation: "javax.servlet.annotation.WebFilter"
    severity: error
    message: "Filter classes must be annotated with @WebFilter"

  # JPA annotations
  - id: jpa-entity
    type: requireAnnotation
    target: "com.example.enterprise.entity..*"
    annotation: "javax.persistence.Entity"
    severity: error
    message: "Entity classes must be annotated with @Entity"

  # Layering
  - id: enterprise-layering
    type: layering
    layers:
      - name: "web"
        packages: ["com.example.enterprise.web..*"]
      - name: "business"
        packages: ["com.example.enterprise.ejb..*"]
      - name: "data"
        packages: ["com.example.enterprise.entity..*", "com.example.enterprise.dao..*"]
      - name: "util"
        packages: ["com.example.enterprise.util..*"]
    allowedDependencies:
      - from: "web"
        to: ["business", "util"]
      - from: "business"
        to: ["data", "util"]
      - from: "data"
        to: ["util"]
    severity: error
    message: "Invalid enterprise layer dependency detected"

  # Forbidden dependencies
  - id: no-web-to-data
    type: forbiddenDependency
    from: "com.example.enterprise.web..*"
    to: ["com.example.enterprise.entity..*", "com.example.enterprise.dao..*"]
    severity: error
    message: "Web layer cannot directly access data layer"
```

## 🔒 Security-Focused Architecture

### Secure Application Example

#### Project Structure
```
com.example.secureapp/
├── public/            # Public API endpoints
├── internal/          # Internal business logic
├── security/          # Security components
├── audit/             # Audit logging
└── config/            # Security configuration
```

#### JTrace Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.secureapp"
failOn:
  severity: "error"

rules:
  # Security layer isolation
  - id: security-isolation
    type: layering
    layers:
      - name: "public"
        packages: ["com.example.secureapp.public..*"]
      - name: "internal"
        packages: ["com.example.secureapp.internal..*"]
      - name: "security"
        packages: ["com.example.secureapp.security..*"]
      - name: "audit"
        packages: ["com.example.secureapp.audit..*"]
      - name: "config"
        packages: ["com.example.secureapp.config..*"]
    allowedDependencies:
      - from: "public"
        to: ["internal", "security"]
      - from: "internal"
        to: ["security", "audit"]
      - from: "security"
        to: ["audit"]
      - from: "config"
        to: ["public", "internal", "security", "audit"]
    severity: error
    message: "Invalid security layer dependency detected"

  # Security annotations
  - id: security-annotations
    type: requireAnnotation
    target: "com.example.secureapp.public..*"
    annotation: "org.springframework.security.access.prepost.PreAuthorize"
    severity: error
    message: "Public endpoints must have security annotations"

  # Audit requirements
  - id: audit-annotations
    type: requireAnnotation
    target: "com.example.secureapp.internal..*"
    annotation: "com.example.secureapp.audit.Audited"
    severity: warning
    message: "Internal operations should be audited"

  # Forbidden security bypasses
  - id: no-security-bypass
    type: forbiddenDependency
    from: "com.example.secureapp.internal..*"
    to: "com.example.secureapp.public..*"
    severity: error
    message: "Internal components cannot bypass security layer"
```

## 📊 Data-Centric Architecture

### Repository Pattern Example

#### Project Structure
```
com.example.dataapp/
├── domain/            # Domain entities
├── repository/        # Repository interfaces
├── repository/impl/   # Repository implementations
├── service/           # Business services
└── controller/        # REST controllers
```

#### JTrace Configuration
```yaml
# jtrace.yml
version: 1
basePackage: "com.example.dataapp"
failOn:
  severity: "error"

rules:
  # Repository pattern enforcement
  - id: repository-pattern
    type: layering
    layers:
      - name: "controller"
        packages: ["com.example.dataapp.controller..*"]
      - name: "service"
        packages: ["com.example.dataapp.service..*"]
      - name: "repository"
        packages: ["com.example.dataapp.repository..*"]
      - name: "domain"
        packages: ["com.example.dataapp.domain..*"]
    allowedDependencies:
      - from: "controller"
        to: ["service", "domain"]
      - from: "service"
        to: ["repository", "domain"]
      - from: "repository"
        to: ["domain"]
    severity: error
    message: "Invalid repository pattern dependency detected"

  # Repository interface requirements
  - id: repository-interfaces
    type: requireAnnotation
    target: "com.example.dataapp.repository..*"
    annotation: "org.springframework.stereotype.Repository"
    exclude: ["com.example.dataapp.repository.impl..*"]
    severity: error
    message: "Repository interfaces must be annotated with @Repository"

  # Repository implementation requirements
  - id: repository-impl
    type: requireAnnotation
    target: "com.example.dataapp.repository.impl..*"
    annotation: "org.springframework.stereotype.Repository"
    severity: error
    message: "Repository implementations must be annotated with @Repository"

  # Service layer requirements
  - id: service-layer
    type: requireAnnotation
    target: "com.example.dataapp.service..*"
    annotation: "org.springframework.stereotype.Service"
    severity: error
    message: "Service classes must be annotated with @Service"

  # Forbidden direct data access
  - id: no-direct-data-access
    type: forbiddenDependency
    from: ["com.example.dataapp.controller..*", "com.example.dataapp.service..*"]
    to: "com.example.dataapp.repository.impl..*"
    severity: error
    message: "Direct access to repository implementations not allowed"
```

## 🔄 Event-Driven Architecture

### Event Sourcing Example

#### Project Structure
```
com.example.eventapp/
├── events/            # Event definitions
├── commands/          # Command handlers
├── aggregates/        # Domain aggregates
├── projections/       # Read models
└── infrastructure/    # Event store, messaging
```

#### JTrace Configuration
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

## 🧪 Testing Architecture

### Test Structure Example

#### Project Structure
```
com.example.testapp/
├── src/main/java/     # Production code
├── src/test/java/     # Test code
├── src/test/resources/ # Test resources
└── src/integration/   # Integration tests
```

#### JTrace Configuration
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

## 🔧 Custom Rule Examples

### Advanced Pattern Matching

#### Complex Package Patterns
```yaml
# jtrace.yml
rules:
  # Regex patterns
  - id: regex-pattern
    type: forbiddenDependency
    from: "^com\\.example\\.(controller|service)\\.\\w+$"
    to: "com.example.repository..*"
    severity: error
    message: "Controllers and services cannot access repositories"

  # Multiple package exclusions
  - id: flexible-rule
    type: forbiddenDependency
    from: "com.example.controller..*"
    to: "com.example.repository..*"
    exclude:
      - "com.example.controller.util.ValidationHelper"
      - "com.example.controller.config.ConfigurationLoader"
      - "com.example.repository.spec.CustomSpecification"
    severity: error
    message: "Controllers cannot access repositories (with exceptions)"

  # Conditional rules
  - id: conditional-annotation
    type: requireAnnotation
    target: "com.example.service..*"
    annotation: "org.springframework.stereotype.Service"
    condition:
      exclude: ["com.example.service.util..*", "com.example.service.config..*"]
    severity: error
    message: "Service classes must be annotated with @Service (excluding utilities and config)"

  # Cross-cutting concerns
  - id: cross-cutting
    type: forbiddenDependency
    from: "com.example..*"
    to: "com.example.util.logging..*"
    severity: warning
    message: "Consider using AOP for logging instead of direct dependencies"
```

## 📚 Template Configurations

### Quick Start Templates

#### Spring Boot Template
```bash
# Generate Spring Boot configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template spring-boot
```

#### Jakarta EE Template
```bash
# Generate Jakarta EE configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template jakarta-ee
```

#### Microservices Template
```bash
# Generate microservices configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template microservices
```

#### Clean Architecture Template
```bash
# Generate clean architecture configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template clean-architecture
```

## 🎯 Best Practices

### Rule Design Guidelines

1. **Start Simple**: Begin with basic dependency rules
2. **Layer by Layer**: Add complexity gradually
3. **Meaningful IDs**: Use descriptive rule identifiers
4. **Appropriate Severity**: Set severity levels based on impact
5. **Clear Messages**: Provide actionable violation messages
6. **Regular Review**: Update rules as architecture evolves

### Performance Considerations

1. **Specific Patterns**: Use exact package names when possible
2. **Limit Complexity**: Avoid overly complex rule combinations
3. **Cache Results**: Enable caching in CI/CD environments
4. **Build Integration**: Run during build, not IDE
5. **Incremental Analysis**: Analyze only changed files when possible

---

**For more examples and advanced configurations, see the [Setup Guide](SETUP_GUIDE.md) and [Command Reference](COMMAND_REFERENCE.md).**
