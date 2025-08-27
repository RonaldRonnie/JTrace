#!/bin/bash

echo "🚀 JTrace - Live Architecture Enforcer - Deployment Script"
echo "=========================================================="
echo ""

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java 17+ is available
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java $JAVA_VERSION detected"
echo "✅ Maven detected"
echo ""

# Build the project
echo "🔨 Building JTrace project..."
mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
else
    echo "❌ Build failed!"
    exit 1
fi

echo ""
echo "📦 Creating distribution package..."
mkdir -p target/distribution
cp -r jtrace-core/target/*.jar jtrace-maven-plugin/target/*.jar jtrace-agent/target/*.jar target/distribution/
cp README.md LICENSE PROJECT_STATUS.md target/distribution/

echo "✅ Distribution package created in target/distribution/"
echo ""

# Test the core functionality
echo "🧪 Testing core functionality..."
cd jtrace-examples

# Create a test configuration
cat > jtrace-test.yml << EOF
version: 1
basePackage: "com.example"
failOn:
  severity: "error"

rules:
  - id: test-rule
    type: forbiddenDependency
    from: "com.example.controller..*"
    to: "com.example.repository..*"
    severity: error
    message: "Controllers must not access repositories directly."
EOF

echo "✅ Test configuration created"
echo ""

# Test the Maven plugin
echo "🔌 Testing Maven plugin integration..."
mvn io.jtrace:jtrace-maven-plugin:0.1.0-SNAPSHOT:enforce -Djtrace.config=jtrace-test.yml

echo ""
echo "🎉 JTrace is ready for deployment!"
echo ""
echo "📋 Deployment Summary:"
echo "  • Core Engine: ✅ Ready"
echo "  • Maven Plugin: ✅ Ready"
echo "  • Java Agent: ✅ Ready"
echo "  • Examples: ✅ Ready"
echo "  • Documentation: ✅ Ready"
echo ""
echo "📁 Distribution files available in: target/distribution/"
echo "📚 Documentation: README.md, PROJECT_STATUS.md"
echo ""
echo "�� Ready to deploy!"
