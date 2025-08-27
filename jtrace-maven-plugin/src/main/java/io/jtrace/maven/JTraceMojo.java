package io.jtrace.maven;

import io.jtrace.core.config.JTraceConfig;
import io.jtrace.core.config.JTraceConfigLoader;
import io.jtrace.core.engine.RuleEngine;
import io.jtrace.core.model.Violation;
import io.jtrace.core.model.Severity;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Maven plugin Mojo for JTrace architecture enforcement.
 */
@Mojo(name = "scan", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
@Execute(goal = "scan", phase = LifecyclePhase.VERIFY)
public class JTraceMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "jtrace.configFile", defaultValue = "jtrace.yml")
    private String configFile;

    @Parameter(property = "jtrace.failOn", defaultValue = "error")
    private String failOn;

    @Parameter(property = "jtrace.skip", defaultValue = "false")
    private boolean skip;

    @Parameter(property = "jtrace.sourceDirectory", defaultValue = "${project.build.sourceDirectory}")
    private String sourceDirectory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skip) {
            getLog().info("JTrace analysis skipped");
            return;
        }

        try {
            getLog().info("Starting JTrace architecture analysis...");

            // Load configuration
            JTraceConfigLoader loader = new JTraceConfigLoader();
            Path configPath = Paths.get(project.getBasedir().getAbsolutePath(), configFile);
            JTraceConfig config = loader.loadFromFile(configPath);

            // Find source files
            Path sourcePath = Paths.get(sourceDirectory);
            List<Path> sourcePaths = List.of(sourcePath);

            // Run analysis
            RuleEngine engine = new RuleEngine();
            List<Violation> violations = engine.run(config, sourcePaths);

            // Report results
            reportViolations(violations);

            // Check if build should fail
            Severity failOnSeverity = Severity.fromString(failOn);
            if (shouldFailBuild(violations, failOnSeverity)) {
                throw new MojoFailureException(
                    "JTrace found architecture violations at or above severity level: " + failOnSeverity.getValue());
            }

            getLog().info("JTrace analysis completed successfully");

        } catch (Exception e) {
            throw new MojoExecutionException("JTrace analysis failed", e);
        }
    }

    private void reportViolations(List<Violation> violations) {
        if (violations.isEmpty()) {
            getLog().info("No architecture violations found");
            return;
        }

        getLog().warn("Found " + violations.size() + " architecture violations:");

        for (Violation violation : violations) {
            String message = String.format("[%s] %s: %s at %s",
                violation.getSeverity().getValue().toUpperCase(),
                violation.getRuleId(),
                violation.getMessage(),
                violation.getLocation());
            
            switch (violation.getSeverity()) {
                case ERROR:
                    getLog().error(message);
                    break;
                case WARNING:
                    getLog().warn(message);
                    break;
                case INFO:
                    getLog().info(message);
                    break;
            }
        }
    }

    private boolean shouldFailBuild(List<Violation> violations, Severity failOnSeverity) {
        return violations.stream()
            .anyMatch(violation -> violation.getSeverity().isAtLeast(failOnSeverity));
    }
}
