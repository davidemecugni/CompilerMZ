package org.compiler.token.tokens;

import org.compiler.errors.TokenError;
import org.compiler.token.TokenType;

import java.util.Objects;

/**
 * TokenIntLit represents an integer literal token.
 */

public class TokenIntLit extends Token {
    private final long value;

    public TokenIntLit(String value, int line, int column_start, int column_end) throws TokenError {
        super(TokenType.int_lit, line, column_start, column_end);
        try {
            this.value = Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new TokenError("Invalid integer literal: " + value, line, column_start, column_end);
        }
    }

    public TokenIntLit(String value) {
        super(TokenType.int_lit);
        this.value = Integer.parseInt(value);
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TokenIntLit{" + "value='" + value + '\'' + ", l=" + getLine() + ", col_s=" + getColumnStart()
                + ", col_e=" + getColumnEnd() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        TokenIntLit intLit = (TokenIntLit) o;
        return value == intLit.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
