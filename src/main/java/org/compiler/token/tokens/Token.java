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

    public Token(TokenType type) {
        this.type = type;
        this.precedence = BinaryPrecedence(type);
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

    @Override
    public String toString() {
        return "Token{" + "type=" + type + '}';
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
