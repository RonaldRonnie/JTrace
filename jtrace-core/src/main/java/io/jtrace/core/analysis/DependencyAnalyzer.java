package io.jtrace.core.analysis;

import io.jtrace.core.importer.ProjectModel;
import io.jtrace.core.model.ForbiddenDependencyRule;
import io.jtrace.core.model.Violation;
import io.jtrace.core.model.Location;
import io.jtrace.core.match.PatternMatcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analyzes dependencies to detect forbidden relationships.
 */
public class DependencyAnalyzer {
    
    private final PatternMatcher patternMatcher;
    
    public DependencyAnalyzer(PatternMatcher patternMatcher) {
        this.patternMatcher = patternMatcher;
    }
    
    public List<Violation> analyze(ForbiddenDependencyRule rule, ProjectModel projectModel) {
        List<Violation> violations = new ArrayList<>();
        
        // Find all classes matching the 'from' pattern
        List<ProjectModel.ClassInfo> fromClasses = findClassesMatchingPattern(
            rule.getFromPattern(), projectModel);
        
        // Find all classes matching the 'to' pattern
        List<ProjectModel.ClassInfo> toClasses = findClassesMatchingPattern(
            rule.getToPattern(), projectModel);
        
        // Check for dependencies between them
        for (ProjectModel.ClassInfo fromClass : fromClasses) {
            for (ProjectModel.ClassInfo toClass : toClasses) {
                if (hasDependency(fromClass, toClass, projectModel)) {
                    violations.add(createViolation(rule, fromClass, toClass));
                }
            }
        }
        
        return violations;
    }
    
    private List<ProjectModel.ClassInfo> findClassesMatchingPattern(String pattern, ProjectModel projectModel) {
        return projectModel.getAllClasses().stream()
            .filter(classInfo -> patternMatcher.matches(pattern, classInfo.getFullName()))
            .collect(Collectors.toList());
    }
    
    private boolean hasDependency(ProjectModel.ClassInfo fromClass, ProjectModel.ClassInfo toClass, 
                                 ProjectModel projectModel) {
        // Check if fromClass imports toClass
        ProjectModel.PackageInfo fromPackage = projectModel.getPackage(fromClass.getPackageName());
        if (fromPackage != null && fromPackage.getImports().contains(toClass.getFullName())) {
            return true;
        }
        
        // Check if fromClass uses toClass in field types
        for (ProjectModel.FieldInfo field : fromClass.getFields()) {
            if (field.getType().equals(toClass.getFullName()) || 
                field.getType().startsWith(toClass.getFullName() + ".")) {
                return true;
            }
        }
        
        // Check if fromClass uses toClass in method return types or parameters
        for (ProjectModel.MethodInfo method : fromClass.getMethods()) {
            if (method.getReturnType().equals(toClass.getFullName()) || 
                method.getReturnType().startsWith(toClass.getFullName() + ".")) {
                return true;
            }
            
            for (ProjectModel.ParameterInfo param : method.getParameters()) {
                if (param.getType().equals(toClass.getFullName()) || 
                    param.getType().startsWith(toClass.getFullName() + ".")) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private Violation createViolation(ForbiddenDependencyRule rule, ProjectModel.ClassInfo fromClass, 
                                     ProjectModel.ClassInfo toClass) {
        Location location = new Location(
            fromClass.getSourceFile(),
            1, // Default line number
            fromClass.getFullName()
        );
        
        return new Violation(
            rule.getId(),
            String.format("Forbidden dependency from %s to %s: %s", 
                         fromClass.getFullName(), toClass.getFullName(), rule.getMessage()),
            rule.getSeverity(),
            location
        );
    }
}
