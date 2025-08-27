package io.jtrace.core.model;

/**
 * Rule that requires certain annotations on classes, methods, or fields.
 */
public class RequireAnnotationRule extends AbstractRule {
    private final String packagePattern;
    private final Target target;
    private final String annotation;

    public enum Target {
        CLASS, METHOD, FIELD
    }

    public RequireAnnotationRule(String id, Severity severity, String message,
                               String packagePattern, Target target, String annotation) {
        super(id, "requireAnnotation", severity, message);
        this.packagePattern = packagePattern;
        this.target = target;
        this.annotation = annotation;
    }

    public String getPackagePattern() {
        return packagePattern;
    }

    public Target getTarget() {
        return target;
    }

    public String getAnnotation() {
        return annotation;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Severity severity = Severity.WARNING;
        private String message;
        private String packagePattern;
        private Target target;
        private String annotation;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder severity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder in(String packagePattern) {
            this.packagePattern = packagePattern;
            return this;
        }

        public Builder target(Target target) {
            this.target = target;
            return this;
        }

        public Builder annotation(String annotation) {
            this.annotation = annotation;
            return this;
        }

        public RequireAnnotationRule build() {
            if (id == null || packagePattern == null || target == null || annotation == null) {
                throw new IllegalStateException("id, packagePattern, target, and annotation are required");
            }
            if (message == null) {
                message = String.format("%s in %s must be annotated with @%s", 
                    target.name().toLowerCase(), packagePattern, annotation);
            }
            return new RequireAnnotationRule(id, severity, message, packagePattern, target, annotation);
        }
    }
}
