package io.jtrace.core.importer;

import java.util.*;

/**
 * Represents the complete model of a Java project after parsing source code.
 */
public class ProjectModel {
    private final Map<String, PackageInfo> packages;
    private final Map<String, ClassInfo> classes;
    private final Map<String, MethodInfo> methods;
    private final List<Dependency> dependencies;

    public ProjectModel() {
        this.packages = new HashMap<>();
        this.classes = new HashMap<>();
        this.methods = new HashMap<>();
        this.dependencies = new ArrayList<>();
    }

    public void addPackage(PackageInfo packageInfo) {
        packages.put(packageInfo.getName(), packageInfo);
    }

    public void addClass(ClassInfo classInfo) {
        classes.put(classInfo.getFullName(), classInfo);
    }

    public void addMethod(MethodInfo methodInfo) {
        methods.put(methodInfo.getSignature(), methodInfo);
    }

    public void addDependency(Dependency dependency) {
        dependencies.add(dependency);
    }

    public Collection<PackageInfo> getPackages() {
        return packages.values();
    }

    public Collection<ClassInfo> getAllClasses() {
        return classes.values();
    }

    public Collection<MethodInfo> getMethods() {
        return methods.values();
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public PackageInfo getPackage(String name) {
        return packages.get(name);
    }

    public ClassInfo getClass(String fullyQualifiedName) {
        return classes.get(fullyQualifiedName);
    }

    public MethodInfo getMethod(String signature) {
        return methods.get(signature);
    }

    public static class PackageInfo {
        private final String name;
        private final Set<ClassInfo> classes;
        private final Set<String> imports;

        public PackageInfo(String name) {
            this.name = name;
            this.classes = new HashSet<>();
            this.imports = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public Set<ClassInfo> getClasses() {
            return classes;
        }

        public Set<String> getImports() {
            return imports;
        }

        public void addClass(ClassInfo classInfo) {
            classes.add(classInfo);
        }

        public void addImports(Set<String> imports) {
            this.imports.addAll(imports);
        }
    }

    public static class ClassInfo {
        private final String name;
        private final String fullName;
        private final String packageName;
        private final Visibility visibility;
        private final ClassType classType;
        private final Set<String> annotations;
        private final Set<MethodInfo> methods;
        private final Set<FieldInfo> fields;
        private String sourceFile;
        private String enclosingClass;

        public ClassInfo(String name, String fullName, Visibility visibility, ClassType classType) {
            this.name = name;
            this.fullName = fullName;
            this.packageName = fullName.substring(0, fullName.lastIndexOf('.'));
            this.visibility = visibility;
            this.classType = classType;
            this.annotations = new HashSet<>();
            this.methods = new HashSet<>();
            this.fields = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public String getFullName() {
            return fullName;
        }

        public String getPackageName() {
            return packageName;
        }

        public Visibility getVisibility() {
            return visibility;
        }

        public ClassType getClassType() {
            return classType;
        }

        public Set<String> getAnnotations() {
            return annotations;
        }

        public Set<MethodInfo> getMethods() {
            return methods;
        }

        public Set<FieldInfo> getFields() {
            return fields;
        }

        public String getSourceFile() {
            return sourceFile;
        }

        public void setSourceFile(String sourceFile) {
            this.sourceFile = sourceFile;
        }

        public String getEnclosingClass() {
            return enclosingClass;
        }

        public void setEnclosingClass(String enclosingClass) {
            this.enclosingClass = enclosingClass;
        }

        public void addAnnotation(String annotation) {
            annotations.add(annotation);
        }

        public void addMethod(MethodInfo method) {
            methods.add(method);
        }

        public void addField(FieldInfo field) {
            fields.add(field);
        }

        public void setAnnotations(List<String> annotations) {
            this.annotations.clear();
            this.annotations.addAll(annotations);
        }
    }

    public static class MethodInfo {
        private final String name;
        private final String returnType;
        private final Visibility visibility;
        private final List<ParameterInfo> parameters;
        private final List<String> annotations;

        public MethodInfo(String name, String returnType, Visibility visibility, 
                         List<ParameterInfo> parameters, List<String> annotations) {
            this.name = name;
            this.returnType = returnType;
            this.visibility = visibility;
            this.parameters = parameters;
            this.annotations = annotations;
        }

        public String getName() {
            return name;
        }

        public String getReturnType() {
            return returnType;
        }

        public Visibility getVisibility() {
            return visibility;
        }

        public List<ParameterInfo> getParameters() {
            return parameters;
        }

        public List<String> getAnnotations() {
            return annotations;
        }

        public String getSignature() {
            return name + "(" + parameters.stream()
                .map(ParameterInfo::getType)
                .reduce("", (a, b) -> a + (a.isEmpty() ? "" : ", ") + b) + ")";
        }
    }

    public static class FieldInfo {
        private final String name;
        private final String type;
        private final Visibility visibility;
        private final List<String> annotations;

        public FieldInfo(String name, String type, Visibility visibility, List<String> annotations) {
            this.name = name;
            this.type = type;
            this.visibility = visibility;
            this.annotations = annotations;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Visibility getVisibility() {
            return visibility;
        }

        public List<String> getAnnotations() {
            return annotations;
        }
    }

    public static class ParameterInfo {
        private final String name;
        private final String type;

        public ParameterInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }

    public static class Dependency {
        private final String from;
        private final String to;
        private final DependencyType type;
        private final Location location;

        public Dependency(String from, String to, DependencyType type, Location location) {
            this.from = from;
            this.to = to;
            this.type = type;
            this.location = location;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public DependencyType getType() {
            return type;
        }

        public Location getLocation() {
            return location;
        }
    }

    public enum DependencyType {
        IMPORT, EXTENDS, IMPLEMENTS, FIELD, METHOD_CALL, CONSTRUCTOR_CALL
    }

    public enum Visibility {
        PUBLIC, PROTECTED, PACKAGE_PRIVATE, PRIVATE
    }

    public enum ClassType {
        CLASS, INTERFACE, ENUM, ANNOTATION
    }

    public static class Location {
        private final String file;
        private final int line;
        private final int column;

        public Location(String file, int line, int column) {
            this.file = file;
            this.line = line;
            this.column = column;
        }

        public String getFile() {
            return file;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }
    }
}
