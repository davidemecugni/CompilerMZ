package org.compiler.token.tokens;

import org.compiler.token.TokenType;

import java.util.Map;

/**
 * Represents a token in the source code A token is a pair consisting of a token name and an optional token value The
 * token name is an enumeration of the possible types of tokens The token value is the actual value of the token in the
 * source code(ex. int_lit -> 42)
 */
public class Token {
    private final TokenType type;
    private final int precedence;
    private final int line;
    private final int column_start;
    private final int column_end;
    private static final Map<TokenType, Integer> PRECEDENCE_MAP = Map.ofEntries(Map.entry(TokenType.plus, 0),
            Map.entry(TokenType.minus, 0), Map.entry(TokenType.star, 1), Map.entry(TokenType.slash, 1),
            Map.entry(TokenType.percent, 1), Map.entry(TokenType.logic_not_eq, 1), Map.entry(TokenType.logic_eq, 1),
            Map.entry(TokenType.logic_gt, 1), Map.entry(TokenType.logic_ge, 1), Map.entry(TokenType.logic_lt, 1),
            Map.entry(TokenType.logic_le, 1), Map.entry(TokenType.logic_and, 1), Map.entry(TokenType.logic_or, 1));

    public Token(TokenType type, int line, int column_start, int column_end) {
        this.type = type;
        this.precedence = BinaryPrecedence(type);
        this.line = line;
        this.column_start = column_start;
        this.column_end = column_end;
    }

    public Token(TokenType type) {
        this.type = type;
        this.precedence = BinaryPrecedence(type);
        this.line = -1;
        this.column_start = -1;
        this.column_end = -1;
    }

    public TokenType getType() {
        return type;
    }

    public int BinaryPrecedence(TokenType type) {
        return PRECEDENCE_MAP.getOrDefault(type, -1);
    }

    public int getPrecedence() {
        return precedence;
    }

    public int getLine() {
        return line;
    }

    public int getColumnStart() {
        return column_start;
    }

    public int getColumnEnd() {
        return column_end;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + ", l=" + line + ", col_s=" + column_start + ", col_e=" + column_end + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Token token = (Token) obj;
        return type == token.type;
    }
}
