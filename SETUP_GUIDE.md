# JTrace - Complete Setup & Troubleshooting Guide

## 🚀 Quick Start

### Prerequisites Check
```bash
# Check Java version (requires 17+)
java -version

# Check Maven version (requires 3.6+)
mvn -version

# Check Gradle version (requires 7.0+)
./gradlew --version
```

### 1. Download & Install
```bash
# Option A: Download pre-built JARs
wget https://github.com/jtrace/jtrace/releases/latest/download/jtrace-core.jar
wget https://github.com/jtrace/jtrace/releases/latest/download/jtrace-maven-plugin.jar
wget https://github.com/jtrace/jtrace/releases/latest/download/jtrace-agent.jar

# Option B: Build from source
git clone https://github.com/jtrace/jtrace.git
cd jtrace
mvn clean install
```

### 2. Initialize Configuration
```bash
# Create basic configuration
cat > jtrace.yml << EOF
version: 1
basePackage: "com.example"
failOn:
  severity: "error"

rules:
  - id: basic-rule
    type: forbiddenDependency
    from: "com.example.controller..*"
    to: "com.example.repository..*"
    severity: error
    message: "Controllers must not access repositories directly."
EOF
```

## 🏗️ Framework-Specific Setup

### Spring Boot

#### Maven Integration
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

#### Gradle Integration
```groovy
// build.gradle
plugins {
    id 'io.jtrace.gradle' version '0.1.0-SNAPSHOT'
}

jtrace {
    configFile = file('jtrace.yml')
    failOnViolations = true
}

tasks.named('check') {
    dependsOn 'jtraceScan'
}
```

#### Spring-Specific Rules
```yaml
# jtrace.yml
rules:
  # Service layer annotations
  - id: spring-service
    type: requireAnnotation
    target: "com.example.service..*"
    annotation: "org.springframework.stereotype.Service"
    severity: error

  # Controller annotations
  - id: spring-controller
    type: requireAnnotation
    target: "com.example.controller..*"
    annotation: "org.springframework.stereotype.Controller"
    severity: error

  # Repository annotations
  - id: spring-repository
    type: requireAnnotation
    target: "com.example.repository..*"
    annotation: "org.springframework.stereotype.Repository"
    severity: error

  # Component annotations
  - id: spring-component
    type: requireAnnotation
    target: "com.example.component..*"
    annotation: "org.springframework.stereotype.Component"
    severity: error

  # Layering rules
  - id: spring-layering
    type: layering
    layers:
      - name: "presentation"
        packages: ["com.example.controller..*", "com.example.rest..*"]
      - name: "business"
        packages: ["com.example.service..*", "com.example.facade..*"]
      - name: "data"
        packages: ["com.example.repository..*", "com.example.dao..*"]
    allowedDependencies:
      - from: "presentation"
        to: ["business"]
      - from: "business"
        to: ["data"]
    severity: error
```

### Jakarta EE / Java EE

#### Maven Integration
```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <configuration>
        <configFile>jtrace.yml</configFile>
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

#### Jakarta EE Rules
```yaml
# jtrace.yml
rules:
  # EJB annotations
  - id: ejb-stateless
    type: requireAnnotation
    target: "com.example.ejb..*"
    annotation: "javax.ejb.Stateless"
    severity: error

  - id: ejb-stateful
    type: requireAnnotation
    target: "com.example.ejb.stateful..*"
    annotation: "javax.ejb.Stateful"
    severity: error

  # Web annotations
  - id: web-servlet
    type: requireAnnotation
    target: "com.example.web..*"
    annotation: "javax.servlet.annotation.WebServlet"
    severity: error

  - id: web-filter
    type: requireAnnotation
    target: "com.example.filter..*"
    annotation: "javax.servlet.annotation.WebFilter"
    severity: error

  # JPA annotations
  - id: jpa-entity
    type: requireAnnotation
    target: "com.example.entity..*"
    annotation: "javax.persistence.Entity"
    severity: error

  # Layering
  - id: jee-layering
    type: layering
    layers:
      - name: "web"
        packages: ["com.example.web..*", "com.example.servlet..*"]
      - name: "business"
        packages: ["com.example.ejb..*", "com.example.service..*"]
      - name: "data"
        packages: ["com.example.entity..*", "com.example.dao..*"]
    allowedDependencies:
      - from: "web"
        to: ["business"]
      - from: "business"
        to: ["data"]
    severity: error
