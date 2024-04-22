package org.compiler.token.tokens;

import org.compiler.token.TokenType;

/**
 * Represents a token in the source code A token is a pair consisting of a token name and an optional token value The
 * token name is an enumeration of the possible types of tokens The token value is the actual value of the token in the
 * source code(ex. int_lit -> 42)
 */
public class Token {
    private final TokenType type;
    private final int precedence;
    private final int line;
    private final int column;

    public Token(TokenType type, int line, int column) {
        this.type = type;
        this.precedence = BinaryPrecedence(type);
        this.line = line;
        this.column = column;
    }

    public Token(TokenType type) {
        this.type = type;
        this.precedence = BinaryPrecedence(type);
        this.line = -1;
        this.column = -1;
    }

    public TokenType getType() {
        return type;
    }

    public int BinaryPrecedence(TokenType type) {
        switch (type) {
        case TokenType.plus, TokenType.minus -> {
            return 0;
        }
        case TokenType.star, TokenType.slash -> {
            return 1;
        }
        }

        return -1;
    }

    public int getPrecedence() {
        return precedence;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return "Token{" + "type=" + type + ", l=" + line + ", col=" + column + '}';
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
