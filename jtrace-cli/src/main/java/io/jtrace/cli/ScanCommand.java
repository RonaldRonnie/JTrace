package io.jtrace.cli;

import io.jtrace.core.config.JTraceConfig;
import io.jtrace.core.config.JTraceConfigLoader;
import io.jtrace.core.engine.RuleEngine;
import io.jtrace.core.model.Violation;
import io.jtrace.core.report.ConsoleReporter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to scan source code for architecture violations.
 */
@Command(
    name = "scan",
    description = "Scan source code for architecture violations"
)
public class ScanCommand implements Runnable {

    @Option(names = {"--config", "-c"}, description = "Configuration file path", defaultValue = "jtrace.yml")
    private Path configFile;

    @Option(names = {"--src", "-s"}, description = "Source directory", defaultValue = "src/main/java")
    private Path sourceDir;

    @Option(names = {"--format", "-f"}, description = "Output format", defaultValue = "console")
    private String format;

    @Option(names = {"--fail-on"}, description = "Fail on violations at or above this severity", defaultValue = "error")
    private String failOn;

    @Override
    public void run() {
        try {
            // Load configuration
            JTraceConfigLoader loader = new JTraceConfigLoader();
            JTraceConfig config = loader.loadFromFile(configFile);

            // Find source files
            List<Path> sourcePaths = findSourceFiles(sourceDir);
            if (sourcePaths.isEmpty()) {
                System.err.println("No Java source files found in " + sourceDir);
                System.exit(1);
            }

            // Run analysis
            RuleEngine engine = new RuleEngine();
            List<Violation> violations = engine.run(config, sourcePaths);

            // Report results
            if ("console".equals(format)) {
                ConsoleReporter reporter = new ConsoleReporter();
                reporter.report(violations);
            }

            // Exit with appropriate code
            int exitCode = determineExitCode(violations, config.getFailOn().getSeverity());
            System.exit(exitCode);

        } catch (Exception e) {
            System.err.println("Error during scan: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private List<Path> findSourceFiles(Path sourceDir) throws Exception {
        List<Path> sourceFiles = new ArrayList<>();
        if (Files.exists(sourceDir)) {
            Files.walk(sourceDir)
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(sourceFiles::add);
        }
        return sourceFiles;
    }

    private int determineExitCode(List<Violation> violations, io.jtrace.core.model.Severity failOnSeverity) {
        for (Violation violation : violations) {
            if (violation.getSeverity().isAtLeast(failOnSeverity)) {
                return 1; // Violations found at or above threshold
            }
        }
        return 0; // No violations or all below threshold
    }
}
