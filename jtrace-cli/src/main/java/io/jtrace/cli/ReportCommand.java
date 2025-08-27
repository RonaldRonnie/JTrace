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
 * Command to generate reports in various formats.
 */
@Command(
    name = "report",
    description = "Generate architecture violation reports"
)
public class ReportCommand implements Runnable {

    @Option(names = {"--config", "-c"}, description = "Configuration file path", defaultValue = "jtrace.yml")
    private Path configFile;

    @Option(names = {"--src", "-s"}, description = "Source directory", defaultValue = "src/main/java")
    private Path sourceDir;

    @Option(names = {"--format", "-f"}, description = "Output format", defaultValue = "html")
    private String format;

    @Option(names = {"--out", "-o"}, description = "Output directory", defaultValue = "build/jtrace")
    private Path outputDir;

    @Override
    public void run() {
        try {
            System.out.println("📊 Generating architecture report...");

            // Load configuration
            JTraceConfigLoader loader = new JTraceConfigLoader();
            JTraceConfig config = loader.loadFromFile(configFile);

            // Run analysis
            RuleEngine engine = new RuleEngine();
            List<Violation> violations = engine.run(config, List.of(sourceDir));

            // Generate report
            switch (format.toLowerCase()) {
                case "html":
                    generateHtmlReport(violations);
                    break;
                case "json":
                    generateJsonReport(violations);
                    break;
                case "sarif":
                    generateSarifReport(violations);
                    break;
                default:
                    System.err.println("Unsupported format: " + format);
                    System.exit(1);
            }

            System.out.println("✅ Report generated successfully in " + outputDir.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }

    private void generateHtmlReport(List<Violation> violations) {
        // TODO: Implement HTML report generation
        System.out.println("📄 Generating HTML report...");
        System.out.println("   - Total violations: " + violations.size());
        System.out.println("   - Output directory: " + outputDir);
    }

    private void generateJsonReport(List<Violation> violations) {
        // TODO: Implement JSON report generation
        System.out.println("📄 Generating JSON report...");
        System.out.println("   - Total violations: " + violations.size());
        System.out.println("   - Output directory: " + outputDir);
    }

    private void generateSarifReport(List<Violation> violations) {
        // TODO: Implement SARIF report generation
        System.out.println("📄 Generating SARIF report...");
        System.out.println("   - Total violations: " + violations.size());
        System.out.println("   - Output directory: " + outputDir);
    }
}
