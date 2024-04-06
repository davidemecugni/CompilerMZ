package org.compiler;

/**
 * Represents a token in the source code
 * A token is a pair consisting of a token name and an optional token value
 * The token name is an enumeration of the possible types of tokens
 * The token value is the actual value of the token in the source code
 */
public class Token {
    private final TokenType type;
    private String value=null;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(TokenType type) {
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean hasValue(){
        return value != null;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
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
        if(value == null){
            return type == token.type && token.value == null;
        }
        return type == token.type && value.equals(token.value);
    }
}
