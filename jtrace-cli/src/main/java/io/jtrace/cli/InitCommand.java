package io.jtrace.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Command to initialize a new JTrace configuration file.
 */
@Command(
    name = "init",
    description = "Initialize a new JTrace configuration file"
)
public class InitCommand implements Runnable {

    @Option(names = {"--output", "-o"}, description = "Output file path", defaultValue = "jtrace.yml")
    private String outputFile;

    @Override
    public void run() {
        try {
            Path outputPath = Paths.get(outputFile);
            
            // Generate starter configuration
            String config = generateStarterConfig();
            
            // Write to file
            Files.write(outputPath, config.getBytes());
            
            System.out.println("✓ Created " + outputPath.toAbsolutePath());
            System.out.println("✓ Edit the configuration file to customize your architecture rules");
            System.out.println("✓ Run 'jtrace scan' to analyze your project");
            
        } catch (IOException e) {
            System.err.println("Error creating configuration file: " + e.getMessage());
            System.exit(1);
        }
    }

    private String generateStarterConfig() {
        return """
            version: 1
            basePackage: "com.myapp"
            failOn:
              severity: "error"
            
            rules:
              # Rule 1: Controllers must not access repositories directly
              - id: layering-no-controller-to-repository
                type: forbiddenDependency
                from: "com.myapp.controller..*"
                to: "com.myapp.repository..*"
                severity: error
                message: "Controllers must not access repositories directly. Use service layer instead."
            
              # Rule 2: Service methods must be transactional
              - id: service-methods-transactional
                type: requireAnnotation
                in: "com.myapp.service..*"
                target: method
                annotation: "org.springframework.transaction.annotation.Transactional"
                severity: warning
                message: "Service methods should be annotated with @Transactional for proper transaction management."
            
              # Rule 3: Domain classes should be package-private
              - id: visibility-domain-classes
                type: visibility
                target: class
                in: "com.myapp.domain..*"
                mustBe: "package-private"
                severity: warning
                message: "Domain classes should be package-private to enforce encapsulation."
            
              # Rule 4: Layering architecture
              - id: layering
                type: layering
                layers:
                  - name: controller
                    packages: ["com.myapp.controller..*"]
                  - name: service
                    packages: ["com.myapp.service..*"]
                  - name: repository
                    packages: ["com.myapp.repository..*"]
                  - name: domain
                    packages: ["com.myapp.domain..*"]
                allowedDependencies:
                  - from: controller
                    to: service
                  - from: service
                    to: repository
                  - from: service
                    to: domain
                  - from: repository
                    to: domain
                forbidCycles: true
                severity: error
                message: "Layering architecture must be respected. Controllers -> Services -> Repositories -> Domain."
            """;
    }
}
