package org.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a token in the source code
 * A token is a pair consisting of a token name and an optional token value
 * The token name is an enumeration of the possible types of tokens
 * The token value is the actual value of the token in the source code
 */
public class Token {
    private final TokenType type;
    private String value=null;
    private static final Map<Object, TokenType> wordToTokenMap;
    static {
        wordToTokenMap = new HashMap<>();
        wordToTokenMap.put("exit", TokenType._exit);
        wordToTokenMap.put(';', TokenType.semi);
        // Add more entries as needed
    }
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
    public static Token of(Object word){
        if(wordToTokenMap.containsKey(word)){
            return new Token(wordToTokenMap.get(word));
        }
        throw new IllegalArgumentException("Illegal alphabetic token not found in the map");
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
