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
        classes.put(classInfo.getFullyQualifiedName(), classInfo);
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

    public Collection<ClassInfo> getClasses() {
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

        public PackageInfo(String name) {
            this.name = name;
            this.classes = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public Set<ClassInfo> getClasses() {
            return classes;
        }

        public void addClass(ClassInfo classInfo) {
            classes.add(classInfo);
        }
    }

    public static class ClassInfo {
        private final String name;
        private final String packageName;
        private final String fullyQualifiedName;
        private final Set<String> annotations;
        private final Set<MethodInfo> methods;
        private final Set<FieldInfo> fields;
        private final Visibility visibility;

        public ClassInfo(String name, String packageName, Visibility visibility) {
            this.name = name;
            this.packageName = packageName;
            this.fullyQualifiedName = packageName + "." + name;
            this.visibility = visibility;
            this.annotations = new HashSet<>();
            this.methods = new HashSet<>();
            this.fields = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getFullyQualifiedName() {
            return fullyQualifiedName;
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

        public Visibility getVisibility() {
            return visibility;
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
    }

    public static class MethodInfo {
        private final String name;
        private final String signature;
        private final ClassInfo declaringClass;
        private final Set<String> annotations;
        private final Visibility visibility;

        public MethodInfo(String name, String signature, ClassInfo declaringClass, Visibility visibility) {
            this.name = name;
            this.signature = signature;
            this.declaringClass = declaringClass;
            this.visibility = visibility;
            this.annotations = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public String getSignature() {
            return signature;
        }

        public ClassInfo getDeclaringClass() {
            return declaringClass;
        }

        public Set<String> getAnnotations() {
            return annotations;
        }

        public Visibility getVisibility() {
            return visibility;
        }

        public void addAnnotation(String annotation) {
            annotations.add(annotation);
        }
    }

    public static class FieldInfo {
        private final String name;
        private final ClassInfo declaringClass;
        private final Set<String> annotations;
        private final Visibility visibility;

        public FieldInfo(String name, ClassInfo declaringClass, Visibility visibility) {
            this.name = name;
            this.declaringClass = declaringClass;
            this.visibility = visibility;
            this.annotations = new HashSet<>();
        }

        public String getName() {
            return name;
        }

        public ClassInfo getDeclaringClass() {
            return declaringClass;
        }

        public Set<String> getAnnotations() {
            return annotations;
        }

        public Visibility getVisibility() {
            return visibility;
        }

        public void addAnnotation(String annotation) {
            annotations.add(annotation);
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
