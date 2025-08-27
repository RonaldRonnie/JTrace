package io.jtrace.core.config;

import io.jtrace.core.model.*;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads JTrace configuration from YAML files.
 */
public class JTraceConfigLoader {
    
    /**
     * Loads configuration from a YAML file.
     */
    public JTraceConfig loadFromFile(Path configFile) throws IOException {
        try (InputStream input = Files.newInputStream(configFile)) {
            return loadFromInputStream(input);
        }
    }

    /**
     * Loads configuration from an input stream.
     */
    public JTraceConfig loadFromInputStream(InputStream input) {
        Yaml yaml = new Yaml(new Constructor(Map.class));
        Map<String, Object> data = yaml.load(input);
        
        return parseConfig(data);
    }

    @SuppressWarnings("unchecked")
    private JTraceConfig parseConfig(Map<String, Object> data) {
        String version = (String) data.get("version");
        String basePackage = (String) data.get("basePackage");
        
        Map<String, Object> failOnData = (Map<String, Object>) data.get("failOn");
        FailOn failOn = parseFailOn(failOnData);
        
        List<Map<String, Object>> rulesData = (List<Map<String, Object>>) data.get("rules");
        List<Rule> rules = parseRules(rulesData);
        
        return JTraceConfig.builder()
                .version(version != null ? version : "1")
                .basePackage(basePackage)
                .failOn(failOn)
                .rules(rules)
                .build();
    }

    private FailOn parseFailOn(Map<String, Object> failOnData) {
        if (failOnData == null) {
            return new FailOn(Severity.ERROR);
        }
        
        String severity = (String) failOnData.get("severity");
        if (severity == null) {
            return new FailOn(Severity.ERROR);
        }
        
        return FailOn.fromString(severity);
    }

    @SuppressWarnings("unchecked")
    private List<Rule> parseRules(List<Map<String, Object>> rulesData) {
        List<Rule> rules = new ArrayList<>();
        
        if (rulesData == null) {
            return rules;
        }
        
        for (Map<String, Object> ruleData : rulesData) {
            Rule rule = parseRule(ruleData);
            if (rule != null) {
                rules.add(rule);
            }
        }
        
        return rules;
    }

    @SuppressWarnings("unchecked")
    private Rule parseRule(Map<String, Object> ruleData) {
        String id = (String) ruleData.get("id");
        String type = (String) ruleData.get("type");
        String severityStr = (String) ruleData.get("severity");
        String message = (String) ruleData.get("message");
        
        Severity severity = severityStr != null ? Severity.fromString(severityStr) : Severity.ERROR;
        
        switch (type) {
            case "forbiddenDependency":
                return parseForbiddenDependencyRule(id, severity, message, ruleData);
            case "requireAnnotation":
                return parseRequireAnnotationRule(id, severity, message, ruleData);
            case "layering":
                return parseLayeringRule(id, severity, message, ruleData);
            case "visibility":
                return parseVisibilityRule(id, severity, message, ruleData);
            default:
                throw new IllegalArgumentException("Unknown rule type: " + type);
        }
    }

    private Rule parseForbiddenDependencyRule(String id, Severity severity, String message, 
                                            Map<String, Object> ruleData) {
        String from = (String) ruleData.get("from");
        String to = (String) ruleData.get("to");
        
        return ForbiddenDependencyRule.builder()
                .id(id)
                .severity(severity)
                .message(message)
                .from(from)
                .to(to)
                .build();
    }

    private Rule parseRequireAnnotationRule(String id, Severity severity, String message,
                                         Map<String, Object> ruleData) {
        String in = (String) ruleData.get("in");
        String targetStr = (String) ruleData.get("target");
        String annotation = (String) ruleData.get("annotation");
        
        RequireAnnotationRule.Target target = RequireAnnotationRule.Target.valueOf(
            targetStr != null ? targetStr.toUpperCase() : "METHOD");
        
        return RequireAnnotationRule.builder()
                .id(id)
                .severity(severity)
                .message(message)
                .in(in)
                .target(target)
                .annotation(annotation)
                .build();
    }

    @SuppressWarnings("unchecked")
    private Rule parseLayeringRule(String id, Severity severity, String message,
                                 Map<String, Object> ruleData) {
        List<Map<String, Object>> layersData = (List<Map<String, Object>>) ruleData.get("layers");
        List<Map<String, Object>> allowedDepsData = (List<Map<String, Object>>) ruleData.get("allowedDependencies");
        Boolean forbidCycles = (Boolean) ruleData.get("forbidCycles");
        
        List<LayeringRule.Layer> layers = parseLayers(layersData);
        List<LayeringRule.AllowedDependency> allowedDeps = parseAllowedDependencies(allowedDepsData);
        
        return LayeringRule.builder()
                .id(id)
                .severity(severity)
                .message(message)
                .layers(layers)
                .allowedDependencies(allowedDeps)
                .forbidCycles(forbidCycles != null ? forbidCycles : true)
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<LayeringRule.Layer> parseLayers(List<Map<String, Object>> layersData) {
        List<LayeringRule.Layer> layers = new ArrayList<>();
        
        if (layersData == null) {
            return layers;
        }
        
        for (Map<String, Object> layerData : layersData) {
            String name = (String) layerData.get("name");
            List<String> packages = (List<String>) layerData.get("packages");
            
            layers.add(new LayeringRule.Layer(name, packages));
        }
        
        return layers;
    }

    @SuppressWarnings("unchecked")
    private List<LayeringRule.AllowedDependency> parseAllowedDependencies(List<Map<String, Object>> depsData) {
        List<LayeringRule.AllowedDependency> deps = new ArrayList<>();
        
        if (depsData == null) {
            return deps;
        }
        
        for (Map<String, Object> depData : depsData) {
            String from = (String) depData.get("from");
            String to = (String) depData.get("to");
            
            deps.add(new LayeringRule.AllowedDependency(from, to));
        }
        
        return deps;
    }

    private Rule parseVisibilityRule(String id, Severity severity, String message,
                                   Map<String, Object> ruleData) {
        String in = (String) ruleData.get("in");
        String targetStr = (String) ruleData.get("target");
        String mustBeStr = (String) ruleData.get("mustBe");
        
        VisibilityRule.Target target = VisibilityRule.Target.valueOf(
            targetStr != null ? targetStr.toUpperCase() : "CLASS");
        VisibilityRule.Visibility mustBe = VisibilityRule.Visibility.fromString(mustBeStr);
        
        return VisibilityRule.builder()
                .id(id)
                .severity(severity)
                .message(message)
                .in(in)
                .target(target)
                .mustBe(mustBe)
                .build();
    }
}
