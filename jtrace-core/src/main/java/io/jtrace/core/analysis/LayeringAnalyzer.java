package io.jtrace.core.analysis;

import io.jtrace.core.importer.ProjectModel;
import io.jtrace.core.model.LayeringRule;
import io.jtrace.core.model.Violation;
import io.jtrace.core.model.Location;
import io.jtrace.core.match.PatternMatcher;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Analyzes architectural layering constraints.
 */
public class LayeringAnalyzer {
    
    private final PatternMatcher patternMatcher;
    
    public LayeringAnalyzer(PatternMatcher patternMatcher) {
        this.patternMatcher = patternMatcher;
    }
    
    public List<Violation> analyze(LayeringRule rule, ProjectModel projectModel) {
        List<Violation> violations = new ArrayList<>();
        
        // Map packages to layers
        Map<String, String> packageToLayer = buildPackageToLayerMap(rule);
        
        // Check allowed dependencies between layers
        violations.addAll(checkLayerDependencies(rule, projectModel, packageToLayer));
        
        // Check for cycles if forbidden
        if (rule.isForbidCycles()) {
            violations.addAll(checkForCycles(rule, projectModel, packageToLayer));
        }
        
        return violations;
    }
    
    private Map<String, String> buildPackageToLayerMap(LayeringRule rule) {
        Map<String, String> packageToLayer = new HashMap<>();
        
        for (LayeringRule.Layer layer : rule.getLayers()) {
            for (String packagePattern : layer.getPackages()) {
                // For now, we'll use the pattern as the key
                // In a more sophisticated implementation, we'd resolve all matching packages
                packageToLayer.put(packagePattern, layer.getName());
            }
        }
        
        return packageToLayer;
    }
    
    private List<Violation> checkLayerDependencies(LayeringRule rule, ProjectModel projectModel, 
                                                  Map<String, String> packageToLayer) {
        List<Violation> violations = new ArrayList<>();
        
        // Build allowed dependency matrix
        Set<String> allowedDeps = rule.getAllowedDependencies().stream()
            .map(dep -> dep.getFrom() + "->" + dep.getTo())
            .collect(Collectors.toSet());
        
        // Check all class dependencies
        for (ProjectModel.ClassInfo fromClass : projectModel.getAllClasses()) {
            String fromLayer = findLayerForClass(fromClass, packageToLayer);
            if (fromLayer == null) continue;
            
            // Check dependencies from this class
            for (ProjectModel.ClassInfo toClass : projectModel.getAllClasses()) {
                if (fromClass.equals(toClass)) continue;
                
                if (hasDependency(fromClass, toClass, projectModel)) {
                    String toLayer = findLayerForClass(toClass, packageToLayer);
                    if (toLayer == null) continue;
                    
                    // Check if this dependency is allowed
                    String dependencyKey = fromLayer + "->" + toLayer;
                    if (!allowedDeps.contains(dependencyKey)) {
                        violations.add(createLayeringViolation(rule, fromClass, toClass, fromLayer, toLayer));
                    }
                }
            }
        }
        
        return violations;
    }
    
    private String findLayerForClass(ProjectModel.ClassInfo classInfo, Map<String, String> packageToLayer) {
        String packageName = classInfo.getPackageName();
        
        for (Map.Entry<String, String> entry : packageToLayer.entrySet()) {
            if (patternMatcher.matches(entry.getKey(), packageName)) {
                return entry.getValue();
            }
        }
        
        return null;
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
    
    private List<Violation> checkForCycles(LayeringRule rule, ProjectModel projectModel, 
                                          Map<String, String> packageToLayer) {
        List<Violation> violations = new ArrayList<>();
        
        // Build dependency graph for layers
        Map<String, Set<String>> layerDependencies = new HashMap<>();
        
        for (LayeringRule.AllowedDependency allowedDep : rule.getAllowedDependencies()) {
            layerDependencies.computeIfAbsent(allowedDep.getFrom(), k -> new HashSet<>())
                .add(allowedDep.getTo());
        }
        
        // Check for cycles in layer dependencies
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();
        
        for (String layer : layerDependencies.keySet()) {
            if (!visited.contains(layer)) {
                if (hasCycle(layer, layerDependencies, visited, recursionStack)) {
                    violations.add(createCycleViolation(rule));
                }
            }
        }
        
        return violations;
    }
    
    private boolean hasCycle(String layer, Map<String, Set<String>> layerDependencies, 
                            Set<String> visited, Set<String> recursionStack) {
        visited.add(layer);
        recursionStack.add(layer);
        
        Set<String> dependencies = layerDependencies.get(layer);
        if (dependencies != null) {
            for (String dep : dependencies) {
                if (!visited.contains(dep)) {
                    if (hasCycle(dep, layerDependencies, visited, recursionStack)) {
                        return true;
                    }
                } else if (recursionStack.contains(dep)) {
                    return true;
                }
            }
        }
        
        recursionStack.remove(layer);
        return false;
    }
    
    private Violation createLayeringViolation(LayeringRule rule, ProjectModel.ClassInfo fromClass, 
                                             ProjectModel.ClassInfo toClass, String fromLayer, String toLayer) {
        Location location = new Location(
            java.nio.file.Path.of(fromClass.getSourceFile()),
            1, // Default line number
            fromClass.getFullName()
        );
        
        String message = String.format("Layering violation: %s layer (%s) cannot depend on %s layer (%s). %s", 
                                     fromLayer, fromClass.getFullName(), toLayer, toClass.getFullName(), 
                                     rule.getMessage());
        
        return new Violation(
            rule.getId(),
            message,
            rule.getSeverity(),
            location
        );
    }
    
    private Violation createCycleViolation(LayeringRule rule) {
        Location location = new Location(
            java.nio.file.Path.of(""), // No specific file
            0,
            "Layer dependencies"
        );
        
        return new Violation(
            rule.getId(),
            "Circular dependency detected in layer architecture: " + rule.getMessage(),
            rule.getSeverity(),
            location
        );
    }
}
