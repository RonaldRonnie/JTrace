package io.jtrace.core.engine;

import io.jtrace.core.analysis.*;
import io.jtrace.core.config.JTraceConfig;
import io.jtrace.core.importer.SourceImporter;
import io.jtrace.core.model.*;
import io.jtrace.core.match.PatternMatcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main engine that orchestrates the analysis of source code against architecture rules.
 */
public class RuleEngine {
    private final DependencyAnalyzer dependencyAnalyzer;
    private final AnnotationAnalyzer annotationAnalyzer;
    private final LayeringAnalyzer layeringAnalyzer;
    private final CycleDetector cycleDetector;
    private final PatternMatcher patternMatcher;
    private final ExecutorService executor;

    public RuleEngine() {
        this.patternMatcher = new PatternMatcher();
        this.dependencyAnalyzer = new DependencyAnalyzer(patternMatcher);
        this.annotationAnalyzer = new AnnotationAnalyzer(patternMatcher);
        this.layeringAnalyzer = new LayeringAnalyzer(patternMatcher);
        this.cycleDetector = new CycleDetector();
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    /**
     * Runs the analysis engine against the given configuration and source set.
     */
    public List<Violation> run(JTraceConfig config, List<Path> sourcePaths) {
        try {
            // Import source code
            SourceImporter importer = new SourceImporter();
            ProjectModel projectModel = importer.importSources(sourcePaths);

            List<Violation> violations = new ArrayList<>();

            // Analyze each rule type
            for (Rule rule : config.getRules()) {
                List<Violation> ruleViolations = analyzeRule(rule, projectModel);
                violations.addAll(ruleViolations);
            }

            return violations;
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Runs the analysis engine asynchronously.
     */
    public CompletableFuture<List<Violation>> runAsync(JTraceConfig config, List<Path> sourcePaths) {
        return CompletableFuture.supplyAsync(() -> run(config, sourcePaths), executor);
    }

    private List<Violation> analyzeRule(Rule rule, ProjectModel projectModel) {
        switch (rule.getType()) {
            case "forbiddenDependency":
                return dependencyAnalyzer.analyze((ForbiddenDependencyRule) rule, projectModel);
            case "requireAnnotation":
                return annotationAnalyzer.analyze((RequireAnnotationRule) rule, projectModel);
            case "layering":
                return layeringAnalyzer.analyze((LayeringRule) rule, projectModel);
            case "visibility":
                return analyzeVisibilityRule((VisibilityRule) rule, projectModel);
            default:
                throw new IllegalArgumentException("Unknown rule type: " + rule.getType());
        }
    }

    private List<Violation> analyzeVisibilityRule(VisibilityRule rule, ProjectModel projectModel) {
        // TODO: Implement visibility analysis
        return new ArrayList<>();
    }

    /**
     * Shuts down the rule engine and releases resources.
     */
    public void shutdown() {
        executor.shutdown();
    }
}
