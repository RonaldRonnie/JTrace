package io.jtrace.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskProvider;

/**
 * Gradle plugin for JTrace architecture enforcement.
 */
public class JTracePlugin implements Plugin<Project> {
    
    @Override
    public void apply(Project project) {
        // Create the JTrace extension
        JTraceExtension extension = project.getExtensions()
            .create("jtrace", JTraceExtension.class);
        
        // Create tasks
        TaskProvider<JTraceScanTask> scanTask = project.getTasks()
            .register("jtraceScan", JTraceScanTask.class, task -> {
                task.setGroup("verification");
                task.setDescription("Scans source code for architecture violations");
            });
        
        TaskProvider<JTraceEnforceTask> enforceTask = project.getTasks()
            .register("jtraceEnforce", JTraceEnforceTask.class, task -> {
                task.setGroup("verification");
                task.setDescription("Enforces architecture rules and fails on violations");
            });
        
        TaskProvider<JTraceReportTask> reportTask = project.getTasks()
            .register("jtraceReport", JTraceReportTask.class, task -> {
                task.setGroup("reporting");
                task.setDescription("Generates architecture violation reports");
            });
        
        // Wire into check lifecycle
        project.getTasks().named("check").configure(check -> {
            check.dependsOn(enforceTask);
        });
        
        // Wire into build lifecycle
        project.getTasks().named("build").configure(build -> {
            build.dependsOn(scanTask);
        });
    }
}
