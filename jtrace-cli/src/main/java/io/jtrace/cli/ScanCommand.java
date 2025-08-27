package io.jtrace.cli;

import io.jtrace.core.config.JTraceConfig;
import io.jtrace.core.config.JTraceConfigLoader;
import io.jtrace.core.engine.RuleEngine;
import io.jtrace.core.model.Violation;
import io.jtrace.core.report.ConsoleReporter;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Command to scan source code for architecture violations.
 */
@CommandLine.Command(
    name = "scan",
    description = "Scan source code for architecture violations"
)
public class ScanCommand implements Runnable {
    
    @CommandLine.Option(
        names = {"--config", "-c"},
        description = "Configuration file path",
        defaultValue = "jtrace.yml"
    )
    private String configFile;
    
    @CommandLine.Option(
        names = {"--src", "-s"},
        description = "Source directory path",
        defaultValue = "src/main/java"
    )
    private String sourceDir;
    
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
            ConsoleReporter reporter = new ConsoleReporter();
            reporter.report(violations);

            // Exit with error code if violations exceed failOn threshold
            if (config.getFailOn().shouldFail(violations)) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error during analysis: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private List<Path> findSourceFiles(String sourceDir) throws Exception {
        List<Path> sourceFiles = new ArrayList<>();
        if (Files.exists(Path.of(sourceDir))) {
            Files.walk(Path.of(sourceDir))
                .filter(path -> path.toString().endsWith(".java"))
                .forEach(sourceFiles::add);
        }
        return sourceFiles;
    }
}
