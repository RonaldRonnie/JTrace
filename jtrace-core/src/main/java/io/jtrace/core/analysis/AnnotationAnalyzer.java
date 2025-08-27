package io.jtrace.core.analysis;

import io.jtrace.core.importer.ProjectModel;
import io.jtrace.core.model.RequireAnnotationRule;
import io.jtrace.core.model.Violation;
import io.jtrace.core.model.Location;
import io.jtrace.core.match.PatternMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Analyzes classes, methods, and fields for required annotations.
 */
public class AnnotationAnalyzer {
    
    private final PatternMatcher patternMatcher;
    
    public AnnotationAnalyzer(PatternMatcher patternMatcher) {
        this.patternMatcher = patternMatcher;
    }
    
    public List<Violation> analyze(RequireAnnotationRule rule, ProjectModel projectModel) {
        List<Violation> violations = new ArrayList<>();
        
        // Find all classes matching the package pattern
        List<ProjectModel.ClassInfo> matchingClasses = findClassesMatchingPattern(
            rule.getPackagePattern(), projectModel);
        
        for (ProjectModel.ClassInfo classInfo : matchingClasses) {
            switch (rule.getTarget()) {
                case CLASS:
                    checkClassAnnotations(rule, classInfo, violations);
                    break;
                case METHOD:
                    checkMethodAnnotations(rule, classInfo, violations);
                    break;
                case FIELD:
                    checkFieldAnnotations(rule, classInfo, violations);
                    break;
            }
        }
        
        return violations;
    }
    
    private List<ProjectModel.ClassInfo> findClassesMatchingPattern(String pattern, ProjectModel projectModel) {
        return projectModel.getAllClasses().stream()
            .filter(classInfo -> patternMatcher.matches(pattern, classInfo.getFullName()))
            .collect(Collectors.toList());
    }
    
    private void checkClassAnnotations(RequireAnnotationRule rule, ProjectModel.ClassInfo classInfo, 
                                     List<Violation> violations) {
        if (!hasAnnotation(new ArrayList<>(classInfo.getAnnotations()), rule.getAnnotation())) {
            violations.add(createViolation(rule, classInfo, null, null));
        }
    }
    
    private void checkMethodAnnotations(RequireAnnotationRule rule, ProjectModel.ClassInfo classInfo, 
                                      List<Violation> violations) {
        for (ProjectModel.MethodInfo method : classInfo.getMethods()) {
            if (!hasAnnotation(new ArrayList<>(method.getAnnotations()), rule.getAnnotation())) {
                violations.add(createViolation(rule, classInfo, method, null));
            }
        }
    }
    
    private void checkFieldAnnotations(RequireAnnotationRule rule, ProjectModel.ClassInfo classInfo, 
                                     List<Violation> violations) {
        for (ProjectModel.FieldInfo field : classInfo.getFields()) {
            if (!hasAnnotation(new ArrayList<>(field.getAnnotations()), rule.getAnnotation())) {
                violations.add(createViolation(rule, classInfo, null, field));
            }
        }
    }
    
    private boolean hasAnnotation(List<String> annotations, String requiredAnnotation) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        
        // Check for exact match or simple class name match
        return annotations.stream().anyMatch(annotation -> 
            annotation.equals(requiredAnnotation) || 
            annotation.endsWith("." + requiredAnnotation) ||
            annotation.equals("@" + requiredAnnotation) ||
            annotation.endsWith("." + requiredAnnotation)
        );
    }
    
    private Violation createViolation(RequireAnnotationRule rule, ProjectModel.ClassInfo classInfo, 
                                     ProjectModel.MethodInfo method, ProjectModel.FieldInfo field) {
        Location location = new Location(
            classInfo.getSourceFile(),
            1, // Default line number
            formatElementName(classInfo, method, field)
        );
        
        String elementName = formatElementName(classInfo, method, field);
        String message = String.format("Missing required annotation '%s' on %s: %s", 
                                     rule.getAnnotation(), rule.getTarget().toString().toLowerCase(), 
                                     elementName);
        
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
}
