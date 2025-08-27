package io.jtrace.core.model;

import java.util.List;
import java.util.Map;

/**
 * Rule that enforces architectural layering constraints.
 */
public class LayeringRule extends AbstractRule {
    private final List<Layer> layers;
    private final List<AllowedDependency> allowedDependencies;
    private final boolean forbidCycles;

    public static class Layer {
        private final String name;
        private final List<String> packages;

        public Layer(String name, List<String> packages) {
            this.name = name;
            this.packages = packages;
        }

        public String getName() {
            return name;
        }

        public List<String> getPackages() {
            return packages;
        }
    }

    public static class AllowedDependency {
        private final String from;
        private final String to;

        public AllowedDependency(String from, String to) {
            this.from = from;
            this.to = to;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }
    }

    public LayeringRule(String id, Severity severity, String message,
                       List<Layer> layers, List<AllowedDependency> allowedDependencies, 
                       boolean forbidCycles) {
        super(id, "layering", severity, message);
        this.layers = layers;
        this.allowedDependencies = allowedDependencies;
        this.forbidCycles = forbidCycles;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public List<AllowedDependency> getAllowedDependencies() {
        return allowedDependencies;
    }

    public boolean isForbidCycles() {
        return forbidCycles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private Severity severity = Severity.ERROR;
        private String message;
        private List<Layer> layers;
        private List<AllowedDependency> allowedDependencies;
        private boolean forbidCycles = true;

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

        public Builder layers(List<Layer> layers) {
            this.layers = layers;
            return this;
        }

        public Builder allowedDependencies(List<AllowedDependency> allowedDependencies) {
            this.allowedDependencies = allowedDependencies;
            return this;
        }

        public Builder forbidCycles(boolean forbidCycles) {
            this.forbidCycles = forbidCycles;
            return this;
        }

        public LayeringRule build() {
            if (id == null || layers == null || allowedDependencies == null) {
                throw new IllegalStateException("id, layers, and allowedDependencies are required");
            }
            if (message == null) {
                message = String.format("Layering rule with %d layers and %d allowed dependencies", 
                    layers.size(), allowedDependencies.size());
            }
            return new LayeringRule(id, severity, message, layers, allowedDependencies, forbidCycles);
        }
    }
}
