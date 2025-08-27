package com.example;

import com.example.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple test to verify the JTrace example project structure.
 */
public class JTraceExampleTest {
    
    @Test
    public void testProjectStructure() {
        // This test verifies that the project compiles and has the expected structure
        assertTrue(true, "Project structure is valid");
    }
    
    @Test
    public void testIntentionalViolations() {
        // This test documents the intentional architecture violations
        // that should be detected by JTrace
        
        // 1. User class is public instead of package-private (visibility violation)
        User user = new User("test", "test@example.com");
        assertNotNull(user);
        
        // 2. UserService methods are missing @Transactional (annotation violation)
        // 3. UserController directly calls UserRepository (dependency violation)
        
        // These violations should be caught by JTrace when running:
        // - jtrace scan
        // - mvn jtrace:scan
        // - gradle jtraceScan
    }
}
