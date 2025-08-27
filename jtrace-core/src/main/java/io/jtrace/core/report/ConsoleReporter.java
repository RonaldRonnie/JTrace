package io.jtrace.core.report;

import io.jtrace.core.model.Violation;
import io.jtrace.core.model.Severity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Reports violations to the console with formatted output.
 */
public class ConsoleReporter {
    
    public void report(List<Violation> violations) {
        if (violations.isEmpty()) {
            System.out.println("✅ No architecture violations found");
            return;
        }

        // Group violations by severity
        Map<Severity, List<Violation>> violationsBySeverity = violations.stream()
            .collect(Collectors.groupingBy(Violation::getSeverity));

        // Print summary
        System.out.println("🔍 Architecture Analysis Results");
        System.out.println("=================================");
        System.out.printf("Total violations: %d%n", violations.size());
        
        for (Severity severity : Severity.values()) {
            List<Violation> severityViolations = violationsBySeverity.get(severity);
            if (severityViolations != null) {
                System.out.printf("%s: %d%n", 
                    severity.getValue().toUpperCase(), severityViolations.size());
            }
        }
        System.out.println();

        // Print violations grouped by severity
        for (Severity severity : Severity.values()) {
            List<Violation> severityViolations = violationsBySeverity.get(severity);
            if (severityViolations != null && !severityViolations.isEmpty()) {
                printViolationsBySeverity(severity, severityViolations);
            }
        }

        // Print suggestions
        printSuggestions(violations);
    }

    private void printViolationsBySeverity(Severity severity, List<Violation> violations) {
        String severityLabel = severity.getValue().toUpperCase();
        System.out.printf("[%s] %s violations:%n", severityLabel, violations.size());
        
        for (Violation violation : violations) {
            System.out.printf("  • %s: %s at %s%n",
                violation.getRuleId(),
                violation.getMessage(),
                violation.getLocation());
        }
        System.out.println();
    }

    private void printSuggestions(List<Violation> violations) {
        System.out.println("💡 Suggestions:");
        System.out.println("===============");
        
        // Group by rule ID to avoid duplicate suggestions
        Map<String, Long> ruleCounts = violations.stream()
            .collect(Collectors.groupingBy(Violation::getRuleId, Collectors.counting()));
        
        ruleCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5) // Top 5 most violated rules
            .forEach(entry -> {
                System.out.printf("  • Rule '%s' was violated %d times%n", 
                    entry.getKey(), entry.getValue());
            });
        
        System.out.println();
        System.out.println("Run 'jtrace report --format=html' for detailed analysis");
    }
}
