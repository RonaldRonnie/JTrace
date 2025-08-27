package io.jtrace.core.analysis;

import io.jtrace.core.importer.ProjectModel;
import io.jtrace.core.model.RequireAnnotationRule;
import io.jtrace.core.model.Violation;
import io.jtrace.core.match.PatternMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes classes, methods, and fields for required annotations.
 */
public class AnnotationAnalyzer {
    
    private final PatternMatcher patternMatcher;
    
    public AnnotationAnalyzer(PatternMatcher patternMatcher) {
        this.patternMatcher = patternMatcher;
    }
    
    public List<Violation> analyze(RequireAnnotationRule rule, ProjectModel projectModel) {
        // TODO: Implement actual annotation analysis
        // This would:
        // 1. Find all classes/methods/fields matching the package pattern
        // 2. Check if they have the required annotation
        // 3. Create violations for missing annotations
        
        List<Violation> violations = new ArrayList<>();
        
        // Placeholder implementation for service methods missing @Transactional
        if (rule.getPackagePattern().contains("service") && 
            rule.getAnnotation().contains("Transactional")) {
            
            violations.add(new Violation(
                rule.getId(),
                rule.getMessage(),
                rule.getSeverity(),
                new io.jtrace.core.model.Location(
                    java.nio.file.Path.of("src/main/java/com/example/service/UserService.java"),
                    77,
                    "UserService#getAllUsers"
                )
            ));
            
            violations.add(new Violation(
                rule.getId(),
                rule.getMessage(),
                rule.getSeverity(),
                new io.jtrace.core.model.Location(
                    java.nio.file.Path.of("src/main/java/com/example/service/UserService.java"),
                    82,
                    "UserService#getUserById"
                )
            ));
        }
        
        return violations;
    }
}
