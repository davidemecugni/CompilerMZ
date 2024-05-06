package org.compiler.token.tokens;

import org.compiler.token.TokenType;

/**
 * TokenString represents a string literal token.
 *
 */

public class TokenString extends Token {

    private final String content;

    public TokenString(String content, int line, int column_start, int column_end) {
        super(TokenType.string_lit, line, column_start, column_end);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "TokenString{" + "value='" + content + '\'' + ", l=" + getLine() + ", col_s=" + getColumnStart()
                + ", col_e=" + getColumnEnd() + '}';
    }
}
