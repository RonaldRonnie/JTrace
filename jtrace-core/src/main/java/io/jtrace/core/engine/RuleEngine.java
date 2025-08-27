package io.jtrace.core.engine;

import io.jtrace.core.analysis.*;
import io.jtrace.core.config.JTraceConfig;
import io.jtrace.core.importer.ProjectModel;
import io.jtrace.core.importer.SourceImporter;
import io.jtrace.core.model.*;
import io.jtrace.core.match.PatternMatcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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
        List<Violation> violations = new ArrayList<>();
        
        // Find all classes matching the package pattern
        List<ProjectModel.ClassInfo> matchingClasses = projectModel.getAllClasses().stream()
            .filter(classInfo -> patternMatcher.matches(rule.getPackagePattern(), classInfo.getFullName()))
            .collect(Collectors.toList());
        
        for (ProjectModel.ClassInfo classInfo : matchingClasses) {
            switch (rule.getTarget()) {
                case CLASS:
                    checkClassVisibility(rule, classInfo, violations);
                    break;
                case METHOD:
                    checkMethodVisibility(rule, classInfo, violations);
                    break;
                case FIELD:
                    checkFieldVisibility(rule, classInfo, violations);
                    break;
            }
        }
        
        return violations;
    }
    
    private void checkClassVisibility(VisibilityRule rule, ProjectModel.ClassInfo classInfo, 
                                    List<Violation> violations) {
        if (classInfo.getVisibility() != mapVisibility(rule.getMustBe())) {
            violations.add(createVisibilityViolation(rule, classInfo, null, null));
        }
    }
    
    private void checkMethodVisibility(VisibilityRule rule, ProjectModel.ClassInfo classInfo, 
                                     List<Violation> violations) {
        for (ProjectModel.MethodInfo method : classInfo.getMethods()) {
            if (method.getVisibility() != mapVisibility(rule.getMustBe())) {
                violations.add(createVisibilityViolation(rule, classInfo, method, null));
            }
        }
    }
    
    private void checkFieldVisibility(VisibilityRule rule, ProjectModel.ClassInfo classInfo, 
                                    List<Violation> violations) {
        for (ProjectModel.FieldInfo field : classInfo.getFields()) {
            if (field.getVisibility() != mapVisibility(rule.getMustBe())) {
                violations.add(createVisibilityViolation(rule, classInfo, null, field));
            }
        }
    }
    
    private ProjectModel.Visibility mapVisibility(VisibilityRule.Visibility visibility) {
        switch (visibility) {
            case PUBLIC: return ProjectModel.Visibility.PUBLIC;
            case PROTECTED: return ProjectModel.Visibility.PROTECTED;
            case PRIVATE: return ProjectModel.Visibility.PRIVATE;
            case PACKAGE_PRIVATE: return ProjectModel.Visibility.PACKAGE_PRIVATE;
            default: return ProjectModel.Visibility.PACKAGE_PRIVATE;
        }
    }
    
    private Violation createVisibilityViolation(VisibilityRule rule, ProjectModel.ClassInfo classInfo, 
                                               ProjectModel.MethodInfo method, ProjectModel.FieldInfo field) {
        Location location = new Location(
            java.nio.file.Path.of(classInfo.getSourceFile()),
            1, // Default line number
            formatElementName(classInfo, method, field)
        );
        
        String elementName = formatElementName(classInfo, method, field);
        String message = String.format("Visibility violation: %s must be %s, but is %s. %s", 
                                     elementName, rule.getMustBe().toString().toLowerCase(),
                                     getCurrentVisibility(classInfo, method, field).toString().toLowerCase(),
                                     rule.getMessage());
        
        return new Violation(
            rule.getId(),
            message,
            rule.getSeverity(),
            location
        );
    }
    
    private String formatElementName(ProjectModel.ClassInfo classInfo, ProjectModel.MethodInfo method, 
                                   ProjectModel.FieldInfo field) {
        if (method != null) {
            return classInfo.getFullName() + "#" + method.getName();
        } else if (field != null) {
            return classInfo.getFullName() + "." + field.getName();
        } else {
            return classInfo.getFullName();
        }
    }
    
    private ProjectModel.Visibility getCurrentVisibility(ProjectModel.ClassInfo classInfo, 
                                                       ProjectModel.MethodInfo method, 
                                                       ProjectModel.FieldInfo field) {
        if (method != null) {
            return method.getVisibility();
        } else if (field != null) {
            return field.getVisibility();
        } else {
            return classInfo.getVisibility();
        }
    }

    /**
     * Shuts down the rule engine and releases resources.
     */
    public void shutdown() {
        executor.shutdown();
    }
}
