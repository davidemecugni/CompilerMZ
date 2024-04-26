package org.compiler.errors;

public class TokenError extends Exception {
    private final int line;
    private final int columnStart;
    private final int columnEnd;

    public TokenError(String message, int line, int columnStart, int columnEnd) {
        super(message);
        this.line = line;
        this.columnStart = columnStart;
        this.columnEnd = columnEnd;
    }

    @Override
    public String toString() {
        return getMessage() + " {" + "line=" + line + ", columnStart=" + columnStart + ", columnEnd=" + columnEnd + '}';
    }
}
