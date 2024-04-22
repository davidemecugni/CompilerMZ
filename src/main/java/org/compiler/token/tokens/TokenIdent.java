package org.compiler.token.tokens;

import org.compiler.token.TokenType;

public class TokenIdent extends Token {
    private final String name;

    public TokenIdent(String name, int line, int column_start, int column_end) {
        super(TokenType.ident, line, column_start, column_end);
        this.name = name;
    }

    public TokenIdent(String name) {
        super(TokenType.ident);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "TokenIdent{" + "name='" + name + '\'' + ", l=" + getLine() + ", col_s=" + getColumnStart() + ", col_e="
                + getColumnEnd() + '}';
    }
}
