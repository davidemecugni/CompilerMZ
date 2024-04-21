package org.compiler.token.tokens;

import java.util.Objects;

public class CharLineColumn {
    private final char c;
    private final int line;
    private final int column;

    public CharLineColumn(char c, int line, int column) {
        this.c = c;
        this.line = line;
        this.column = column;
    }

    /**
     * Used in tests
     *
     * @param c
     *            Char to be saved
     */
    public CharLineColumn(char c) {
        this.c = c;
        this.line = -1;
        this.column = -1;
    }

    public char getChar() {
        return c;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        CharLineColumn that = (CharLineColumn) o;
        return c == that.c;
    }

    @Override
    public int hashCode() {
        return Objects.hash(c);
    }

    @Override
    public String toString() {
        return "CharLineColumn{" + "c=" + c + ", line=" + line + ", column=" + column + '}';
    }
}
