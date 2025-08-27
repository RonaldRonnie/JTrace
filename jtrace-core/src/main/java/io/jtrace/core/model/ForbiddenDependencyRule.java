package io.jtrace.core.model;

/**
 * Rule that forbids dependencies from one package pattern to another.
 */
public class ForbiddenDependencyRule extends AbstractRule {
    private final String fromPattern;
    private final String toPattern;

    public ForbiddenDependencyRule(String id, Severity severity, String message, 
                                 String fromPattern, String toPattern) {
        super(id, "forbiddenDependency", severity, message);
        this.fromPattern = fromPattern;
        this.toPattern = toPattern;
    }

    public String getFromPattern() {
        return fromPattern;
    }

    public String getToPattern() {
        return toPattern;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Severity severity = Severity.ERROR;
        private String message;
        private String fromPattern;
        private String toPattern;

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

        public Builder from(String fromPattern) {
            this.fromPattern = fromPattern;
            return this;
        }

        public Builder to(String toPattern) {
            this.toPattern = toPattern;
            return this;
        }

        public ForbiddenDependencyRule build() {
            if (id == null || fromPattern == null || toPattern == null) {
                throw new IllegalStateException("id, fromPattern, and toPattern are required");
            }
            if (message == null) {
                message = String.format("Dependency from %s to %s is forbidden", fromPattern, toPattern);
            }
            return new ForbiddenDependencyRule(id, severity, message, fromPattern, toPattern);
        }
    }
}
