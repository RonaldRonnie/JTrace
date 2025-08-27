# JTrace - Command Reference

## 📋 Overview

This document provides a comprehensive reference for all JTrace commands, options, and usage patterns across different integration methods.

## 🚀 Maven Plugin Commands

### Basic Scan Command
```bash
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan
```

### Command Options
```bash
# Custom configuration file
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.configFile=custom-jtrace.yml

# Custom source directory
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.sourceDirectory=src/main/java

# Custom output format
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=html

# Custom output file
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFile=target/architecture-report.html

# Verbose output
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.verbose=true

# Fail on violations
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.failOnViolations=true

# Include test sources
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.includeTests=true

# Custom log level
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.logLevel=DEBUG
```

### Maven Plugin Configuration Options

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `configFile` | String | `jtrace.yml` | Path to JTrace configuration file |
| `sourceDirectory` | String | `src/main/java` | Source directory to analyze |
| `outputFormat` | String | `console` | Output format (console, html, json, sarif) |
| `outputFile` | String | - | Output file path |
| `verbose` | boolean | `false` | Enable verbose output |
| `failOnViolations` | boolean | `false` | Fail build on violations |
| `includeTests` | boolean | `false` | Include test sources in analysis |
| `logLevel` | String | `INFO` | Log level (DEBUG, INFO, WARN, ERROR) |
| `severity` | String | `error` | Minimum severity to report |

## 🔧 Gradle Plugin Commands

### Basic Scan Command
```bash
./gradlew jtraceScan
```

### Command Options
```bash
# Custom configuration
./gradlew jtraceScan -Pjtrace.configFile=custom-jtrace.yml

# Custom source directory
./gradlew jtraceScan -Pjtrace.sourceDirectory=src/main/java

# Custom output format
./gradlew jtraceScan -Pjtrace.outputFormat=html

# Custom output file
./gradlew jtraceScan -Pjtrace.outputFile=build/reports/architecture.html

# Verbose output
./gradlew jtraceScan -Pjtrace.verbose=true

# Fail on violations
./gradlew jtraceScan -Pjtrace.failOnViolations=true

# Include test sources
./gradlew jtraceScan -Pjtrace.includeTests=true

# Custom log level
./gradlew jtraceScan -Pjtrace.logLevel=DEBUG
```

### Gradle Plugin Configuration Options

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `configFile` | File | `jtrace.yml` | Path to JTrace configuration file |
| `sourceDirectory` | String | `src/main/java` | Source directory to analyze |
| `outputFormat` | String | `console` | Output format (console, html, json, sarif) |
| `outputFile` | File | - | Output file path |
| `verbose` | boolean | `false` | Enable verbose output |
| `failOnViolations` | boolean | `false` | Fail build on violations |
| `includeTests` | boolean | `false` | Include test sources in analysis |
| `logLevel` | String | `INFO` | Log level (DEBUG, INFO, WARN, ERROR) |
| `severity` | String | `error` | Minimum severity to report |

## 🎯 CLI Commands

### Basic Commands
```bash
# Scan for violations
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan

# Initialize configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init

# Validate configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate

# Generate report
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar report

# Show help
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar --help
```

### Scan Command
```bash
# Basic scan
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan

# Custom configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --config custom-jtrace.yml

# Custom source directory
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --src src/main/java

# Custom output format
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --format html

# Custom output file
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --output report.html

# Verbose output
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --verbose

# Fail on violations
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --fail-on error

# Include test sources
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --include-tests

# Custom log level
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --log-level DEBUG
```

### Init Command
```bash
# Initialize with default configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init

# Custom output file
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --output custom-jtrace.yml

# Template-based initialization
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --template spring-boot

# Available templates: spring-boot, jakarta-ee, microservices, clean-architecture
```

### Validate Command
```bash
# Validate configuration file
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate --config jtrace.yml

# Validate specific rule
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate --rule-id "test-rule"

# Validate with verbose output
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate --verbose
```

### Report Command
```bash
# Generate HTML report
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar report --format html --output report.html

# Generate JSON report
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar report --format json --output report.json

# Generate SARIF report
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar report --format sarif --output report.sarif

# Generate console report
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar report --format console
```

## 🚀 Java Agent Commands

### Basic Usage
```bash
# Development mode
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -jar your-application.jar

# Production mode with enforcement
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -Djtrace.mode=enforce \
     -jar your-application.jar

# Monitoring mode only
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -Djtrace.mode=monitor \
     -jar your-application.jar
```

### Agent Configuration Options

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `jtrace.config` | String | - | Path to JTrace configuration file |
| `jtrace.mode` | String | `monitor` | Operating mode (monitor, enforce, report) |
| `jtrace.failOnViolation` | boolean | `false` | Fail on violations |
| `jtrace.logLevel` | String | `INFO` | Log level (DEBUG, INFO, WARN, ERROR) |
| `jtrace.outputFormat` | String | `console` | Output format |
| `jtrace.outputFile` | String | - | Output file path |
| `jtrace.includeStackTraces` | boolean | `false` | Include stack traces |
| `jtrace.enablePerformanceMonitoring` | boolean | `false` | Enable performance monitoring |

