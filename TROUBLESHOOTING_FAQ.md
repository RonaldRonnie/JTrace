# JTrace - Troubleshooting FAQ

## 🚨 Common Issues & Solutions

### Build Failures

#### Q: Maven build fails with JTrace errors
**Problem**: 
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

#### Q: Gradle build fails with JTrace errors
**Problem**:
```bash
Execution failed for task ':jtraceScan'
```

**Solutions**:
```bash
# Check Gradle configuration
./gradlew jtraceScan --info

# Validate configuration
./gradlew jtraceScan -Pjtrace.verbose=true

# Check plugin version compatibility
./gradlew jtraceVersion
```

### Configuration Issues

#### Q: JTrace can't find configuration file
**Problem**: Configuration file not found error

**Solutions**:
```bash
# Check file path
ls -la jtrace.yml

# Use absolute path
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.configFile=/absolute/path/to/jtrace.yml

# Check file permissions
chmod 644 jtrace.yml

# Verify file encoding
file jtrace.yml
```

#### Q: YAML configuration syntax errors
**Problem**: Invalid YAML syntax

**Solutions**:
```bash
# Validate YAML syntax
yamllint jtrace.yml

# Use online YAML validator
# https://www.yamllint.com/

# Check for common issues:
# - Proper indentation (spaces, not tabs)
# - Correct quote usage
# - Valid rule structure
```

### Performance Issues

#### Q: JTrace analysis is very slow
**Problem**: Analysis takes too long

**Solutions**:
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
# Exclude test sources if not needed
```

#### Q: High memory usage during analysis
**Problem**: OutOfMemoryError during analysis

**Solutions**:
```bash
# Increase JVM memory
export MAVEN_OPTS="-Xmx2g -Xms1g"

# For Gradle
export GRADLE_OPTS="-Xmx2g -Xms1g"

# Limit analysis scope
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.sourceDirectory=src/main/java \
    -Djtrace.includeTests=false
```

### False Positives

#### Q: JTrace reports violations that shouldn't be violations
**Problem**: Incorrect violation detection

**Solutions**:
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

**Debug Steps**:
```bash
# Enable verbose output
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.verbose=true \
    -Djtrace.logLevel=DEBUG

# Test specific patterns
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar test-pattern \
    --pattern "com.example.controller..*" \
    --package "com.example.controller.UserController"
```

### Java Agent Issues

#### Q: Java agent not loading
**Problem**: Agent not starting or working

**Solutions**:
```bash
# Check agent JAR exists
ls -la jtrace-agent-0.1.0-SNAPSHOT.jar

# Verify Java agent syntax
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar -version

# Check agent logs
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.logLevel=DEBUG \
     -jar your-app.jar

# Verify agent configuration
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.config=jtrace.yml \
     -Djtrace.verbose=true \
     -jar your-app.jar
```

#### Q: Agent causes application startup failure
**Problem**: Application fails to start with agent

**Solutions**:
```bash
# Start in monitor mode only
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.mode=monitor \
     -Djtrace.failOnViolation=false \
     -jar your-app.jar

# Check agent compatibility
java -javaagent:jtrace-agent-0.1.0-SNAPSHOT.jar \
     -Djtrace.logLevel=DEBUG \
     -Djtrace.verbose=true \
     -jar your-app.jar

# Verify Java version compatibility
java -version
```

### Pattern Matching Issues

#### Q: Rules not matching expected packages
**Problem**: Pattern matching not working as expected

**Solutions**:
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

### Integration Issues

#### Q: Maven plugin not found
**Problem**: Plugin resolution failure

**Solutions**:
```bash
# Check plugin availability
mvn help:describe -Dplugin=io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT

# Verify repository access
mvn help:effective-settings

# Check local repository
ls -la ~/.m2/repository/io/jtrace/

# Reinstall plugin
mvn clean install -pl jtrace-maven-plugin
```

#### Q: Gradle plugin not working
**Problem**: Gradle plugin tasks not available

**Solutions**:
```bash
# Check available tasks
./gradlew tasks --all | grep jtrace

# Verify plugin application
./gradlew jtraceVersion

# Check build script syntax
./gradlew build --info

# Verify plugin compatibility
./gradlew --version
```

### CI/CD Issues

#### Q: JTrace fails in CI/CD pipeline
**Problem**: Build failures in automated environments

**Solutions**:
```yaml
# GitHub Actions - Add debugging
- name: Architecture Check
  run: |
    mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
      -Djtrace.verbose=true \
      -Djtrace.logLevel=DEBUG \
      -Djtrace.failOnViolations=false  # Start with warnings only
```

**CI/CD Tips**:
```bash
# Start with warnings, not errors
-Djtrace.failOnViolations=false

# Generate reports for review
-Djtrace.outputFormat=html
-Djtrace.outputFile=target/architecture-report.html

# Use verbose output for debugging
-Djtrace.verbose=true
-Djtrace.logLevel=DEBUG
```

### Reporting Issues

#### Q: No violations reported when expected
**Problem**: JTrace reports "No violations found" but violations exist

**Solutions**:
```bash
# Check configuration file
cat jtrace.yml

# Verify source directory
ls -la src/main/java/

# Test with verbose output
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.verbose=true \
    -Djtrace.logLevel=DEBUG

# Check rule patterns
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate --config jtrace.yml
```

#### Q: HTML/JSON reports not generated
**Problem**: Report files not created

**Solutions**:
```bash
# Check output directory permissions
ls -la target/

# Verify output format
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.outputFormat=html \
    -Djtrace.outputFile=target/architecture-report.html

# Check for errors in build output
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.verbose=true
```

## 🔍 Debug Mode

### Enable Debug Logging
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

### Debug Output
```bash
# Check JTrace logs
tail -f jtrace.log

# Monitor system resources
top -p $(pgrep -f jtrace)

# Check JVM memory
jstat -gc $(pgrep -f jtrace)

# Enable system property debugging
-Djtrace.debug=true
```

## 📚 Additional Resources

### Documentation
- [Setup Guide](SETUP_GUIDE.md)
- [Command Reference](COMMAND_REFERENCE.md)
- [Architecture Examples](ARCHITECTURE_EXAMPLES.md)

### Support Channels
- **GitHub Issues**: Report bugs and feature requests
- **Documentation**: Check the guides above
- **Examples**: See `jtrace-examples` module
- **Community**: Join discussions and share solutions

### Common Patterns

#### Quick Fix Commands
```bash
# Reset and rebuild
mvn clean install

# Validate configuration
java -jar jtrace-cli-0.1.0-SNAPSHOT.jar validate --config jtrace.yml

# Test with minimal configuration
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:scan \
    -Djtrace.verbose=true \
    -Djtrace.failOnViolations=false

# Check plugin status
mvn help:describe -Dplugin=io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT
```

---

**Still having issues? Check the [Setup Guide](SETUP_GUIDE.md) or report a problem through GitHub Issues!** 🆘
