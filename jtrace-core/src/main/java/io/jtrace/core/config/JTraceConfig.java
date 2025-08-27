package io.jtrace.core.config;

import io.jtrace.core.model.Rule;
import io.jtrace.core.model.Severity;
import java.util.List;

/**
 * Main configuration class for JTrace.
 */
public class JTraceConfig {
    private final String version;
    private final String basePackage;
    private final FailOn failOn;
    private final List<Rule> rules;

    public JTraceConfig(String version, String basePackage, FailOn failOn, List<Rule> rules) {
        this.version = version;
        this.basePackage = basePackage;
        this.failOn = failOn;
        this.rules = rules;
    }

    public String getVersion() {
        return version;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public FailOn getFailOn() {
        return failOn;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public static class FailOn {
        private final Severity severity;

        public FailOn(Severity severity) {
            this.severity = severity;
        }

        public Severity getSeverity() {
            return severity;
        }

        public boolean shouldFail(List<io.jtrace.core.model.Violation> violations) {
            return violations.stream()
                .anyMatch(violation -> violation.getSeverity().ordinal() <= severity.ordinal());
        }

        public static FailOn fromString(String severity) {
            return new FailOn(Severity.fromString(severity));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String version = "1";
        private String basePackage;
        private FailOn failOn = new FailOn(Severity.ERROR);
        private List<Rule> rules;

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder basePackage(String basePackage) {
            this.basePackage = basePackage;
            return this;
        }

        public Builder failOn(FailOn failOn) {
            this.failOn = failOn;
            return this;
        }

        public Builder failOn(String severity) {
            this.failOn = FailOn.fromString(severity);
            return this;
        }

        public Builder rules(List<Rule> rules) {
            this.rules = rules;
            return this;
        }

        public JTraceConfig build() {
            if (basePackage == null) {
                throw new IllegalStateException("basePackage is required");
            }
            if (rules == null) {
                throw new IllegalStateException("rules are required");
            }
            return new JTraceConfig(version, basePackage, failOn, rules);
        }
    }
}
