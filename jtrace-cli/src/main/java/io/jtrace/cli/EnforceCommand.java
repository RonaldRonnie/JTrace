package io.jtrace.cli;

import io.jtrace.core.config.JTraceConfig;
import io.jtrace.core.config.JTraceConfigLoader;
import io.jtrace.core.engine.RuleEngine;
import io.jtrace.core.model.Violation;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.List;

/**
 * Command to enforce architecture rules and fail on violations.
 */
@Command(
    name = "enforce",
    description = "Enforce architecture rules and fail on violations"
)
public class EnforceCommand implements Runnable {

    @Option(names = {"--config", "-c"}, description = "Configuration file path", defaultValue = "jtrace.yml")
    private Path configFile;

    @Option(names = {"--src", "-s"}, description = "Source directory", defaultValue = "src/main/java")
    private Path sourceDir;

    @Option(names = {"--fail-on"}, description = "Fail on violations at or above this severity", defaultValue = "error")
    private String failOn;

    @Override
    public void run() {
        try {
            System.out.println("🔍 Enforcing architecture rules...");

            // Load configuration
            JTraceConfigLoader loader = new JTraceConfigLoader();
            JTraceConfig config = loader.loadFromFile(configFile);

            // Run analysis
            RuleEngine engine = new RuleEngine();
            List<Violation> violations = engine.run(config, List.of(sourceDir));

            // Report results
            if (violations.isEmpty()) {
                System.out.println("✅ No architecture violations found");
                return;
            }

            System.out.println("❌ Found " + violations.size() + " architecture violations:");
            
            for (Violation violation : violations) {
                System.out.printf("[%s] %s: %s at %s%n",
                    violation.getSeverity().getValue().toUpperCase(),
                    violation.getRuleId(),
                    violation.getMessage(),
                    violation.getLocation());
            }

            // Check if we should fail
            io.jtrace.core.model.Severity failOnSeverity = io.jtrace.core.model.Severity.fromString(failOn);
            boolean shouldFail = violations.stream()
                .anyMatch(violation -> violation.getSeverity().isAtLeast(failOnSeverity));

            if (shouldFail) {
                System.err.println("💥 Architecture enforcement failed due to violations at or above severity: " + failOnSeverity.getValue());
                System.exit(1);
            } else {
                System.out.println("⚠️  Found violations but none at or above the fail threshold: " + failOnSeverity.getValue());
            }

        } catch (Exception e) {
            System.err.println("Error during enforcement: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
}
