package io.jtrace.core.model;

/**
 * Represents a rule violation found during analysis.
 */
public class Violation {
    private final String ruleId;
    private final String message;
    private final Severity severity;
    private final Location location;
    private final String suggestion;

    public Violation(String ruleId, String message, Severity severity, Location location) {
        this(ruleId, message, severity, location, null);
    }

    public Violation(String ruleId, String message, Severity severity, Location location, String suggestion) {
        this.ruleId = ruleId;
        this.message = message;
        this.severity = severity;
        this.location = location;
        this.suggestion = suggestion;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Location getLocation() {
        return location;
    }

    public String getSuggestion() {
        return suggestion;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s at %s", 
            severity.getValue().toUpperCase(), ruleId, message, location);
    }
}
