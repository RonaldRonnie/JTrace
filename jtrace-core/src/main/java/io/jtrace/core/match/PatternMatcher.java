package io.jtrace.core.match;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Matches package names against patterns (glob, regex, etc.).
 */
public class PatternMatcher {
    
    /**
     * Matches a package name against a pattern.
     * Supports glob patterns, regex patterns, and wildcard patterns.
     */
    public boolean matches(String pattern, String packageName) {
        if (pattern == null || packageName == null) {
            return false;
        }
        
        // Check if it's a regex pattern (starts with ^ or ends with $)
        if (pattern.startsWith("^") || pattern.endsWith("$")) {
            return matchesRegex(pattern, packageName);
        }
        
        // Check if it's a glob pattern (contains * or ?)
        if (pattern.contains("*") || pattern.contains("?")) {
            return matchesGlob(pattern, packageName);
        }
        
        // Check if it's a recursive wildcard pattern (ends with ..*)
        if (pattern.endsWith("..*")) {
            String basePattern = pattern.substring(0, pattern.length() - 3);
            return packageName.startsWith(basePattern);
        }
        
        // Check if it's a single-level wildcard pattern (ends with .*)
        if (pattern.endsWith(".*")) {
            String basePattern = pattern.substring(0, pattern.length() - 2);
            return packageName.startsWith(basePattern) && 
                   !packageName.substring(basePattern.length()).contains(".");
        }
        
        // Exact match
        return packageName.equals(pattern);
    }
    
    private boolean matchesRegex(String regexPattern, String packageName) {
        try {
            Pattern pattern = Pattern.compile(regexPattern);
            return pattern.matcher(packageName).matches();
        } catch (PatternSyntaxException e) {
            // If regex is invalid, fall back to exact match
            return packageName.equals(regexPattern);
        }
    }
    
    private boolean matchesGlob(String globPattern, String packageName) {
        // Convert glob pattern to regex
        String regexPattern = globToRegex(globPattern);
        return matchesRegex(regexPattern, packageName);
    }
    
    private String globToRegex(String globPattern) {
        StringBuilder regex = new StringBuilder("^");
        
        for (int i = 0; i < globPattern.length(); i++) {
            char c = globPattern.charAt(i);
            switch (c) {
                case '*':
                    // * matches any sequence of characters except dots
                    regex.append("[^.]*");
                    break;
                case '?':
                    // ? matches any single character except dots
                    regex.append("[^.]");
                    break;
                case '.':
                    // Escape dots in package names
                    regex.append("\\.");
                    break;
                case '[':
                    // Handle character classes
                    regex.append('[');
                    i++; // Skip the opening [
                    while (i < globPattern.length() && globPattern.charAt(i) != ']') {
                        regex.append(globPattern.charAt(i));
                        i++;
                    }
                    if (i < globPattern.length()) {
                        regex.append(']');
                    }
                    break;
                case '\\':
                    // Escape backslashes
                    regex.append("\\\\");
                    break;
                default:
                    // Escape other special regex characters
                    if ("(){}|+^$".indexOf(c) >= 0) {
                        regex.append('\\');
                    }
                    regex.append(c);
                    break;
            }
        }
        
        regex.append('$');
        return regex.toString();
    }
    
    /**
     * Checks if a package name matches any of the given patterns.
     */
    public boolean matchesAny(List<String> patterns, String packageName) {
        return patterns.stream().anyMatch(pattern -> matches(pattern, packageName));
    }
    
    /**
     * Checks if a package name matches all of the given patterns.
     */
    public boolean matchesAll(List<String> patterns, String packageName) {
        return patterns.stream().allMatch(pattern -> matches(pattern, packageName));
    }
}