```

### Microservices Architecture

#### Microservice Rules
```yaml
# jtrace.yml
rules:
  # Service boundaries
  - id: service-isolation
    type: forbiddenDependency
    from: "com.example.userservice..*"
    to: "com.example.orderservice..*"
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
    allowedDependencies:
      - from: "api"
        to: ["internal"]
      - from: "internal"
        to: ["infrastructure"]
    severity: error

  # External dependencies
  - id: external-deps
    type: forbiddenDependency
    from: "com.example.userservice..*"
    to: ["com.example.shared..*", "com.example.common..*"]
    severity: warning
    message: "Consider using interfaces for external dependencies"
```

### Clean Architecture / Hexagonal Architecture

#### Clean Architecture Rules
```yaml
# jtrace.yml
rules:
  # Domain layer isolation
  - id: domain-isolation
    type: forbiddenDependency
    from: "com.example.domain..*"
    to: ["com.example.infrastructure..*", "com.example.application..*", "com.example.presentation..*"]
    severity: error
    message: "Domain layer must not depend on external layers"

  # Infrastructure dependencies
  - id: infrastructure-deps
    type: forbiddenDependency
    from: "com.example.infrastructure..*"
    to: ["com.example.application..*", "com.example.presentation..*"]
    severity: error
    message: "Infrastructure layer must not depend on application or presentation"

  # Application layer boundaries
  - id: application-boundaries
    type: forbiddenDependency
    from: "com.example.application..*"
    to: ["com.example.presentation..*"]
    severity: error
    message: "Application layer must not depend on presentation"

  # Layering enforcement
  - id: clean-layering
    type: layering
    layers:
      - name: "domain"
        packages: ["com.example.domain..*"]
      - name: "application"
        packages: ["com.example.application..*"]
      - name: "infrastructure"
        packages: ["com.example.infrastructure..*"]
      - name: "presentation"
        packages: ["com.example.presentation..*"]
    allowedDependencies:
      - from: "presentation"
        to: ["application"]
      - from: "application"
        to: ["domain"]
      - from: "infrastructure"
        to: ["domain"]
    severity: error
```

## 🔧 Build System Integration

### Maven

#### Basic Plugin Configuration
```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
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
        <outputFormat>console</outputFormat>
        <outputFile>target/jtrace-report.txt</outputFile>
    </configuration>
    <executions>
        <execution>
            <id>architecture-check</id>
            <phase>verify</phase>
            <goals>
                <goal>scan</goal>
            </goals>
        </execution>
        <execution>
            <id>pre-commit-check</id>
            <phase>validate</phase>
            <goals>
                <goal>scan</goal>
            </goals>
            <configuration>
                <failOnViolations>false</failOnViolations>
                <severity>warning</severity>
            </configuration>
        </execution>
    </executions>
</plugin>
```

#### Maven Commands
```bash
# Basic scan
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan

# Scan with custom config
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.configFile=custom-jtrace.yml

# Scan specific source directory
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.sourceDirectory=src/main/java

# Generate report
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=html \
    -Djtrace.outputFile=target/architecture-report.html
```

### Gradle

#### Basic Plugin Configuration
```groovy
// build.gradle
plugins {
    id 'io.jtrace.gradle' version '0.1.0-SNAPSHOT'
}

jtrace {
    configFile = file('jtrace.yml')
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
}

