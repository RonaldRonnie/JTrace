package io.jtrace.core.match;

/**
 * Matches package names against patterns (glob, regex, etc.).
 */
public class PatternMatcher {
    
    public boolean matches(String pattern, String packageName) {
        // TODO: Implement pattern matching
        // This would support:
        // - Glob patterns (e.g., "com.myapp.*")
        // - Regex patterns
        // - Wildcard patterns (e.g., "com.myapp..*" for recursive)
        
        // Placeholder implementation
        if (pattern.endsWith("..*")) {
            String basePattern = pattern.substring(0, pattern.length() - 3);
            return packageName.startsWith(basePattern);
        }
        
        if (pattern.endsWith(".*")) {
            String basePattern = pattern.substring(0, pattern.length() - 2);
            return packageName.startsWith(basePattern) && 
                   !packageName.substring(basePattern.length()).contains(".");
        }
        
        return packageName.equals(pattern);
    }
}
