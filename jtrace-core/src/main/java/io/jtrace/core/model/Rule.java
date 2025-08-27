package io.jtrace.core.model;

/**
 * Base interface for all architecture rules.
 */
public interface Rule {
    String getId();
    String getType();
    Severity getSeverity();
    String getMessage();
}

/**
 * Abstract base class for rule implementations.
 */
abstract class AbstractRule implements Rule {
    protected final String id;
    protected final String type;
    protected final Severity severity;
    protected final String message;

    protected AbstractRule(String id, String type, Severity severity, String message) {
        this.id = id;
        this.type = type;
        this.severity = severity;
        this.message = message;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