// Custom task configuration
tasks.named('jtraceScan') {
    doFirst {
        println "Starting architecture analysis..."
    }
    doLast {
        println "Architecture analysis completed"
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

#### Gradle Commands
```bash
# Basic scan
./gradlew jtraceScan

# Scan with custom config
./gradlew jtraceScan -Pjtrace.configFile=custom-jtrace.yml

# Scan specific source directory
./gradlew jtraceScan -Pjtrace.sourceDirectory=src/main/java

# Generate report
./gradlew jtraceScan -Pjtrace.outputFormat=html -Pjtrace.outputFile=build/reports/architecture.html
```

## 🚀 Runtime Integration

### Java Agent

#### Basic Usage
```bash
# Development
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -jar your-application.jar

# Production with enforcement
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -Djtrace.mode=enforce \
     -Djtrace.failOnViolation=true \
     -jar your-application.jar

# Development with monitoring only
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -Djtrace.mode=monitor \
     -jar your-application.jar
```

#### Agent Configuration Options
```bash
# Configuration file path
-Djtrace.config=jtrace.yml

# Operating mode (monitor, enforce, report)
-Djtrace.mode=enforce

# Fail on violations (true/false)
-Djtrace.failOnViolation=true

# Log level (DEBUG, INFO, WARN, ERROR)
-Djtrace.logLevel=INFO

# Output format (console, file, json)
-Djtrace.outputFormat=console

# Output file path
-Djtrace.outputFile=jtrace-violations.log

# Include stack traces
-Djtrace.includeStackTraces=true

# Performance monitoring
-Djtrace.enablePerformanceMonitoring=true
```

### Docker Integration

#### Dockerfile Example
```dockerfile
FROM openjdk:17-jre-slim

# Install JTrace agent
COPY jtrace-agent-0.1.0-SNAPSHOT.jar /app/
COPY jtrace.yml /app/

# Copy application
COPY your-application.jar /app/

# Set working directory
WORKDIR /app

# Run with JTrace agent
ENTRYPOINT ["java", \
    "-javaagent:/app/jtrace-agent-0.1.0-SNAPSHOT.jar", \
    "-Djtrace.config=/app/jtrace.yml", \
    "-Djtrace.mode=enforce", \
    "-Djtrace.failOnViolation=true", \
    "-jar", "/app/your-application.jar"]
```

#### Docker Compose
```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    build: .
    environment:
      - JTRACE_CONFIG=/app/jtrace.yml
      - JTRACE_MODE=enforce
      - JTRACE_FAIL_ON_VIOLATION=true
    volumes:
      - ./jtrace.yml:/app/jtrace.yml:ro
    ports:
      - "8080:8080"
```

### Kubernetes Integration

#### ConfigMap for JTrace Configuration
```yaml
# jtrace-configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: jtrace-config
data:
  jtrace.yml: |
    version: 1
    basePackage: "com.example"
    failOn:
      severity: "error"
    rules:
      - id: k8s-rule
        type: forbiddenDependency
        from: "com.example.controller..*"
        to: "com.example.repository..*"
        severity: error
        message: "Controllers must not access repositories directly."
```

#### Deployment with JTrace Agent
```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: my-app
        image: my-app:latest
        command: ["java"]
        args:
        - "-javaagent:/app/jtrace-agent-0.1.0-SNAPSHOT.jar"
        - "-Djtrace.config=/app/jtrace.yml"
        - "-Djtrace.mode=enforce"
        - "-jar"
        - "/app/application.jar"
        volumeMounts:
        - name: jtrace-config
          mountPath: /app/jtrace.yml
          subPath: jtrace.yml
        - name: jtrace-agent
          mountPath: /app/jtrace-agent-0.1.0-SNAPSHOT.jar
          subPath: jtrace-agent-0.1.0-SNAPSHOT.jar
      volumes:
      - name: jtrace-config
        configMap:
          name: jtrace-config
      - name: jtrace-agent
        configMap:
          name: jtrace-agent
```

## 🔍 Troubleshooting

### Common Issues & Solutions

#### 1. Build Failures

**Problem**: Maven build fails with JTrace errors
```bash
[ERROR] Failed to execute goal io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan
```

**Solutions**:
```bash
# Check JTrace configuration
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan -X

# Validate YAML syntax
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate --config jtrace.yml

# Check for syntax errors in jtrace.yml
yamllint jtrace.yml

# Run with verbose output
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan -Djtrace.verbose=true
```

#### 2. Performance Issues

**Problem**: JTrace analysis is slow
```yaml
# jtrace.yml - Optimize patterns
rules:
  # Instead of broad patterns
  - id: slow-rule
    type: forbiddenDependency
    from: "com.example..*"  # Too broad
    to: "com.example..*"    # Too broad

  # Use specific patterns
  - id: fast-rule
    type: forbiddenDependency
    from: "com.example.controller.UserController"
    to: "com.example.repository.UserRepository"
```

**Performance Tips**:
```bash
# Run during build, not IDE
mvn verify

# Use specific package patterns
# Limit rule complexity
# Cache results in CI/CD
```

#### 3. False Positives

**Problem**: JTrace reports violations that shouldn't be violations
```yaml
# jtrace.yml - Add exclusions
rules:
  - id: flexible-rule
    type: forbiddenDependency
    from: "com.example.controller..*"
    to: "com.example.repository..*"
    exclude:
      - "com.example.controller.util.ValidationHelper"
      - "com.example.controller.config.ConfigurationLoader"
      - "com.example.repository.spec.CustomSpecification"
```

#### 4. Configuration Issues

**Problem**: JTrace can't find configuration file
```bash
# Check file path
ls -la jtrace.yml

# Use absolute path
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.configFile=/absolute/path/to/jtrace.yml

# Check file permissions
chmod 644 jtrace.yml
```

#### 5. Java Agent Issues

**Problem**: Java agent not loading
```bash
# Check agent JAR exists
ls -la jtrace-agent-0.1.0-SNAPSHOT.jar

# Verify Java agent syntax
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar -version

# Check agent logs
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.logLevel=DEBUG \
     -jar your-app.jar
```

#### 6. Pattern Matching Issues

**Problem**: Rules not matching expected packages
```yaml
# jtrace.yml - Debug pattern matching
rules:
  # Test with simple patterns first
  - id: test-rule
    type: forbiddenDependency
    from: "com.example.controller.UserController"  # Exact class
    to: "com.example.repository.UserRepository"    # Exact class

  # Then expand to patterns
  - id: pattern-rule
    type: forbiddenDependency
    from: "com.example.controller..*"  # Recursive wildcard
    to: "com.example.repository..*"    # Recursive wildcard
```

**Debug Commands**:
```bash
# Test pattern matching
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar test-pattern \
    --pattern "com.example.controller..*" \
    --package "com.example.controller.UserController"

# Validate specific rule
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate-rule \
    --config jtrace.yml \
    --rule-id "test-rule"
```

### Debug Mode

#### Enable Debug Logging
```bash
# Maven
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.verbose=true \
    -Djtrace.logLevel=DEBUG

# Gradle
./gradlew jtraceScan -Pjtrace.verbose=true -Pjtrace.logLevel=DEBUG

# Java Agent
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.logLevel=DEBUG \
     -Djtrace.verbose=true \
     -jar your-app.jar
```

#### Debug Output
```bash
# Check JTrace logs
tail -f jtrace.log

# Monitor system resources
top -p $(pgrep -f jtrace)

# Check JVM memory
jstat -gc $(pgrep -f jtrace)
```

## 📚 Additional Resources

- [📖 Complete Documentation](README.md)
- [📋 Command Reference](COMMAND_REFERENCE.md)
- [🏗️ Architecture Examples](ARCHITECTURE_EXAMPLES.md)
- [🚀 Deployment Guide](DEPLOYMENT_SUMMARY.md)
- [🔧 Troubleshooting FAQ](TROUBLESHOOTING_FAQ.md)

---

**Need help? Check the troubleshooting section above or refer to the additional resources!** 🆘
