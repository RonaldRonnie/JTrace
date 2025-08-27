package io.jtrace.core.analysis;

import io.jtrace.core.importer.ProjectModel;
import java.util.*;

/**
 * Detects dependency cycles in the project using Tarjan's algorithm.
 */
public class CycleDetector {
    
    private static class Node {
        final String className;
        final Set<String> dependencies;
        int index = -1;
        int lowLink = -1;
        boolean onStack = false;
        
        Node(String className, Set<String> dependencies) {
            this.className = className;
            this.dependencies = dependencies;
        }
    }
    
    public List<String> detectCycles(ProjectModel projectModel) {
        List<String> cycles = new ArrayList<>();
        Map<String, Node> nodes = buildDependencyGraph(projectModel);
        
        if (nodes.isEmpty()) {
            return cycles;
        }
        
        int index = 0;
        Stack<Node> stack = new Stack<>();
        
        for (Node node : nodes.values()) {
            if (node.index == -1) {
                index = strongConnect(node, nodes, index, stack, cycles);
            }
        }
        
        return cycles;
    }
    
    private Map<String, Node> buildDependencyGraph(ProjectModel projectModel) {
        Map<String, Node> nodes = new HashMap<>();
        
        // Create nodes for all classes
        for (ProjectModel.ClassInfo classInfo : projectModel.getAllClasses()) {
            Set<String> dependencies = new HashSet<>();
            
            // Add dependencies from imports
            ProjectModel.PackageInfo packageInfo = projectModel.getPackage(classInfo.getPackageName());
            if (packageInfo != null) {
                dependencies.addAll(packageInfo.getImports());
            }
            
            // Add dependencies from field types
            for (ProjectModel.FieldInfo field : classInfo.getFields()) {
                dependencies.add(field.getType());
            }
            
            // Add dependencies from method return types and parameters
            for (ProjectModel.MethodInfo method : classInfo.getMethods()) {
                dependencies.add(method.getReturnType());
                for (ProjectModel.ParameterInfo param : method.getParameters()) {
                    dependencies.add(param.getType());
                }
            }
            
            // Filter to only include dependencies that are classes in our project
            Set<String> filteredDeps = new HashSet<>();
            for (String dep : dependencies) {
                if (isProjectClass(dep, projectModel)) {
                    filteredDeps.add(dep);
                }
            }
            
            nodes.put(classInfo.getFullName(), new Node(classInfo.getFullName(), filteredDeps));
        }
        
        return nodes;
    }
    
    private boolean isProjectClass(String className, ProjectModel projectModel) {
        // Check if this is a class we're analyzing
        return projectModel.getAllClasses().stream()
            .anyMatch(c -> c.getFullName().equals(className) || 
                          className.startsWith(c.getFullName() + "."));
    }
    
    private int strongConnect(Node node, Map<String, Node> nodes, int index, 
                            Stack<Node> stack, List<String> cycles) {
        node.index = index;
        node.lowLink = index;
        index++;
        stack.push(node);
        node.onStack = true;
        
        for (String depName : node.dependencies) {
            Node depNode = nodes.get(depName);
            if (depNode != null) {
                if (depNode.index == -1) {
                    index = strongConnect(depNode, nodes, index, stack, cycles);
                    node.lowLink = Math.min(node.lowLink, depNode.lowLink);
                } else if (depNode.onStack) {
                    node.lowLink = Math.min(node.lowLink, depNode.index);
                }
            }
        }
        
        if (node.lowLink == node.index) {
            List<Node> scc = new ArrayList<>();
            Node w;
            do {
                w = stack.pop();
                w.onStack = false;
                scc.add(w);
            } while (w != node);
            
            // If SCC has more than one node, it's a cycle
            if (scc.size() > 1) {
                cycles.add(formatCycle(scc));
            }
        }
        
        return index;
    }
    
    private String formatCycle(List<Node> cycle) {
        StringBuilder sb = new StringBuilder("Dependency cycle detected: ");
        for (int i = 0; i < cycle.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(cycle.get(i).className);
        }
        sb.append(" -> ").append(cycle.get(0).className);
        return sb.toString();
    }
}
