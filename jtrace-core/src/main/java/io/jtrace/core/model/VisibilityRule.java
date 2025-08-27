package io.jtrace.core.model;

/**
 * Rule that enforces visibility constraints on classes, methods, or fields.
 */
public class VisibilityRule extends AbstractRule {
    private final String packagePattern;
    private final Target target;
    private final Visibility mustBe;

    public enum Target {
        CLASS, METHOD, FIELD
    }

    public enum Visibility {
        PUBLIC("public"),
        PROTECTED("protected"),
        PACKAGE_PRIVATE("package-private"),
        PRIVATE("private");

        private final String value;

        Visibility(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Visibility fromString(String value) {
            for (Visibility visibility : values()) {
                if (visibility.value.equalsIgnoreCase(value)) {
                    return visibility;
                }
            }
            throw new IllegalArgumentException("Unknown visibility: " + value);
        }
    }

    public VisibilityRule(String id, Severity severity, String message,
                         String packagePattern, Target target, Visibility mustBe) {
        super(id, "visibility", severity, message);
        this.packagePattern = packagePattern;
        this.target = target;
        this.mustBe = mustBe;
    }

    public String getPackagePattern() {
        return packagePattern;
    }

    public Target getTarget() {
        return target;
    }

    public Visibility getMustBe() {
        return mustBe;
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
        private Visibility mustBe;

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

        public Builder mustBe(Visibility mustBe) {
            this.mustBe = mustBe;
            return this;
        }

        public VisibilityRule build() {
            if (id == null || packagePattern == null || target == null || mustBe == null) {
                throw new IllegalStateException("id, packagePattern, target, and mustBe are required");
            }
            if (message == null) {
                message = String.format("%s in %s must be %s", 
                    target.name().toLowerCase(), packagePattern, mustBe.getValue());
            }
            return new VisibilityRule(id, severity, message, packagePattern, target, mustBe);
        }
    }
}
