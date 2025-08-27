# JTrace - Live Architecture Enforcer

JTrace lets teams define **architecture rules as code** and enforces them via:
1) Static analysis at build/CI time, and
2) An optional **runtime Java Agent** that detects/blocks forbidden interactions during tests or staging.

## 🚀 Features

- **Architecture Rules as Code**: Define rules in YAML or programmatically
- **Static Analysis**: Analyze source code at build time
- **Runtime Enforcement**: Optional Java Agent for live monitoring
- **Multiple Output Formats**: Console, JSON, SARIF, and HTML reports
- **Build Integration**: Maven and Gradle plugins
- **Fast & Incremental**: Efficient analysis with caching support

## 📋 Quick Start

### 1. Install JTrace CLI

```bash
# Build from source
git clone https://github.com/jtrace/jtrace.git
cd jtrace
mvn clean install

# Use the CLI
java -jar jtrace-cli/target/jtrace-cli-0.1.0-SNAPSHOT.jar --help
```

### 2. Initialize Configuration

```bash
jtrace init
```

This creates a `jtrace.yml` file with starter rules based on your project structure.

### 3. Run Analysis

```bash
# Scan your project
jtrace scan

# Generate HTML report
jtrace report --format=html --out=build/jtrace
```

### 4. Integrate with Build

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

## 📖 Rule DSL

JTrace supports several rule types:

### Forbidden Dependencies

```yaml
- id: no-controller-to-repository
  type: forbiddenDependency
  from: "com.myapp.controller..*"
  to: "com.myapp.repository..*"
  severity: error
  message: "Controllers must not access repositories directly"
```

### Required Annotations

```yaml
- id: service-methods-transactional
  type: requireAnnotation
  in: "com.myapp.service..*"
  target: method
  annotation: "org.springframework.transaction.annotation.Transactional"
  severity: warning
```

### Layering Architecture

```yaml
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

### Visibility Constraints

```yaml
- id: domain-classes-private
  type: visibility
  target: class
  in: "com.myapp.domain..*"
  mustBe: "package-private"
  severity: warning
```

## 🏗️ Project Structure

```
jtrace/
├── jtrace-core/           # Core rule engine and analyzers
├── jtrace-cli/            # Command-line interface
├── jtrace-maven-plugin/   # Maven plugin
├── jtrace-gradle-plugin/  # Gradle plugin
├── jtrace-agent/          # Runtime Java Agent
└── jtrace-examples/       # Example projects with violations
```

## 🔧 Configuration

### CLI Options

```bash
jtrace scan [--config jtrace.yml] [--src src/main/java] [--format console|json|sarif|html]
jtrace enforce [--fail-on error|warning]
jtrace report --format json|sarif|html --out build/jtrace
```

### Maven Plugin Configuration

```xml
<plugin>
    <groupId>io.jtrace</groupId>
    <artifactId>jtrace-maven-plugin</artifactId>
    <configuration>
        <configFile>jtrace.yml</configFile>
        <failOn>error</failOn>
        <skip>false</skip>
    </configuration>
</plugin>
```

## 🧪 Examples

See `jtrace-examples/` for a complete Jakarta EE application with intentional architecture violations that demonstrate JTrace's capabilities.

## 🚧 Development Status

This is a **preview release** (0.1.0-SNAPSHOT). Core functionality is implemented with TODO markers for advanced features.

### What's Working
- ✅ Basic rule model and configuration loading
- ✅ CLI framework with PicoCLI
- ✅ Maven plugin structure
- ✅ Example project with violations
- ✅ Project structure and build system

### What's Coming
- 🔄 Source code parsing and analysis
- 🔄 Rule violation detection
- 🔄 Report generation
- 🔄 Runtime Java Agent
- 🔄 Gradle plugin
- 🔄 Advanced rule types

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🔗 Links

- [Documentation](https://jtrace.io/docs)
- [Issues](https://github.com/jtrace/jtrace/issues)
- [Discussions](https://github.com/jtrace/jtrace/discussions)
