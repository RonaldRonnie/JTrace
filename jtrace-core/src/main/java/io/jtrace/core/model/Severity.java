package io.jtrace.core.model;

/**
 * Represents the severity level of a rule violation.
 */
public enum Severity {
    ERROR("error"),
    WARNING("warning"),
    INFO("info");

    private final String value;

    Severity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Severity fromString(String value) {
        for (Severity severity : values()) {
            if (severity.value.equalsIgnoreCase(value)) {
                return severity;
            }
        }
        throw new IllegalArgumentException("Unknown severity: " + value);
    }

    public boolean isAtLeast(Severity other) {
        return this.ordinal() <= other.ordinal();
    }
}