## 🔍 Advanced Commands

### Pattern Testing
```bash
# Test pattern matching
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar test-pattern \
    --pattern "com.example.controller..*" \
    --package "com.example.controller.UserController"

# Test multiple patterns
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar test-pattern \
    --pattern "com.example.controller..*" \
    --packages "com.example.controller.UserController,com.example.controller.OrderController"
```

### Rule Validation
```bash
# Validate specific rule
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate-rule \
    --config jtrace.yml \
    --rule-id "no-controller-to-repository"

# Validate rule syntax
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate-rule \
    --config jtrace.yml \
    --rule-id "test-rule" \
    --syntax-only
```

### Performance Analysis
```bash
# Performance profiling
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan \
    --profile \
    --output profile-report.json

# Memory usage analysis
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan \
    --memory-profile \
    --output memory-report.json
```

## 📊 Output Formats

### Console Output
```bash
# Default console output
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan

# Colored output (if supported)
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=console \
    -Djtrace.colored=true
```

### HTML Output
```bash
# Generate HTML report
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=html \
    -Djtrace.outputFile=target/architecture-report.html

# HTML with custom template
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=html \
    -Djtrace.outputFile=target/architecture-report.html \
    -Djtrace.htmlTemplate=custom-template.html
```

### JSON Output
```bash
# Generate JSON report
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=json \
    -Djtrace.outputFile=target/architecture-report.json

# Pretty-printed JSON
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=json \
    -Djtrace.outputFile=target/architecture-report.json \
    -Djtrace.jsonPrettyPrint=true
```

### SARIF Output
```bash
# Generate SARIF report
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=sarif \
    -Djtrace.outputFile=target/architecture-report.sarif

# SARIF with custom schema
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=sarif \
    -Djtrace.outputFile=target/architecture-report.sarif \
    -Djtrace.sarifSchema=sarif-2.1.0.json
```

## 🔧 CI/CD Integration Commands

### GitHub Actions
```yaml
# .github/workflows/architecture-check.yml
- name: Architecture Check
  run: |
    mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
      -Djtrace.failOnViolations=true \
      -Djtrace.outputFormat=html \
      -Djtrace.outputFile=target/architecture-report.html
```

### Jenkins Pipeline
```groovy
// Jenkinsfile
stage('Architecture Check') {
    steps {
        sh '''
            mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
              -Djtrace.failOnViolations=true \
              -Djtrace.outputFormat=html \
              -Djtrace.outputFile=target/architecture-report.html
        '''
        publishHTML([
            allowMissing: false,
            alwaysLinkToLastBuild: true,
            keepAll: true,
            reportDir: 'target',
            reportFiles: 'architecture-report.html',
            reportName: 'Architecture Report'
        ])
    }
}
```

### GitLab CI
```yaml
# .gitlab-ci.yml
architecture-check:
  stage: test
  script:
    - mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan
      -Djtrace.failOnViolations=true
      -Djtrace.outputFormat=html
      -Djtrace.outputFile=target/architecture-report.html
  artifacts:
    reports:
      html: target/architecture-report.html
```

## 🐳 Docker Commands

### Docker Run
```bash
# Run with JTrace agent
docker run -it \
  -v $(pwd)/jtrace.yml:/app/jtrace.yml:ro \
  -v $(pwd)/jtrace-agent-0.1.0-SNAPSHOT.jar:/app/jtrace-agent.jar:ro \
  your-application:latest \
  java -javaagent:/app/jtrace-agent.jar \
       -Djtrace.config=/app/jtrace.yml \
       -jar /app/application.jar
```

### Docker Compose
```bash
# Start services with JTrace
docker-compose up -d

# View JTrace logs
docker-compose logs -f app

# Execute JTrace commands in container
docker-compose exec app java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan
```

## 🔍 Debug Commands

### Verbose Output
```bash
# Maven with verbose output
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.verbose=true \
    -Djtrace.logLevel=DEBUG \
    -X

# Gradle with verbose output
./gradlew jtraceScan \
    -Pjtrace.verbose=true \
    -Pjtrace.logLevel=DEBUG \
    --info

# CLI with verbose output
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan \
    --verbose \
    --log-level DEBUG
```

### Debug Mode
```bash
# Enable debug mode
export JTRACE_DEBUG=true

# Run with debug
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.debug=true

# Debug Java agent
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.debug=true \
     -Djtrace.logLevel=DEBUG \
     -jar your-app.jar
```

## 📚 Help Commands

### General Help
```bash
# Maven plugin help
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:help

# CLI help
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar --help

# Command-specific help
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar scan --help
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar init --help
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate --help
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar report --help
```

### Version Information
```bash
# Check JTrace version
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar --version

# Check Maven plugin version
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:help

# Check Gradle plugin version
./gradlew jtraceVersion
```

---

**For more detailed information, see the [Setup Guide](SETUP_GUIDE.md) and [Architecture Examples](ARCHITECTURE_EXAMPLES.md).**
