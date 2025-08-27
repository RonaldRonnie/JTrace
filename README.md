# JTrace - Live Architecture Enforcer

JTrace is a powerful tool that lets teams define architecture rules as code and enforces them via static analysis at build/CI time and an optional runtime Java Agent that detects/blocks forbidden interactions.

## Features

- **Static Analysis**: Analyze your codebase for architecture violations during build time
- **Runtime Enforcement**: Optional Java Agent for runtime policy enforcement
- **Multiple Rule Types**: Support for dependency rules, layering, annotations, and visibility
- **Flexible Patterns**: Glob patterns, regex, and wildcards for package matching
- **CI/CD Integration**: Maven and Gradle plugins for seamless integration
- **Rich Reporting**: Console, HTML, JSON, and SARIF report formats
- **Java 17+**: Built for modern Java applications

## Quick Start

### 1. Install JTrace

```bash
# Download the latest release
wget https://github.com/jtrace/jtrace/releases/latest/download/jtrace-cli.jar

# Or build from source
git clone https://github.com/jtrace/jtrace.git
cd jtrace
mvn clean install
```

### 2. Initialize Configuration

```bash
java -jar jtrace-cli.jar init
```

This creates a `jtrace.yml` file with example rules.

### 3. Scan Your Project

```bash
java -jar jtrace-cli.jar scan --src src/main/java
```

### 4. Enforce Rules

```bash
java -jar jtrace-cli.jar enforce --src src/main/java
```

## Configuration

JTrace uses YAML configuration files. Here's an example:

```yaml
version: 1
basePackage: "com.myapp"
failOn:
  severity: "error"

rules:
  # Forbidden dependencies
  - id: no-controller-to-repository
    type: forbiddenDependency
    from: "com.myapp.controller..*"
    to: "com.myapp.repository..*"
    severity: error
    message: "Controllers must not access repositories directly"

  # Required annotations
  - id: service-transactional
    type: requireAnnotation
    in: "com.myapp.service..*"
    target: method
    annotation: "org.springframework.transaction.annotation.Transactional"
    severity: warning
    message: "Service methods should be transactional"

  # Layering architecture
  - id: layering
    type: layering
    layers:
      - name: controller
        packages: ["com.myapp.controller..*"]
      - name: service
        packages: ["com.myapp.service..*"]
      - name: repository
        packages: ["com.myapp.repository..*"]
    allowedDependencies:
      - from: controller
        to: service
      - from: service
        to: repository
    forbidCycles: true
    severity: error
```

## Rule Types

### Forbidden Dependency Rules
Prevent specific package dependencies:

```yaml
- id: no-web-to-db
  type: forbiddenDependency
  from: "com.myapp.web..*"
  to: "com.myapp.database..*"
  severity: error
  message: "Web layer cannot directly access database layer"
```

### Required Annotation Rules
Ensure classes, methods, or fields have specific annotations:

```yaml
- id: entity-validation
  type: requireAnnotation
  in: "com.myapp.entity..*"
  target: class
  annotation: "javax.validation.constraints.Valid"
  severity: warning
  message: "Entity classes should have validation annotations"
```

### Layering Rules
Enforce architectural layers and dependencies:

```yaml
- id: clean-architecture
  type: layering
  layers:
    - name: presentation
      packages: ["com.myapp.presentation..*"]
    - name: business
      packages: ["com.myapp.business..*"]
    - name: data
      packages: ["com.myapp.data..*"]
  allowedDependencies:
    - from: presentation
      to: business
    - from: business
      to: data
  forbidCycles: true
  severity: error
```

### Visibility Rules
Control access modifiers:

```yaml
- id: domain-encapsulation
  type: visibility
  target: class
  in: "com.myapp.domain..*"
  mustBe: "package-private"
  severity: warning
  message: "Domain classes should be package-private"
```

## Integration

### Maven Plugin

Add to your `pom.xml`:

```xml
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <executions>
        <execution>
            <goals>
                <goal>scan</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Run with:
```bash
mvn jtrace:scan
```

### Gradle Plugin

Add to your `build.gradle`:

```groovy
plugins {
    id 'io.jtrace.gradle' version '0.1.0-SNAPSHOT'
}

jtrace {
    configFile = 'jtrace.yml'
    failOn = 'error'
}
```

### Runtime Agent

For runtime enforcement, add the JVM argument:

```bash
-javaagent:jtrace-agent.jar
```

## CLI Commands

### Scan
Analyze your codebase for violations:
```bash
jtrace scan --config jtrace.yml --src src/main/java
```

### Enforce
Fail the build on violations:
```bash
jtrace enforce --config jtrace.yml --src src/main/java --fail-on error
```

### Report
Generate detailed reports:
```bash
jtrace report --config jtrace.yml --src src/main/java --format html --out reports/
```

### Init
Create a new configuration file:
```bash
jtrace init --output jtrace.yml
```

## Examples

Check out the `jtrace-examples` module for complete working examples with intentional architecture violations.

## Building from Source

```bash
git clone https://github.com/jtrace/jtrace.git
cd jtrace
mvn clean install
```

## Requirements

- Java 17 or higher
- Maven 3.6+ or Gradle 7.0+

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- Documentation: [https://jtrace.io/docs](https://jtrace.io/docs)
- Issues: [https://github.com/jtrace/jtrace/issues](https://github.com/jtrace/jtrace/issues)
- Discussions: [https://github.com/jtrace/jtrace/discussions](https://github.com/jtrace/jtrace/discussions)
