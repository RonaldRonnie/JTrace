# JTrace - Live Architecture Enforcer

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![Gradle](https://img.shields.io/badge/Gradle-7.0+-green.svg)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**JTrace** is a powerful, framework-agnostic Live Architecture Enforcer that helps maintain clean architecture in Java projects. It analyzes your codebase in real-time and enforces architectural rules during build time and runtime.

## 🚀 Features

- **🔍 Real-time Analysis**: Analyze source code during build and runtime
- **📏 Multiple Rule Types**: Dependency rules, annotation requirements, layering, visibility, and cycle detection
- **🔄 Framework Agnostic**: Works with Maven, Gradle, and standalone Java applications
- **⚡ Fast**: Efficient pattern matching and AST analysis
- **📊 Rich Reporting**: Detailed violation reports with actionable suggestions
- **🔧 Flexible Configuration**: YAML-based rule configuration
- **🎯 Multiple Integration Points**: Maven plugin, Gradle plugin, Java agent, and CLI

## 🏗️ Architecture Rules Supported

### 1. **Dependency Rules**
- Forbidden dependencies between packages
- Required dependencies
- Dependency direction enforcement

### 2. **Annotation Rules**
- Required annotations on classes, methods, or fields
- Annotation validation and enforcement

### 3. **Layering Rules**
- Architectural layer enforcement
- Package-to-layer mapping
- Cross-layer dependency validation

### 4. **Visibility Rules**
- Class, method, and field visibility enforcement
- Access modifier validation

### 5. **Cycle Detection**
- Dependency cycle identification
- Circular reference prevention

## 🛠️ Installation & Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+ or Gradle 7.0+

### Quick Start

#### Option 1: Maven Plugin (Recommended)
```xml
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <configuration>
        <configFile>jtrace.yml</configFile>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>scan</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Option 2: Gradle Plugin
```groovy
plugins {
    id 'io.jtrace.gradle' version '0.1.0-SNAPSHOT'
}

jtrace {
    configFile = file('jtrace.yml')
}
```

#### Option 3: Java Agent
```bash
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar -jar your-application.jar
```

#### Option 4: Standalone CLI
```bash
# Download the CLI jar
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --config jtrace.yml --src src/main/java
```

## 📋 Configuration

Create a `jtrace.yml` file in your project root:

```yaml
version: 1
basePackage: "com.example"
failOn:
  severity: "error"

rules:
  # Forbidden dependency rule
  - id: no-controller-to-repository
    type: forbiddenDependency
    from: "com.example.controller..*"
    to: "com.example.repository..*"
    severity: error
    message: "Controllers must not access repositories directly."

  # Required annotation rule
  - id: service-annotation-required
    type: requireAnnotation
    target: "com.example.service..*"
    annotation: "org.springframework.stereotype.Service"
    severity: error
    message: "Service classes must be annotated with @Service"

  # Layering rule
  - id: layer-dependencies
    type: layering
    layers:
      - name: "presentation"
        packages: ["com.example.controller..*"]
      - name: "business"
        packages: ["com.example.service..*"]
      - name: "data"
        packages: ["com.example.repository..*"]
    allowedDependencies:
      - from: "presentation"
        to: ["business"]
      - from: "business"
        to: ["data"]
    severity: error
    message: "Invalid layer dependency detected"

  # Visibility rule
  - id: public-service-methods
    type: visibility
    target: "com.example.service..*"
    visibility: "public"
    scope: "method"
    severity: warning
    message: "Service methods should be public"
```

## 🔧 Usage Examples

### Maven Integration

#### Build-time Enforcement
```bash
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan
```

#### CI/CD Integration
```xml
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <id>architecture-check</id>
            <phase>verify</phase>
            <goals>
                <goal>scan</goal>
            </goals>
            <configuration>
                <failOnViolations>true</failOnViolations>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Gradle Integration

#### Build-time Enforcement
```bash
./gradlew jtraceScan
```

#### CI/CD Integration
```groovy
tasks.named('check') {
    dependsOn 'jtraceScan'
}

jtrace {
    configFile = file('jtrace.yml')
    failOnViolations = true
}
```

### Runtime Enforcement

#### Java Agent
```bash
# Development
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -jar your-app.jar

# Production
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -Djtrace.mode=enforce \
     -jar your-app.jar
```

#### Docker Integration
```dockerfile
FROM openjdk:17-jre-slim
COPY jtrace-agent-0.1.0-SNAPSHOT.jar /app/
COPY jtrace.yml /app/
COPY your-app.jar /app/

ENTRYPOINT ["java", "-javaagent:/app/jtrace-agent-0.1.0-SNAPSHOT.jar", \
            "-Djtrace.config=/app/jtrace.yml", \
            "-jar", "/app/your-app.jar"]
```

## 🏗️ Framework-Specific Configurations

### Spring Boot
```yaml
# jtrace.yml
rules:
  - id: spring-service-annotation
    type: requireAnnotation
    target: "com.example.service..*"
    annotation: "org.springframework.stereotype.Service"
    severity: error

  - id: spring-controller-annotation
    type: requireAnnotation
    target: "com.example.controller..*"
    annotation: "org.springframework.stereotype.Controller"
    severity: error

  - id: spring-repository-annotation
    type: requireAnnotation
    target: "com.example.repository..*"
    annotation: "org.springframework.stereotype.Repository"
    severity: error
```

### Jakarta EE / Java EE
```yaml
# jtrace.yml
rules:
  - id: ejb-annotation
    type: requireAnnotation
    target: "com.example.ejb..*"
    annotation: "javax.ejb.Stateless"
    severity: error

  - id: web-servlet
    type: requireAnnotation
    target: "com.example.web..*"
    annotation: "javax.servlet.annotation.WebServlet"
    severity: error
```

### Microservices
```yaml
# jtrace.yml
rules:
  - id: microservice-boundaries
    type: forbiddenDependency
    from: "com.example.user..*"
    to: "com.example.order..*"
    severity: error
    message: "Cross-service dependencies not allowed"

  - id: internal-package-structure
    type: layering
    layers:
      - name: "api"
        packages: ["com.example.user.api..*"]
      - name: "internal"
        packages: ["com.example.user.internal..*"]
    allowedDependencies:
      - from: "api"
        to: ["internal"]
    severity: error
```

## 📊 Violation Reports

JTrace provides detailed violation reports:

```
🚨 Architecture Violations Found: 3

❌ ERROR: Forbidden dependency detected
   Rule: no-controller-to-repository
   From: com.example.controller.UserController
   To: com.example.repository.UserRepository
   File: src/main/java/com/example/controller/UserController.java:25
   Message: Controllers must not access repositories directly.

⚠️  WARNING: Missing required annotation
   Rule: service-annotation-required
   Target: com.example.service.UserService
   Required: @Service
   File: src/main/java/com/example/service/UserService.java:15
   Message: Service classes must be annotated with @Service

❌ ERROR: Invalid layer dependency
   Rule: layer-dependencies
   From: com.example.controller.UserController
   To: com.example.repository.UserRepository
   File: src/main/java/com/example/controller/UserController.java:25
   Message: Invalid layer dependency detected

💡 Suggestions:
   - Move repository access to service layer
   - Add @Service annotation to UserService
   - Review architectural boundaries
```

## 🔍 Advanced Configuration

### Pattern Matching
```yaml
rules:
  # Recursive wildcard (any depth)
  - id: deep-nested
    type: forbiddenDependency
    from: "com.example.api..*"
    to: "com.example.internal..*"

  # Single level wildcard
  - id: single-level
    type: forbiddenDependency
    from: "com.example.api.*"
    to: "com.example.internal.*"

  # Regex patterns
  - id: regex-pattern
    type: forbiddenDependency
    from: "^com\\.example\\.(controller|service)\\.\\w+$"
    to: "com.example.repository..*"
```

### Conditional Rules
```yaml
rules:
  - id: conditional-annotation
    type: requireAnnotation
    target: "com.example.service..*"
    annotation: "org.springframework.stereotype.Service"
    condition:
      exclude: ["com.example.service.util..*"]
    severity: error
```

## 🚀 Performance & Best Practices

### Performance Tips
1. **Use specific patterns** instead of broad wildcards
2. **Limit rule complexity** for faster analysis
3. **Cache results** in CI/CD environments
4. **Run during build** rather than IDE for better performance

### Best Practices
1. **Start simple** with basic dependency rules
2. **Gradually add complexity** as your architecture matures
3. **Use meaningful rule IDs** for easier maintenance
4. **Set appropriate severity levels** (error, warning, info)
5. **Document your rules** in team documentation

## 🔧 Troubleshooting

### Common Issues

#### Build Failures
```bash
# Check JTrace configuration
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan -X

# Verify rule syntax
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate --config jtrace.yml
```

#### Performance Issues
```yaml
# jtrace.yml - Optimize patterns
rules:
  - id: optimized-rule
    type: forbiddenDependency
    from: "com.example.controller.UserController"  # Specific class
    to: "com.example.repository.UserRepository"    # Specific class
    # Instead of broad patterns like "com.example.controller..*"
```

#### False Positives
```yaml
# jtrace.yml - Add exclusions
rules:
  - id: flexible-rule
    type: forbiddenDependency
    from: "com.example.controller..*"
    to: "com.example.repository..*"
    exclude:
      - "com.example.controller.util.ValidationHelper"
      - "com.example.repository.spec.CustomSpecification"
```

## 📚 Additional Resources

- [📖 Complete Documentation](DEPLOYMENT_README.md)
- [🔧 Setup & Troubleshooting Guide](SETUP_GUIDE.md)
- [📋 Command Reference](COMMAND_REFERENCE.md)
- [🏗️ Architecture Examples](ARCHITECTURE_EXAMPLES.md)
- [🚀 Deployment Guide](DEPLOYMENT_SUMMARY.md)

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

- **Documentation**: Check the guides above
- **Examples**: See `jtrace-examples` module
- **Issues**: Report problems through the project repository
- **Community**: Join our discussions and share your use cases

---

**JTrace - Enforcing Clean Architecture, One Rule at a Time** 🏗️✨
