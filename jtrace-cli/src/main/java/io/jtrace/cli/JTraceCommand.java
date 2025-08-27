package io.jtrace.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Main CLI command for JTrace.
 */
@Command(
    name = "jtrace",
    mixinStandardHelpOptions = true,
    version = "JTrace 0.1.0-SNAPSHOT",
    description = "Live Architecture Enforcer for Java Applications",
    subcommands = {
        InitCommand.class,
        ScanCommand.class,
        EnforceCommand.class,
        ReportCommand.class
    }
)
public class JTraceCommand implements Runnable {

    @Override
    public void run() {
        // Show help if no subcommand is provided
        System.out.println("JTrace - Live Architecture Enforcer");
        System.out.println("Use --help to see available commands");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new JTraceCommand()).execute(args);
        System.exit(exitCode);
    }
}
