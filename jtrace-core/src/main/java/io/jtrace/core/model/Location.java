package io.jtrace.core.model;

import java.nio.file.Path;

/**
 * Represents the location of a rule violation in the source code.
 */
public class Location {
    private final Path file;
    private final int line;
    private final int column;
    private final String symbol;

    public Location(Path file, int line, int column, String symbol) {
        this.file = file;
        this.line = line;
        this.column = column;
        this.symbol = symbol;
    }

    public Location(Path file, int line, String symbol) {
        this(file, line, -1, symbol);
    }

    public Path getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        if (column > 0) {
            return file + ":" + line + ":" + column + " (" + symbol + ")";
        }
        return file + ":" + line + " (" + symbol + ")";
    }
}
